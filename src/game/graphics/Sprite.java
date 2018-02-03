package game.graphics;

import java.util.Arrays;

import game.level.tile.Tile;

public class Sprite
{

	public final int SIZE;
	private static final int DEFAULT_TILE_SIZE = Tile.DEFAULT_TILE_SIZE;
	private int x, y;
	private int width, height;
	public int[] pixels;
	private SpriteSheet spriteSheet;

	//Sprites

	//Blocks
	public static Sprite voidSprite = new Sprite(DEFAULT_TILE_SIZE, 0xFF00FF);
	public static Sprite grassSprite = new Sprite(DEFAULT_TILE_SIZE, 0, 0, SpriteSheet.tiles);
	public static Sprite dirtSprite = new Sprite(DEFAULT_TILE_SIZE, 1, 0, SpriteSheet.tiles);
	public static Sprite blockSprite = new Sprite(DEFAULT_TILE_SIZE, 2, 0, SpriteSheet.tiles);
	public static Sprite waterSprite0 = new Sprite(DEFAULT_TILE_SIZE, 4, 0, SpriteSheet.tiles);
	public static Sprite waterSprite1 = new Sprite(DEFAULT_TILE_SIZE, 5, 0, SpriteSheet.tiles);
	public static Sprite waterSprite2 = new Sprite(DEFAULT_TILE_SIZE, 6, 0, SpriteSheet.tiles);
	public static Sprite waterSprite3 = new Sprite(DEFAULT_TILE_SIZE, 7, 0, SpriteSheet.tiles);
	public static Sprite sandSprite = new Sprite(DEFAULT_TILE_SIZE, 8, 0, SpriteSheet.tiles);

	public static Sprite boosterSprite = new Sprite(DEFAULT_TILE_SIZE, 1, 1, SpriteSheet.tiles);
	public static Sprite checkpointSprite = new Sprite(DEFAULT_TILE_SIZE, 5, 1, SpriteSheet.tiles);
	public static Sprite errorSprite = new Sprite(DEFAULT_TILE_SIZE, 3, 0, SpriteSheet.tiles);
	public static Sprite iceSprite = new Sprite(DEFAULT_TILE_SIZE, 2, 1, SpriteSheet.tiles);
	public static Sprite killerSprite = new Sprite(DEFAULT_TILE_SIZE, 3, 1, SpriteSheet.tiles);
	public static Sprite quartzSprite = new Sprite(DEFAULT_TILE_SIZE, 0, 1, SpriteSheet.tiles);
	public static Sprite quartzWallSprite = new Sprite(DEFAULT_TILE_SIZE, 4, 1, SpriteSheet.tiles);

	//Particles
	public static Sprite[] particleQuartz = { new Sprite(2, 0xF6F5E7), new Sprite(3, 0xF3F4E5), new Sprite(3, 0xE9E9E9), new Sprite(2, 0xDEDEDE),
			new Sprite(2, 0xD6D5C4) };
	public static Sprite[] particleSlime = { new Sprite(2, 0x3E7CC2), new Sprite(3, 0x3371B6), new Sprite(2, 0x2160A6), new Sprite(2, 0x205DA1) };
	public static Sprite[] particleBlood = { new Sprite(2, 0xEB3D3D), new Sprite(3, 0xEE4D59), new Sprite(3, 0xEE4D7F), new Sprite(2, 0xFF0048) };

	//Projectiles
	public static Sprite wizardProjectile = new Sprite(DEFAULT_TILE_SIZE, 57, 63, SpriteSheet.tiles);
	public static Sprite boomerangProjectile = new Sprite(DEFAULT_TILE_SIZE, 58, 63, SpriteSheet.tiles);

	//Cooldown bar 1
	public static Sprite[] cooldownBar0 = { new Sprite(DEFAULT_TILE_SIZE, 63, 0, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 62, 0, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 61, 0, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 60, 0, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 59, 0, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 58, 0, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 57, 0, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 56, 0, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 55, 0, SpriteSheet.tiles) };

