package game.level;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import game.Game;
import game.entity.Entity;
import game.entity.mob.Mob;
import game.entity.mob.player.Player;
import game.entity.mob.Slime;
import game.entity.particle.Particle;
import game.entity.projectile.Projectile;
import game.graphics.Screen;
import game.level.tile.Tile;
import game.network.NetworkPackage;
import game.util.GameState;
import game.util.Hitbox;
import game.util.Node;
import game.util.TileCoordinate;
import game.util.Timer;
import game.util.Vector2i;

public class Level
{
	protected static int width, height;
	protected static int[] tiles; //Contains pixel colors from level file that is currently loaded
	private final static int TILE_SIZE_SHIFTING = Screen.TILE_SIZE_SHIFTING;
	private TileCoordinate playerSpawn = null;
	private static String levelName = "";

	private Random rand = new Random();

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private List<Particle> particles = new ArrayList<Particle>();
	private List<Player> players = new ArrayList<Player>();

	private Comparator<Node> nodeSorter = new Comparator<Node>()
	{
		public int compare(Node n0, Node n1)
		{
			if(n1.fCost < n0.fCost) return +1; //If so, move it up in the index
			if(n1.fCost > n0.fCost) return -1; //If this is the case, move it down
			return 0;
		}
	};

	public static byte currentLevelID = 0;

	public Level(TileCoordinate playerSpawn)
	{
		this.playerSpawn = playerSpawn;
	}

	public static void loadLevel(GameLevel level)
	{
		level.loadLevel(level.getPath());
		Level.levelName = level.getLevelName();
		Timer.reset();
		Timer.start();
	}

	public void unloadLevel()
	{
		width = 0;
		height = 0;
		tiles = new int[0];
		levelName = "";

		playerSpawn = null;

		entities.clear();
		projectiles.clear();
		particles.clear();
		players.clear();
	}

	protected void generateLevel()
	{

	}

	public void tick()
	{
		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(Game.getGameStateTicksPassed() % 400 == 180)
			{
				int xSpawn, ySpawn;
				for(int i = 0; i < 2; i++)
				{
					while(true)
					{
						xSpawn = rand.nextInt(width * Tile.DEFAULT_TILE_SIZE);
						ySpawn = rand.nextInt(height * Tile.DEFAULT_TILE_SIZE);
						
						if(!new Slime(0, 0, 0.75F).collision(xSpawn, ySpawn))
						{
							this.add(new Slime(xSpawn, ySpawn, 0.75F));
							break;
						}
					}
				}
			}
		}

		removeKilledMobs();
		removeRemovedEntities();

