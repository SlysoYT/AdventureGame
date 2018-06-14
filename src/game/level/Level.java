/*******************************************************************************
 * Copyright (C) 2018 Thomas Zahner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import game.audio.PlayMusic;
import game.entity.Entity;
import game.entity.lighting.LightSource;
import game.entity.mob.Guardian;
import game.entity.mob.Mob;
import game.entity.mob.Salesman;
import game.entity.mob.Slime;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.entity.particle.Particle;
import game.entity.projectile.Projectile;
import game.graphics.Screen;
import game.level.tile.Tile;
import game.util.GameState;
import game.util.Hitbox;
import game.util.Node;
import game.util.TileCoordinate;
import game.util.Vector2i;

public class Level
{
	protected int width, height;
	protected int[] tiles; //Contains pixel colours from level file that is currently loaded
	protected TileCoordinate playerSpawn = null;

	private final int TILE_SIZE_SHIFTING = Game.getScreen().TILE_SIZE_SHIFTING;
	private String levelName = "";
	private GameLevel gameLevel;

	private Random rand = new Random();

	private final int MAX_TIME = 3600;
	private int time = MAX_TIME / 2;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Particle> particles = new ArrayList<Particle>();
	private List<Player> players = new ArrayList<Player>();
	private List<LightSource> lightSources = new ArrayList<LightSource>();

	private Comparator<Node> nodeSorter = new Comparator<Node>()
	{
		public int compare(Node n0, Node n1)
		{
			if(n1.fCost < n0.fCost) return +1; //If so, move it up in the index
			if(n1.fCost > n0.fCost) return -1; //If this is the case, move it down
			return 0;
		}
	};

	public void loadLevel(GameLevel level)
	{
		gameLevel = level;
		gameLevel.loadLevel(level.getPath());
		levelName = level.getLevelName();
	}

	public void generateLevel(GameLevel level)
	{
		gameLevel = level;
		gameLevel.loadLevel(level.getSeed());
		levelName = level.getLevelName();
	}

	public void unloadLevel()
	{
		width = 0;
		height = 0;
		tiles = new int[0];
		levelName = "";
		time = 0;

		playerSpawn = null;

		entities.clear();
		particles.clear();
		players.clear();
	}

	public void tick()
	{
		if(getClientPlayer() != null)
		{
			float deltaX = getClientPlayer().getX() - Game.width / 2 - Game.getScreen().getXOffsetFloat();
			float deltaY = getClientPlayer().getY() - Game.height / 2 - Game.getScreen().getYOffsetFloat();

			Game.getScreen().setCameraMotion(deltaX / 20, deltaY / 20);
		}

		if(Game.getGameState() == GameState.IngameOffline || (Game.getGameState() == GameState.IngameOnline && Game.isHostingGame))
		{
			mobSpawning();

			if(Game.getGameStateTicksPassed() == 0)
			{
				Game.getScreen().setOffset(getClientPlayer().getX() - Game.width, getClientPlayer().getY() - Game.height);
			}
		}

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
		for(int i = 0; i < lightSources.size(); i++)
		{
			lightSources.get(i).tick();
		}

		handleDeadAndRemovedEntities();
		time();
		PlayMusic.tick();
	}

	private void mobSpawning()
	{
		for(Player player : players)
		{
			if(player.isMoving())
			{
				if(player.getDistanceMoved() % 3 == 0 && rand.nextInt(80) == 0)
				{

					int xPos = player.getX() + (-800 * rand.nextInt(2)) + 400 + (rand.nextInt(50) - 25);
					int yPos = player.getY() + (-800 * rand.nextInt(2)) + 400 + (rand.nextInt(50) - 25);

					int randomMob = rand.nextInt(4);

					if(randomMob == 0) add(new Guardian(xPos, yPos, null));
					else if(randomMob == 1) add(new Slime(xPos, yPos, null));
					else if(randomMob == 2) add(new Salesman(xPos, yPos, null));
				}
			}
		}

		for(Entity entity : entities)
		{
			if(entity instanceof Player) continue;
			if(!(entity instanceof Mob)) continue;

			Player player = getNearestPlayer(entity);
			if(player == null) break;
			if(getDistance(new Vector2i(player.getX(), player.getY()), new Vector2i(entity.getX(), entity.getY())) > 1300) entity.remove();
		}
	}

	private void handleDeadAndRemovedEntities()
	{
		//Entities
		for(int i = 0; i < entities.size(); i++)
		{
			if(entities.get(i).isRemoved()) entities.remove(i--);
		}
		for(int i = 0; i < particles.size(); i++)
		{
			if(particles.get(i).isRemoved()) particles.remove(i--);
		}
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isRemoved()) players.remove(i--);
		}
		for(int i = 0; i < lightSources.size(); i++)
		{
			if(lightSources.get(i).isRemoved()) lightSources.remove(i--);
		}

		//Dead players
		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).isDead()) players.get(i).whileDead();
		}
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
		Game.getScreen().setOffset(xScroll, yScroll);

		renderVisibleTiles(xScroll, yScroll, screen);
		renderVisibleEntities(xScroll, yScroll, screen);
	}

	public void render(Screen screen)
	{
		renderVisibleTiles(screen.getXOffset(), screen.getYOffset(), screen);
		renderVisibleEntities(screen.getXOffset(), screen.getYOffset(), screen);
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
			g.drawRect(Game.SCALE * (allEntities.get(i).getX() - Game.getScreen().getXOffset() + hitbox.getXOffset()),
					Game.SCALE * (allEntities.get(i).getY() - Game.getScreen().getYOffset() + hitbox.getYOffset()),
					(hitbox.getWidth() + 1) * Game.SCALE, (hitbox.getHeight() + 1) * Game.SCALE);
		}
	}

	private void time()
	{
		if(++time > MAX_TIME) time = 0;
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

	public Vector2i hitboxCollidesWithSolidTileVector(int x, int y, Hitbox hitbox)
	{
		Vector2i vector = new Vector2i();

		for(int currentX = x + hitbox.getXOffset() >> TILE_SIZE_SHIFTING; currentX <= x + hitbox.getWidth()
				+ hitbox.getXOffset() >> TILE_SIZE_SHIFTING; currentX++)
		{
			for(int currentY = y + hitbox.getYOffset() >> TILE_SIZE_SHIFTING; currentY <= y + hitbox.getHeight()
					+ hitbox.getYOffset() >> TILE_SIZE_SHIFTING; currentY++)
			{
				Tile currentTile = getTile(currentX, currentY);
				if(currentTile.solid())
				{
					vector.set(currentX * Tile.DEFAULT_TILE_SIZE, currentY * Tile.DEFAULT_TILE_SIZE);
					return vector;
				}
				if(currentTile.getHitbox() != null)
				{
					if(hitboxCollidesWithHitbox(x, y, hitbox, currentX * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getXOffset(),
							currentY * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getYOffset(), currentTile.getHitbox()))
					{
						vector = hitboxCollidesWithHitboxVector(x, y, hitbox,
								currentX * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getXOffset(),
								currentY * Tile.DEFAULT_TILE_SIZE + currentTile.getHitbox().getYOffset(), currentTile.getHitbox());
						return vector;
					}
				}
			}
		}

		return null;
	}

	public List<Player> playersCollidedWithHitbox(int x, int y, Hitbox hitbox)
	{
		List<Player> collidedPlayers = new ArrayList<Player>();

		for(Player p : players)
		{
			if(p.isDead()) continue;
			if(hitboxCollidesWithHitbox(x, y, hitbox, p.getX(), p.getY(), p.getHitbox())) collidedPlayers.add(p);
		}

		return collidedPlayers;
	}

	public List<Mob> mobsCollidedWithHitbox(int x, int y, Hitbox hitbox)
	{
		List<Mob> mobs = getMobs();
		List<Mob> collidedMobs = new ArrayList<Mob>();
		for(Mob m : mobs)
		{
			if(hitboxCollidesWithHitbox(x, y, hitbox, m.getX(), m.getY(), m.getHitbox())) collidedMobs.add(m);
		}

		return collidedMobs;
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

	private Vector2i hitboxCollidesWithHitboxVector(int h0xPos, int h0yPos, Hitbox h0, int h1xPos, int h1yPos, Hitbox h1)
	{
		for(int currentX = h0xPos + h0.getXOffset(); currentX <= h0xPos + h0.getWidth() + h0.getXOffset(); currentX++)
		{
			for(int currentY = h0yPos + h0.getYOffset(); currentY <= h0yPos + h0.getHeight() + h0.getYOffset(); currentY++)
			{
				if(currentX >= h1xPos + h1.getXOffset() && currentX <= h1xPos + h1.getXOffset() + h1.getWidth()
						&& currentY >= h1yPos + h1.getYOffset() && currentY <= h1yPos + h1.getYOffset() + h1.getHeight())
					return new Vector2i(currentX, currentY);
			}
		}

		return null;
	}

	public void add(Entity e)
	{
		e.init(this);

		if(e instanceof Particle) particles.add((Particle) e);
		else if(e instanceof Projectile) entities.add((Projectile) e);
		else if(e instanceof Player) players.add((Player) e);
		else if(e instanceof LightSource) lightSources.add((LightSource) e);
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
		for(LightSource lightSource : lightSources)
			entities.add(lightSource);

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

	public Player getPlayerByIP(String IPAddress)
	{
		if(IPAddress == null) return null;

		for(int i = 0; i < players.size(); i++)
		{
			if(players.get(i).getIPAddress() == null) continue;
			if(players.get(i).getIPAddress().equals(IPAddress)) return players.get(i);
		}

		return null;
	}

	public Player getPlayerByName(String playerName)
	{
		for(Player player : players)
		{
			if(!(player instanceof OnlinePlayer)) continue;
			if(((OnlinePlayer) player).getPlayerName().equals(playerName)) return player;
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

	public Player getNearestPlayer(Entity entity)
	{
		int x0 = entity.getX();
		int y0 = entity.getY();

		Player nearestPlayer = null;

		for(Player player : players)
		{
			if(player.isDead()) continue;
			if(nearestPlayer == null) nearestPlayer = player;

			if(Math.sqrt(Math.pow(x0 - player.getX(), 2) + Math.pow(y0 - player.getY(), 2)) < Math
					.sqrt(Math.pow(x0 - nearestPlayer.getX(), 2) + Math.pow(y0 - nearestPlayer.getY(), 2)))
				nearestPlayer = player;
		}

		return nearestPlayer;
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

	public List<LightSource> getVisibleLightSources(int xOffset, int yOffset, int width, int height)
	{
		List<LightSource> visibleLightSources = new ArrayList<LightSource>();

		for(LightSource ls : lightSources)
		{
			if(ls.getX() + ls.getRadius() >= xOffset && ls.getY() + ls.getRadius() >= yOffset && ls.getX() - ls.getRadius() <= xOffset + width
					&& ls.getY() - ls.getRadius() <= yOffset + height)
				visibleLightSources.add(ls);
		}

		return visibleLightSources;
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

	public Entity getEntity(UUID uuid)
	{
		List<Entity> allEntities = getAllEntities();

		for(Entity entity : allEntities)
		{
			if(entity.getUUID().compareTo(uuid) == 0) return entity;
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
				if(at.getHitbox() != null) continue;
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

	public Tile getTile(int x, int y)
	{
		//If out of bounds, return void tile
		if(x < 0 || y < 0 || x >= width || y >= height) return Tile.TILE_VOID;
		//Tiles: If colour in level file at specific location is e.g. equal to grass, then return a grass tile
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

		//Unknown colour
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

	public int getTime()
	{
		return time;
	}

	public String getLevelName()
	{
		return levelName;
	}

	public long getSeed()
	{
		return -1;
	}

	public TileCoordinate getSpawnLocation()
	{
		return playerSpawn;
	}

	public GameLevel getGameLevel()
	{
		return gameLevel;
	}
}