	//Cooldown bar 2
	public static Sprite[] cooldownBar1 = { new Sprite(DEFAULT_TILE_SIZE, 63, 2, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 62, 2, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 61, 2, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 60, 2, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 59, 2, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 58, 2, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 57, 2, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 56, 2, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 55, 2, SpriteSheet.tiles) };

	//Health bar
	public static Sprite[] healthBar = { new Sprite(DEFAULT_TILE_SIZE, 63, 1, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 62, 1, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 61, 1, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 60, 1, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 59, 1, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 58, 1, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 57, 1, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 56, 1, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 55, 1, SpriteSheet.tiles) };

	//Player
	public static Sprite[] playerDown = { new Sprite(DEFAULT_TILE_SIZE, 63, 63, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 63, 62, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 63, 61, SpriteSheet.tiles) };
	public static Sprite[] playerUp = { new Sprite(DEFAULT_TILE_SIZE, 62, 63, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 62, 62, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 62, 61, SpriteSheet.tiles) };
	public static Sprite[] playerLeft = { new Sprite(DEFAULT_TILE_SIZE, 61, 63, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 61, 62, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 61, 61, SpriteSheet.tiles) };
	public static Sprite[] playerRight = { new Sprite(DEFAULT_TILE_SIZE, 60, 63, SpriteSheet.tiles),
			new Sprite(DEFAULT_TILE_SIZE, 60, 62, SpriteSheet.tiles), new Sprite(DEFAULT_TILE_SIZE, 60, 61, SpriteSheet.tiles) };

	//Slime
	public static Sprite slimeDown = new Sprite(DEFAULT_TILE_SIZE, 59, 63, SpriteSheet.tiles);