		for(int i = 0; i < entities.size(); i++)
		{
			entities.get(i).tick();
		}
		for(int i = 0; i < projectiles.size(); i++)
		{
			projectiles.get(i).tick();
		}
		for(int i = 0; i < particles.size(); i++)
		{
			particles.get(i).tick();
		}
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).tick();
			//players.get(i).motion(0.5f, 0f);
		}
	}

	private void removeKilledMobs()
	{
		//Players are getting handeld diffrently
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isDead()) players.get(i).whileDead();
		}
	}

	private void removeRemovedEntities()
	{
		for(int i = 0; i < entities.size(); i++)
		{
			if(entities.get(i).isRemoved()) entities.remove(i);
		}
		for(int i = 0; i < projectiles.size(); i++)
		{
			if(projectiles.get(i).isRemoved()) projectiles.remove(i);
		}
		for(int i = 0; i < particles.size(); i++)
		{
			if(particles.get(i).isRemoved()) particles.remove(i);
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isRemoved()) players.remove(i);
		}
	}

	float x = 1;

	public void render(int xScroll, int yScroll, Screen screen)
	{
		screen.setOffset(xScroll, yScroll);

		renderVisibleTiles(xScroll, yScroll, screen);

		for(int i = 0; i < particles.size(); i++)
		{
			particles.get(i).render(screen);
		}
		for(int i = 0; i < projectiles.size(); i++)
		{
			projectiles.get(i).render(screen);
		}
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).render(screen);
		}
		for(int i = 0; i < entities.size(); i++)
		{
			entities.get(i).render(screen);
		}
	}

	@SuppressWarnings("unused")
	private void time()
	{

	}

	public boolean tileCollision(int x, int y, int size, int xOffset, int yOffset)
	{
		for(int corner = 0; corner < 4; corner++)
		{
			//Transforms pixel into tile precision and "asks" the appropriate tile, if it's solid
			double xt = (x - corner % 2 * size + xOffset) >> TILE_SIZE_SHIFTING; //With values after corner % 2 or corner / 2, it's possible
			double yt = (y - corner / 2 * size + yOffset) >> TILE_SIZE_SHIFTING; //to modify the position and size of the hitbox
			if(getTile((int) (xt), (int) (yt)).solid()) return true;
		}

		return false;
	}

	public Player playerCollidedWithMob(Mob mob)
	{
		for(int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if(p.isDead()) continue;
			Hitbox mHitbox = mob.getHitbox();

			for(int corner = 0; corner < 4; corner++)
			{
				int playerX = p.getX() + p.getHitbox().getXOffset() + p.getHitbox().getWidth() * (corner % 2);
				int playerY = p.getY() + p.getHitbox().getYOffset() + p.getHitbox().getHeight() * (corner / 2);

				if(playerX >= mob.getX() + mHitbox.getXOffset() && playerX <= mob.getX() + mHitbox.getXOffset() + mHitbox.getWidth()
						&& playerY >= mob.getY() + mHitbox.getYOffset() && playerY <= mob.getY() + mHitbox.getYOffset() + mHitbox.getHeight())
					return p;
			}
		}
		return null;
	}

	private void renderVisibleTiles(int xScroll, int yScroll, Screen screen)
	{
		//Corner pins
		int x0 = xScroll >> TILE_SIZE_SHIFTING; //xScroll >> TILE_SIZE to transform from pixel precision to tile precision
		int x1 = (xScroll + screen.width + Tile.DEFAULT_TILE_SIZE) >> TILE_SIZE_SHIFTING;
		int y0 = yScroll >> TILE_SIZE_SHIFTING;
		int y1 = (yScroll + screen.height + Tile.DEFAULT_TILE_SIZE) >> TILE_SIZE_SHIFTING;

		for(int y = y0; y < y1; y++)
		{
			for(int x = x0; x < x1; x++)
			{
				getTile(x, y).render(x, y, screen); //Renders appropriate tile at the appropriate position
			}
		}
	}

	public void renderHitboxes(Graphics g)
	{
		g.setColor(Color.BLUE);
		for(int i = 0; i < entities.size(); i++)
		{
			if(entities.get(i) instanceof Mob)
			{
				Hitbox hitbox = ((Mob) entities.get(i)).getHitbox();
				g.drawRect(Game.SCALE * (entities.get(i).getX() - Screen.getXOffset() + hitbox.getXOffset()),
						Game.SCALE * (entities.get(i).getY() - Screen.getYOffset() + hitbox.getYOffset()), (hitbox.getWidth() + 1) * Game.SCALE,
						(hitbox.getHeight() + 1) * Game.SCALE);
			}
		}
		for(int i = 0; i < projectiles.size(); i++)
		{
			Hitbox hitbox = projectiles.get(i).getHitbox();
			g.drawRect(Game.SCALE * (projectiles.get(i).getX() - Screen.getXOffset() + hitbox.getXOffset()),
					Game.SCALE * (projectiles.get(i).getY() - Screen.getYOffset() + hitbox.getYOffset()), (hitbox.getWidth() + 1) * Game.SCALE,
					(hitbox.getHeight() + 1) * Game.SCALE);
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isDead()) continue;
			Hitbox hitbox = players.get(i).getHitbox();
			g.drawRect(Game.SCALE * (players.get(i).getX() - Screen.getXOffset() + hitbox.getXOffset()),
					Game.SCALE * (players.get(i).getY() - Screen.getYOffset() + hitbox.getYOffset()), (hitbox.getWidth() + 1) * Game.SCALE,
					(hitbox.getHeight() + 1) * Game.SCALE);
		}
	}

	public void add(Entity e)
	{
		e.init(this);
		if(e instanceof Particle) particles.add((Particle) e);
		else if(e instanceof Projectile)
		{
			projectiles.add((Projectile) e);
			if(((Projectile) e).getSource() == getClientPlayer() && Game.getGameState() == GameState.IngameOnline)
				NetworkPackage.shoot(((Projectile) e));
		}
		else if(e instanceof Player) players.add((Player) e);
		else entities.add(e);
	}

	public void remove(Entity e)
	{
		e.remove();
	}

	public List<Entity> getAllEntities()
	{
		List<Entity> entities = new ArrayList<Entity>();

		for(Player player : players)
			entities.add(player);
		for(Particle particle : particles)
			entities.add(particle);
		for(Projectile projectile : projectiles)
			entities.add(projectile);
		for(Entity entity : this.entities)
			entities.add(entity);

		return entities;
	}

	public List<Mob> getMobs()
	{
		List<Mob> mobs = new ArrayList<Mob>();

		for(Player player : players)
			mobs.add(player);
		for(Entity entity : entities)
			if(entity instanceof Mob) mobs.add((Mob) entity);

		return mobs;
	}

	public List<Player> getPlayers()
	{
		return players;
	}

	public Player getPlayer(String IPAddress)
	{
		if(IPAddress == null) return null;

		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getIPAddress() == null) continue;
			if(players.get(i).getIPAddress().equals(IPAddress)) return players.get(i);
		}

		return null;
	}

	public Player getPlayer(UUID uuid)
	{
		if(uuid == null) return null;

		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getUUID().compareTo(uuid) == 0) return players.get(i);
		}

		return null;
	}

	public Player getClientPlayer()
	{
		if(players.size() > 0) return players.get(0);
		return null;
	}

	public List<Projectile> getProjectiles()
	{
		return projectiles;
	}

	public Projectile getProjectile(UUID uuid)
	{
		if(uuid == null) return null;

		for(int i = 0; i < projectiles.size(); i++)
		{
			if(projectiles.get(i).getUUID().compareTo(uuid) == 0) return projectiles.get(i);
		}

		return null;
	}

	public List<Node> findPath(Vector2i start, Vector2i end)
	{
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, getDistance(start, end));
		openList.add(current);

		while(openList.size() > 0)
		{
			Collections.sort(openList, nodeSorter); //Sorts nodes by cost, lowest cost is on top, highest on bottom
			current = openList.get(0); //Current is now the node with the lowest cost
			if(current.tile.equals(end))
			{
				ArrayList<Node> path = new ArrayList<Node>();
				//Will be looping throught till the start, because the start's parent is null
				while(current.parent != null)
				{
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}
			openList.remove(current); //Move lowest cost node to the closed list
			closedList.add(current);
			for(int i = 0; i < 9; i++)
			{
				if(i == 4) continue;
				int x = current.tile.getX();
				int y = current.tile.getY();
				int xDir = (i % 3) - 1;
				int yDir = (i / 3) - 1;
				Tile at = getTile(x + xDir, y + yDir);
				if(at == null) continue;
				if(at.solid()) continue;
				if(at.deadly()) continue;
				Vector2i a = new Vector2i(x + xDir, y + yDir);
				double gCost = current.gCost + getDistance(current.tile, a);
				double hCost = getDistance(a, end);
				Node node = new Node(a, current, gCost, hCost);
				if(vectorInList(closedList, a) && gCost >= node.gCost) continue;
				if(!vectorInList(openList, a) || gCost < node.gCost) openList.add(node);
			}
		}

		closedList.clear();
		return null;
	}

	private boolean vectorInList(List<Node> list, Vector2i vector)
	{
		for(Node n : list)
		{
			if(n.tile.equals(vector)) return true;
		}
		return false;
	}

	private double getDistance(Vector2i vector1, Vector2i vector2)
	{
		double deltaX = vector1.getX() - vector2.getX();
		double deltaY = vector1.getY() - vector2.getY();
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		return distance;
	}

	//If randomLevel(); used in Game.java, change if(tiles[x + y * width] == 0xFFFFFFFF) return Tile.grassTile;
	//to ---> if(tilesInt[x + y * width] == 0) return Tile.grassTile;
	public static Tile getTile(int x, int y)
	{
		//If out of bounds, return void tile
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.voidTile;
		//Tiles: If color in level file at specific location is e.g. equal to grass, then return a grass tile
		if(tiles[x + y * width] == Tile.colBoosterTile) return Tile.boosterTile;
		if(tiles[x + y * width] == Tile.colCheckpointTile) return Tile.checkpointTile;
		if(tiles[x + y * width] == Tile.colIceTile) return Tile.iceTile;
		if(tiles[x + y * width] == Tile.colKillerTile) return Tile.killerTile;
		if(tiles[x + y * width] == Tile.colVoidTile) return Tile.voidTile;
		if(tiles[x + y * width] == Tile.colQuartzTile) return Tile.quartzTile;
		if(tiles[x + y * width] == Tile.colQuartzWallTile) return Tile.quartzWallTile;

		if(tiles[x + y * width] == Tile.colBlockTile) return Tile.blockTile;
		if(tiles[x + y * width] == Tile.colDirtTile) return Tile.dirtTile;
		if(tiles[x + y * width] == Tile.colGrassTile) return Tile.grassTile;
		if(tiles[x + y * width] == Tile.colSandTile) return Tile.sandTile;

		//Water
		if(tiles[x + y * width] == Tile.colWaterTile)
		{
			if(x % 2 == 0 && y % 2 == 0) return Tile.waterTile0;
			else if(x % 2 == 1 && y % 2 == 0) return Tile.waterTile1;
			else if(x % 2 == 0 && y % 2 == 1) return Tile.waterTile2;
			return Tile.waterTile3;
		}

		//Unknown color in level file:
		return Tile.errorTile;
	}

	public void finishedLevel()
	{
		currentLevelID++;
		Game.setGameState(GameState.LevelFinished);
	}

	public int getLevelWidth()
	{
		return width;
	}

	public int getLevelHeight()
	{
		return height;
	}

	public String getLevelName()
	{
		return levelName;
	}

	public TileCoordinate getSpawnLocation()
	{
		return playerSpawn;
	}

	public void setTile(int tileColor, int x, int y)
	{
		tiles[(x >> TILE_SIZE_SHIFTING) + (y >> TILE_SIZE_SHIFTING) * width] = tileColor;
	}
}