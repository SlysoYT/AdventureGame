package game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.util.Print;
import game.util.TileCoordinate;

public class GameLevel extends Level
{
	private String path;
	private String levelName;

	public GameLevel(String path, String levelName, int spawnX, int spawnY)
	{
		super(new TileCoordinate(spawnX, spawnY));
		this.path = path;
		this.levelName = levelName;
	}

	public void loadLevel(String path)
	{
		try
		{
			BufferedImage image = ImageIO.read(GameLevel.class.getResource(path));
			int w = width = image.getWidth();
			int h = height = image.getHeight();
			tiles = new int[w * h];
			//Converting image into the array levelPixels
			image.getRGB(0, 0, w, h, tiles, 0, w);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			Print.printError("Unable to load level file");
		}
	}

	public String getPath()
	{
		return path;
	}

	public String getLevelName()
	{
		return levelName;
	}
}
