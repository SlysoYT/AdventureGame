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
import java.util.stream.Collectors;

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
import game.input.Keyboard;
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
	protected TileCoordinate playerSpawn;

	private final int TILE_SIZE_SHIFTING = Game.getScreen().TILE_SIZE_SHIFTING;
	private String levelName = "";

	private final long seed;

	private Random rand = new Random();

	private final int MAX_TIME = 3600;
	private int time = MAX_TIME / 2;

	private List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
	private List<Particle> particles = Collections.synchronizedList(new ArrayList<>());
	private List<Player> players = Collections.synchronizedList(new ArrayList<>());
	private List<LightSource> lightSources = Collections.synchronizedList(new ArrayList<>());

	public Level(int width, int height, int[] tiles, long seed, Keyboard keyboard, String levelName)
	{
		this.width = width;
		this.height = height;
		this.tiles = tiles;

		this.seed = seed;

		this.levelName = levelName;
		this.playerSpawn = new TileCoordinate(width / 2, height / 2);

		//Add the client player
		if(!levelName.equals("TitleScreen")) add(new Player(getSpawnLocation().getX(), getSpawnLocation().getY(), keyboard));
	}

	public void tick()
	{
		handleCamera();
		mobSpawning();

		tickAllEntities();

		time();
		PlayMusic.tick();
	}

	private void mobSpawning()
	{
		if(!(Game.getGameState() == GameState.IngameOffline || (Game.getGameState() == GameState.IngameOnline && Game.isHostingGame))) return;

		for(Player player : players)
		{
			if(player.isMoving())
			{
				if(player.getDistanceMoved() % 3 == 0 && rand.nextInt(30) == 0)
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

	private void handleCamera()
	{
		if(getClientPlayer() == null) return;

		if(Game.getGameStateTicksPassed() == 0)
		{
			Game.getScreen().setOffset(getClientPlayer().getX() - Game.width, getClientPlayer().getY() - Game.height);
		}

		float deltaX = getClientPlayer().getX() - Game.width / 2 - Game.getScreen().getXOffsetFloat();
		float deltaY = getClientPlayer().getY() - Game.height / 2 - Game.getScreen().getYOffsetFloat();

		Game.getScreen().setCameraMotion(deltaX / 20, deltaY / 20);
	}

	private void tickAllEntities()
	{
		//Tick entities
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

		//Handle dead and removed entities
		entities.removeIf(e -> e.isRemoved());
		particles.removeIf(p -> p.isRemoved());
		players.removeIf(p -> p.isRemoved());
		lightSources.removeIf(l -> l.isRemoved());

		//Dead players
		players.stream().filter(p -> p.isDead()).forEach(p -> p.whileDead());
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

		entities.forEach(e ->
		{
			if(e.getX() > x0 && e.getX() < x1 && e.getY() > y0 && e.getY() < y1) e.render(screen);
		});

		players.forEach(p ->
		{
			if(p.getX() > x0 && p.getX() < x1 && p.getY() > y0 && p.getY() < y1) p.render(screen);
		});

		particles.forEach(p ->
		{
			if(p.getX() > x0 && p.getX() < x1 && p.getY() > y0 && p.getY() < y1) p.render(screen);
		});
	}

	public void renderHitboxes(Graphics g)
	{
		g.setColor(Color.BLUE);

		getAllEntities().stream().filter(e -> e.getHitbox() != null).forEach(e ->
		{
			Hitbox hitbox = e.getHitbox();

			g.drawRect(Game.SCALE * (e.getX() - Game.getScreen().getXOffset() + hitbox.getXOffset() - 1),
					Game.SCALE * (e.getY() - Game.getScreen().getYOffset() + hitbox.getYOffset() - 1), (hitbox.getWidth() + 1) * Game.SCALE,
					(hitbox.getHeight() + 1) * Game.SCALE);
		});
	}

	private void time()
	{
		if(++time > MAX_TIME) time = 0;
	}

	public boolean hitboxCollidesWithSolidTile(int x, int y, Hitbox hitbox)
	{
		return hitboxCollidesWithSolidTileVector(x, y, hitbox) != null;
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
		return getPlayersAlive().stream().filter(p -> hitboxCollidesWithHitbox(x, y, hitbox, p.getX(), p.getY(), p.getHitbox()))
				.collect(Collectors.toList());
	}

	public List<Mob> mobsCollidedWithHitbox(int x, int y, Hitbox hitbox)
	{
		return getMobs().stream().filter(m -> hitboxCollidesWithHitbox(x, y, hitbox, m.getX(), m.getY(), m.getHitbox())).collect(Collectors.toList());
	}

	private boolean hitboxCollidesWithHitbox(int h0xPos, int h0yPos, Hitbox h0, int h1xPos, int h1yPos, Hitbox h1)
	{
		return hitboxCollidesWithHitboxVector(h0xPos, h0yPos, h0, h1xPos, h1yPos, h1) != null;
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

		entities.addAll(this.entities);

		entities.addAll(players);
		entities.addAll(particles);
		entities.addAll(lightSources);

		return entities;
	}

	public List<Mob> getMobs()
	{
		List<Mob> mobs = new ArrayList<Mob>();

		mobs.addAll(players);
		entities.stream().filter(e -> e instanceof Mob).collect(Collectors.toList()).forEach(m -> mobs.add((Mob) m));

		return mobs;
	}

	public List<Player> getPlayersAlive()
	{
		return players.stream().filter(player -> !player.isDead()).collect(Collectors.toList());
	}

	public Player getPlayerByIP(String IPAddress)
	{
		if(IPAddress == null) return null;
		return players.stream().filter(p -> p.getIPAddress() != null).filter(p -> p.getIPAddress().equals(IPAddress)).findFirst().orElse(null);
	}

	public Player getPlayerByName(String playerName)
	{
		return players.stream().filter(p -> p instanceof OnlinePlayer).filter(p -> ((OnlinePlayer) p).getPlayerName().equals(playerName)).findFirst()
				.orElse(null);
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

		for(Player player : getPlayersAlive())
		{
			if(nearestPlayer == null) nearestPlayer = player;

			if(Math.sqrt(Math.pow(x0 - player.getX(), 2) + Math.pow(y0 - player.getY(), 2)) < Math
					.sqrt(Math.pow(x0 - nearestPlayer.getX(), 2) + Math.pow(y0 - nearestPlayer.getY(), 2)))
				nearestPlayer = player;
		}

		return nearestPlayer;
	}

	public List<LightSource> getVisibleLightSources(int xOffset, int yOffset, int width, int height)
	{
		return lightSources.stream().filter(ls ->
		{
			return ls.getX() + ls.getRadius() >= xOffset && ls.getY() + ls.getRadius() >= yOffset && ls.getX() - ls.getRadius() <= xOffset + width
					&& ls.getY() - ls.getRadius() <= yOffset + height;
		}).collect(Collectors.toList());
	}

	public Projectile getProjectile(UUID uuid)
	{
		if(uuid == null) return null;
		return (Projectile) entities.stream().filter(e -> e instanceof Projectile).filter(p -> p.getUUID().equals(uuid)).findFirst().orElse(null);
	}

	public Entity getEntity(UUID uuid)
	{
		return getAllEntities().stream().filter(e -> e.getUUID().equals(uuid)).findFirst().orElse(null);
	}

	public double getDistance(Vector2i vector1, Vector2i vector2)
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

		int tileCol = tiles[x + y * width];
		//Tiles: If colour in level file at specific location is e.g. equal to grass, then return a grass tile
		if(tileCol == Tile.COL_TILE_DIRT) return Tile.TILE_DIRT;
		if(tileCol == Tile.COL_TILE_GRASS) return Tile.TILE_GRASS;
		if(tileCol == Tile.COL_TILE_FLOWER_0) return Tile.TILE_FLOWER_0;
		if(tileCol == Tile.COL_TILE_FLOWER_1) return Tile.TILE_FLOWER_1;
		if(tileCol == Tile.COL_TILE_FLOWER_2) return Tile.TILE_FLOWER_2;
		if(tileCol == Tile.COL_TILE_FLOWER_3) return Tile.TILE_FLOWER_3;
		if(tileCol == Tile.COL_TILE_ROCK_GRASS) return Tile.TILE_ROCK_GRASS;
		if(tileCol == Tile.COL_TILE_ROCK_SAND) return Tile.TILE_ROCK_SAND;
		if(tileCol == Tile.COL_TILE_SAND) return Tile.TILE_SAND;
		if(tileCol == Tile.COL_TILE_WATER) return Tile.TILE_WATER;

		if(tileCol == Tile.COL_TILE_BOOSTER) return Tile.TILE_BOOSTER;
		if(tileCol == Tile.COL_TILE_CHECKPOINT) return Tile.TILE_CHECKPOINT;
		if(tileCol == Tile.COL_TILE_ICE) return Tile.TILE_ICE;
		if(tileCol == Tile.COL_TILE_KILLER) return Tile.TILE_KILLER;
		if(tileCol == Tile.COL_TILE_VOID) return Tile.TILE_VOID;
		if(tileCol == Tile.COL_TILE_QUARTZ) return Tile.TILE_QUARTZ;
		if(tileCol == Tile.COL_TILE_QUARTZ_WALL) return Tile.TILE_QUARTZ_WALL;

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
		return seed;
	}

	public TileCoordinate getSpawnLocation()
	{
		return playerSpawn;
	}
}
