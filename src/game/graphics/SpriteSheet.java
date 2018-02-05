package game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.util.Print;

public class SpriteSheet
{

	private String path;
	public final int SIZE;
	public int pixels[];

	public static SpriteSheet tiles = new SpriteSheet("/textures/spriteSheets/spriteSheet.png", 1024);

	public SpriteSheet(String path, int size)
	{
		this.path = path;
		this.SIZE = size;
		pixels = new int[SIZE * SIZE];
		load();
	}

	private void load()
	{
		try
		{
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			int width = image.getWidth();
			int height = image.getHeight();
			image.getRGB(0, 0, width, height, pixels, 0, width);
		}
		catch(IOException e)
		{
			Print.printError(e.getMessage());
		}
	}

}