	private static String[] letterOrder = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "?", "!", ".", ",", " " };
	private static Sprite[] letters = { new Sprite(0, 994, 22, 30, SpriteSheet.tiles), new Sprite(22, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(45, 994, 22, 30, SpriteSheet.tiles), new Sprite(67, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(89, 994, 22, 30, SpriteSheet.tiles), new Sprite(112, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(134, 994, 22, 30, SpriteSheet.tiles), new Sprite(157, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(179, 994, 22, 30, SpriteSheet.tiles), new Sprite(202, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(224, 994, 22, 30, SpriteSheet.tiles), new Sprite(246, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(269, 994, 22, 30, SpriteSheet.tiles), new Sprite(291, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(313, 994, 22, 30, SpriteSheet.tiles), new Sprite(336, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(358, 994, 22, 30, SpriteSheet.tiles), new Sprite(381, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(403, 994, 22, 30, SpriteSheet.tiles), new Sprite(425, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(448, 994, 22, 30, SpriteSheet.tiles), new Sprite(470, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(493, 994, 22, 30, SpriteSheet.tiles), new Sprite(515, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(537, 994, 22, 30, SpriteSheet.tiles), new Sprite(560, 994, 22, 30, SpriteSheet.tiles),
			new Sprite(0, 960, 22, 34, SpriteSheet.tiles), new Sprite(22, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(45, 960, 22, 34, SpriteSheet.tiles), new Sprite(67, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(89, 960, 22, 34, SpriteSheet.tiles), new Sprite(112, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(134, 960, 22, 34, SpriteSheet.tiles), new Sprite(157, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(179, 960, 22, 34, SpriteSheet.tiles), new Sprite(202, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(226, 960, 22, 34, SpriteSheet.tiles), new Sprite(246, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(269, 960, 22, 34, SpriteSheet.tiles), new Sprite(291, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(313, 960, 22, 34, SpriteSheet.tiles), new Sprite(336, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(358, 960, 22, 34, SpriteSheet.tiles), new Sprite(381, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(403, 960, 22, 34, SpriteSheet.tiles), new Sprite(425, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(448, 960, 22, 34, SpriteSheet.tiles), new Sprite(470, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(493, 960, 22, 34, SpriteSheet.tiles), new Sprite(515, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(537, 960, 22, 34, SpriteSheet.tiles), new Sprite(560, 960, 22, 34, SpriteSheet.tiles),
			new Sprite(0, 926, 22, 34, SpriteSheet.tiles), new Sprite(22, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(45, 926, 22, 34, SpriteSheet.tiles), new Sprite(67, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(89, 926, 22, 34, SpriteSheet.tiles), new Sprite(112, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(134, 926, 22, 34, SpriteSheet.tiles), new Sprite(157, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(179, 926, 22, 34, SpriteSheet.tiles), new Sprite(202, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(224, 926, 22, 34, SpriteSheet.tiles), new Sprite(246, 926, 22, 34, SpriteSheet.tiles),
			new Sprite(269, 926, 22, 34, SpriteSheet.tiles), new Sprite(291, 926, 22, 34, SpriteSheet.tiles), new Sprite(22, 34, 0xFFFF00FF) };

	/**
	 * Writes text to the screen. Note: The position in the center of the text
	 * 
	 * @param text
	 * @param screen
	 * @param xPos
	 * @param yPos
	 */
	public static void writeText(String text, Screen screen, int xPos, int yPos, int colour)
	{
		if(text == null) return;
		Sprite[] renderText = new Sprite[text.length()];
		int renderTextWidth = 0;

		for(int i = 0; i < text.length(); i++)
		{
			String currentLetter = text.substring(i, i + 1);
			for(int j = 0; j < letterOrder.length; j++)
			{
				if(currentLetter.equals(letterOrder[j]))
				{
					renderText[i] = letters[j];
					renderTextWidth += renderText[i].width;
				}
			}
		}

		for(int i = 0; i < renderText.length; i++)
		{
			if(renderText[i] == null) continue;
			renderText[i].setColourWithoutMagicPink(colour);
			screen.renderSprite(xPos + i * renderText[i].width - renderTextWidth / 2, yPos - renderText[i].height / 2, renderText[i], false);
		}
	}

	public Sprite(int size, int x, int y, SpriteSheet spriteSheet)
	{
		this.SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		this.x = x * 16;
		this.y = y * 16;
		this.spriteSheet = spriteSheet;
		load();
	}

	public Sprite(int xPosInPixels, int yPosInPixels, int widthInPixels, int heightInPixels, SpriteSheet spriteSheet)
	{
		SIZE = -1;
		this.width = widthInPixels;
		this.height = heightInPixels;
		pixels = new int[width * height];
		this.x = xPosInPixels;
		this.y = yPosInPixels;
		this.spriteSheet = spriteSheet;
		load(width, height);
	}

	public Sprite(int width, int height, int colour)
	{
		SIZE = -1;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		setColour(colour);
	}

	public Sprite(int size, int colour)
	{
		this.SIZE = size;
		this.width = size;
		this.height = size;
		pixels = new int[SIZE * SIZE];
		setColour(colour);
	}

	private void setColour(int colour)
	{
		Arrays.fill(pixels, colour);
	}

	private void setColourWithoutMagicPink(int colour)
	{
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if(pixels[x + y * width] == 0xFFFF00FF) continue;
				pixels[x + y * width] = colour;
			}
		}
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	//Extracts a single sprite out of the sprite sheet
	private void load()
	{
		for(int y = 0; y < SIZE; y++)
		{
			for(int x = 0; x < SIZE; x++)
			{
				pixels[x + y * SIZE] = spriteSheet.pixels[(x + this.x) + (y + this.y) * spriteSheet.SIZE];
			}
		}
	}

	private void load(int width, int height)
	{
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				pixels[x + y * width] = spriteSheet.pixels[(x + this.x) + (y + this.y) * spriteSheet.SIZE];
			}
		}
	}

}
