package game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import game.level.generation.GenerateLevel;
import game.util.TileCoordinate;

public class GameLevel extends Level
{
	private String path;
	private long seed;
	private String levelName;
	private boolean customLevel;

	public GameLevel(String path, String levelName, int spawnX, int spawnY)
	{
		this.playerSpawn = new TileCoordinate(spawnX, spawnY);
		this.path = path;
		this.customLevel = true;
		this.levelName = levelName;
	}

	public GameLevel(long seed, String levelName, TileCoordinate playerSpawn)
	{
		this.playerSpawn = playerSpawn;
		this.seed = seed;
		this.customLevel = false;
		this.levelName = levelName;
	}

	public void loadLevel(String path)
	{
		try
		{
			BufferedImage image = ImageIO.read(GameLevel.class.getResource(path));
			width = image.getWidth();
			height = image.getHeight();
			tiles = new int[width * height];
			//Converting image into the array levelPixels
			image.getRGB(0, 0, width, height, tiles, 0, width);
		}
		catch(IOException e)
		{
			Game.getPrinter().printError(e.getMessage());
		}
	}

	public void loadLevel(long seed)
	{
		height = width = 512;
		tiles = GenerateLevel.generateLevel(seed);
	}

	public String getPath()
	{
		return path;
	}

	public long getSeed()
	{
		return seed;
	}

	public boolean isCustomLevel()
	{
		return customLevel;
	}

	public String getLevelName()
	{
		return levelName;
	}
}
