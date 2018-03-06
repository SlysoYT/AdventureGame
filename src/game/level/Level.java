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
import game.entity.mob.Slime;
import game.entity.mob.player.Player;
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

	public static void generateLevel(GameLevel level)
	{
		level.loadLevel(level.getSeed());
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
		particles.clear();
		players.clear();
	}

	public void tick()
	{
		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(Game.getGameStateTicksPassed() % 400 == 200)
			{
				for(int i = 0; i < 2; i++)
					this.add(new Slime(rand.nextInt(width * Tile.DEFAULT_TILE_SIZE), rand.nextInt(height * Tile.DEFAULT_TILE_SIZE), 0.75F));
			}
		}

		removeKilledMobs();
		removeRemovedEntities();

		for(int i = 0; i < entities.size(); i++)
		{
			entities.get(i).tick();
		}
		for(int i = 0; i < particles.size(); i++)
		{
			particles.get(i).tick();
		}
		for(int i = 0; i < players.size(); i++)
		{
			players.get(i).tick();
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
		for(int i = 0; i < particles.size(); i++)
		{
			if(particles.get(i).isRemoved()) particles.remove(i);
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isRemoved()) players.remove(i);
		}
	}

	/**
	 * Rendering level with the client player in the center with smooth
	 * scrolling
	 * 
	 * @param screen
	 */
	public void render(Screen screen)
	{
		int deltaX = getClientPlayer().getX() - Game.width / 2 - Screen.getXOffset();
		int deltaY = getClientPlayer().getY() - Game.height / 2 - Screen.getYOffset();
		int xScroll = Screen.getXOffset() + (int) (deltaX * 0.02F);
		int yScroll = Screen.getYOffset() + (int) (deltaY * 0.02F);

		screen.setOffset(xScroll, yScroll);

		renderVisibleTiles(xScroll, yScroll, screen);
		renderVisibleEntities(xScroll, yScroll, screen);
	}

	/**
	 * Rendering level at defined location
	 * 
	 * @param xScroll
	 * @param yScroll
	 * @param screen
	 */
	public void render(int xScroll, int yScroll, Screen screen)
	{
		screen.setOffset(xScroll, yScroll);

		renderVisibleTiles(xScroll, yScroll, screen);
		renderVisibleEntities(xScroll, yScroll, screen);
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

	private void renderVisibleEntities(int xScroll, int yScroll, Screen screen)
	{
		//Corner pins
		int x0 = xScroll - Tile.DEFAULT_TILE_SIZE;
		int x1 = xScroll + screen.width + Tile.DEFAULT_TILE_SIZE;
		int y0 = yScroll - Tile.DEFAULT_TILE_SIZE;
		int y1 = yScroll + screen.height + Tile.DEFAULT_TILE_SIZE;

		for(int i = 0; i < particles.size(); i++)
		{
			if(particles.get(i).getX() > x0 && particles.get(i).getX() < x1 && particles.get(i).getY() > y0 && particles.get(i).getY() < y1)
				particles.get(i).render(screen);
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getX() > x0 && players.get(i).getX() < x1 && players.get(i).getY() > y0 && players.get(i).getY() < y1)
				players.get(i).render(screen);
		}
		for(int i = 0; i < entities.size(); i++)
		{
			if(entities.get(i).getX() > x0 && entities.get(i).getX() < x1 && entities.get(i).getY() > y0 && entities.get(i).getY() < y1)
				entities.get(i).render(screen);
		}
	}

	public void renderHitboxes(Graphics g)
	{
		g.setColor(Color.BLUE);

		List<Entity> allEntities = getAllEntities();

		for(int i = 0; i < allEntities.size(); i++)
		{
			Hitbox hitbox = allEntities.get(i).getHitbox();
			if(hitbox == null) continue;
			g.drawRect(Game.SCALE * (allEntities.get(i).getX() - Screen.getXOffset() + hitbox.getXOffset()),
					Game.SCALE * (allEntities.get(i).getY() - Screen.getYOffset() + hitbox.getYOffset()), (hitbox.getWidth() + 1) * Game.SCALE,
					(hitbox.getHeight() + 1) * Game.SCALE);
		}
	}

	@SuppressWarnings("unused")
	private void time()
	{

	}

	public boolean hitboxCollidesWithSolidTile(int x, int y, Hitbox hitbox)
	{
		for(int currentX = x + hitbox.getXOffset() >> TILE_SIZE_SHIFTING; currentX <= x + hitbox.getWidth()
				+ hitbox.getXOffset() >> TILE_SIZE_SHIFTING; currentX++)
		{
			for(int currentY = y + hitbox.getYOffset() >> TILE_SIZE_SHIFTING; currentY <= y + hitbox.getHeight()
					+ hitbox.getYOffset() >> TILE_SIZE_SHIFTING; currentY++)
			{
				Tile currentTile = getTile(currentX, currentY);
				if(currentTile.solid()) return true;
				if(currentTile.getHitbox() != null)
				{
					if(hitboxCollidesWithHitbox(x, y, hitbox, currentX * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getXOffset(),
							currentY * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getYOffset(), currentTile.getHitbox()))
						return true;
				}
			}
		}

		return false;
	}

	public Player anyPlayerCollidedWithHitbox(int x, int y, Hitbox hitbox)
	{
		for(int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if(p.isDead()) continue;
			if(hitboxCollidesWithHitbox(x, y, hitbox, p.getX(), p.getY(), p.getHitbox())) return p;
		}
		return null;
	}

	private boolean hitboxCollidesWithHitbox(int h0xPos, int h0yPos, Hitbox h0, int h1xPos, int h1yPos, Hitbox h1)
	{
		for(int currentX = h0xPos + h0.getXOffset(); currentX <= h0xPos + h0.getWidth() + h0.getXOffset(); currentX++)
		{
			for(int currentY = h0yPos + h0.getYOffset(); currentY <= h0yPos + h0.getHeight() + h0.getYOffset(); currentY++)
			{
				if(currentX >= h1xPos + h1.getXOffset() && currentX <= h1xPos + h1.getXOffset() + h1.getWidth()
						&& currentY >= h1yPos + h1.getYOffset() && currentY <= h1yPos + h1.getYOffset() + h1.getHeight())
					return true;
			}
		}

		return false;
	}

	public void add(Entity e)
	{
		e.init(this);
		if(e instanceof Particle) particles.add((Particle) e);
		else if(e instanceof Projectile)
		{
			entities.add((Projectile) e);
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
		List<Projectile> projectiles = new ArrayList<Projectile>();
		for(int i = 0; i < entities.size(); i++)
		{
			if((entities.get(i) instanceof Projectile)) projectiles.add((Projectile) entities.get(i));
		}
		return projectiles;
	}

	public Projectile getProjectile(UUID uuid)
	{
		if(uuid == null) return null;

		for(int i = 0; i < entities.size(); i++)
		{
			if(!(entities.get(i) instanceof Projectile)) continue;
			if(entities.get(i).getUUID().compareTo(uuid) == 0) return (Projectile) entities.get(i);
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

	public static Tile getTile(int x, int y)
	{
		//If out of bounds, return void tile
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.TILE_VOID;
		//Tiles: If color in level file at specific location is e.g. equal to grass, then return a grass tile
		if(tiles[x + y * width] == Tile.COL_TILE_DIRT) return Tile.TILE_DIRT;
		if(tiles[x + y * width] == Tile.COL_TILE_GRASS) return Tile.TILE_GRASS;
		if(tiles[x + y * width] == Tile.COL_TILE_FLOWER_0) return Tile.TILE_FLOWER_0;
		if(tiles[x + y * width] == Tile.COL_TILE_FLOWER_1) return Tile.TILE_FLOWER_1;
		if(tiles[x + y * width] == Tile.COL_TILE_FLOWER_2) return Tile.TILE_FLOWER_2;
		if(tiles[x + y * width] == Tile.COL_TILE_FLOWER_3) return Tile.TILE_FLOWER_3;
		if(tiles[x + y * width] == Tile.COL_TILE_ROCK_GRASS) return Tile.TILE_ROCK_GRASS;
		if(tiles[x + y * width] == Tile.COL_TILE_ROCK_SAND) return Tile.TILE_ROCK_SAND;
		if(tiles[x + y * width] == Tile.COL_TILE_SAND) return Tile.TILE_SAND;
		if(tiles[x + y * width] == Tile.COL_TILE_WATER) return Tile.TILE_WATER;

		if(tiles[x + y * width] == Tile.COL_TILE_BOOSTER) return Tile.TILE_BOOSTER;
		if(tiles[x + y * width] == Tile.COL_TILE_CHECKPOINT) return Tile.TILE_CHECKPOINT;
		if(tiles[x + y * width] == Tile.COL_TILE_ICE) return Tile.TILE_ICE;
		if(tiles[x + y * width] == Tile.COL_TILE_KILLER) return Tile.TILE_KILLER;
		if(tiles[x + y * width] == Tile.COL_TILE_VOID) return Tile.TILE_VOID;
		if(tiles[x + y * width] == Tile.COL_TILE_QUARTZ) return Tile.TILE_QUARTZ;
		if(tiles[x + y * width] == Tile.COL_TILE_QUARTZ_WALL) return Tile.TILE_QUARTZ_WALL;

		//Unknown color
		return Tile.TILE_ERROR;
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
