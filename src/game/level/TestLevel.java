package game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.util.Print;
import game.util.TileCoordinate;

public class TestLevel extends Level
{
	public TestLevel()
	{
		super(new TileCoordinate(6, 2));
	}

	protected void loadLevel(String path)
	{
		try
		{
			BufferedImage image = ImageIO.read(TestLevel.class.getResource(path));
			int w = width = image.getWidth();
			int h = height = image.getHeight();
			tiles = new int[w * h];
			//Converting image into the array levelPixels
			image.getRGB(0, 0, w, h, tiles, 0, w);
		}
		catch(IOException e)
		{
			Print.printError(e.getMessage());
		}
	}

	protected void generateLevel()
	{

	}

}
