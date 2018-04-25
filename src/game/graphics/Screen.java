package game.graphics;

import game.input.Keyboard;
import game.level.tile.Tile;

public class Screen
{
	public int width, height;
	public int[] pixels;

	public static final byte TILE_SIZE_SHIFTING = 4; //3: 8x8 Tiles, 4: 16x16 Tiles, 5: 32x32 Tiles, 2^tileSize = tiles in pixels

	private static float xOffset;
	private static float yOffset;

	public Screen(int width, int height, Keyboard input)
	{
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public void clear()
	{
		for(int i = 0; i < pixels.length; i++)
		{
			pixels[i] = 0x000000;
		}
	}

	public void renderSprite(int xPos, int yPos, Sprite sprite, boolean fixedToMapOffset)
	{
		if(fixedToMapOffset)
		{
			xPos -= xOffset;
			yPos -= yOffset;
		}
		for(int y = 0; y < sprite.getHeight(); y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < sprite.getWidth(); x++)
			{
				int xAbsolute = x + xPos;

				if(xAbsolute < 0 || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) continue;

				int pixelColour = sprite.pixels[x + y * sprite.getWidth()];
				if(pixelColour != 0xFFFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered

			}
		}
	}

	public void renderSprite(int xPos, int yPos, int renderWidth, int renderHeight, Sprite sprite, boolean fixedToMapOffset)
	{
		if(fixedToMapOffset)
		{
			xPos -= xOffset;
			yPos -= yOffset;
		}
		for(int y = 0; y < renderHeight; y++)
		{
			if(y >= sprite.getWidth()) return;
			int yAbsolute = y + yPos;

			for(int x = 0; x < renderWidth; x++)
			{
				if(x >= sprite.getHeight()) continue;
				int xAbsolute = x + xPos;

				if(xAbsolute < 0 || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) continue;

				int pixelColour = sprite.pixels[x + y * sprite.getWidth()];
				if(pixelColour != 0xFFFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered

			}
		}
	}

	public void renderTile(int xPos, int yPos, Tile tile)
	{
		xPos -= xOffset;
		yPos -= yOffset;
		for(int y = 0; y < tile.getSprite().SIZE; y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < tile.getSprite().SIZE; x++)
			{
				int xAbsolute = x + xPos;
				if(xAbsolute < -tile.getSprite().SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break; //-tile.sprite.SIZE renders 1 more tile
				if(xAbsolute < 0) xAbsolute = 0; //Prevents array index out of bounds exception

				int pixelColour = tile.getSprite().pixels[x + y * tile.getSprite().SIZE];
				if(pixelColour != 0xFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered
			}
		}
	}

	public void renderGUI(GUI gui)
	{
		if(gui != null) gui.render(this);
	}

	public void applyGamma(float gamma)
	{
		if(gamma < 0 || gamma == 1) return;
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int r = (pixels[x + y * width] & 0xFF0000) >> 16;
				int g = (pixels[x + y * width] & 0x00FF00) >> 8;
				int b = (pixels[x + y * width] & 0x0000FF);

				r *= gamma;
				g *= gamma;
				b *= gamma	;

				if(r > 255) r = 255;
				if(g > 255) g = 255;
				if(b > 255) b = 255;

				pixels[x + y * width] = (r << 16) | (g << 8) | (b);
			}
		}
	}

	public void blur()
	{
		//A for loop would be much more readable, but this way it's more than twice as fast
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				//4 corner pixels
				if(x == 0 && y == 0)
				{
					int rSum = ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 4) << 16) | ((gSum / 4) << 8) | ((bSum / 4));
					continue;
				}
				else if(x == 0 && y == height - 1)
				{
					int rSum = ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 4) << 16) | ((gSum / 4) << 8) | ((bSum / 4));
					continue;
				}
				else if(x == width - 1 && y == 0)
				{
					int rSum = ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 4) << 16) | ((gSum / 4) << 8) | ((bSum / 4));
					continue;
				}
				else if(x == width - 1 && y == height - 1)
				{
					int rSum = ((pixels[(x + -1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 4) << 16) | ((gSum / 4) << 8) | ((bSum / 4));
					continue;
				}
				//Edge pixels
				else if(x == 0)
				{
					int rSum = ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 6) << 16) | ((gSum / 6) << 8) | ((bSum / 6));
					continue;
				}
				else if(y == 0)
				{
					int rSum = ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + -1) + (y + 1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + -1) + (y + 1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + -1) + (y + 1) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 6) << 16) | ((gSum / 6) << 8) | ((bSum / 6));

					continue;
				}
				else if(x == width - 1)
				{
					int rSum = ((pixels[(x + -1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 6) << 16) | ((gSum / 6) << 8) | ((bSum / 6));
					continue;
				}
				else if(y == height - 1)
				{
					int rSum = ((pixels[(x + -1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + 1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 6) << 16) | ((gSum / 6) << 8) | ((bSum / 6));
					continue;
				}
				//Normal pixels
				else
				{
					int rSum = ((pixels[(x + -1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + -1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 1) + (y + -1) * width] & 0xFF0000) >> 16) + ((pixels[(x + -1) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0xFF0000) >> 16) + ((pixels[(x + 1) + (y + 0) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0xFF0000) >> 16) + ((pixels[(x + 0) + (y + 1) * width] & 0xFF0000) >> 16)
							+ ((pixels[(x + 1) + (y + 1) * width] & 0xFF0000) >> 16);

					int gSum = ((pixels[(x + -1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + -1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 1) + (y + -1) * width] & 0x00FF00) >> 8) + ((pixels[(x + -1) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x00FF00) >> 8) + ((pixels[(x + 1) + (y + 0) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x00FF00) >> 8) + ((pixels[(x + 0) + (y + 1) * width] & 0x00FF00) >> 8)
							+ ((pixels[(x + 1) + (y + 1) * width] & 0x00FF00) >> 8);

					int bSum = ((pixels[(x + -1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + -1) * width] & 0x0000FF))
							+ ((pixels[(x + 1) + (y + -1) * width] & 0x0000FF)) + ((pixels[(x + -1) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + 0) + (y + 0) * width] & 0x0000FF)) + ((pixels[(x + 1) + (y + 0) * width] & 0x0000FF))
							+ ((pixels[(x + -1) + (y + 1) * width] & 0x0000FF)) + ((pixels[(x + 0) + (y + 1) * width] & 0x0000FF))
							+ ((pixels[(x + 1) + (y + 1) * width] & 0x0000FF));

					pixels[x + y * width] = ((rSum / 9) << 16) | ((gSum / 9) << 8) | ((bSum / 9));
				}
			}
		}
	}

	public void setOffset(float xOffset, float yOffset)
	{
		Screen.xOffset = xOffset;
		Screen.yOffset = yOffset;
	}

	public static int getXOffset()
	{
		return (int) xOffset;
	}

	public static int getYOffset()
	{
		return (int) yOffset;
	}

	public static float getXOffsetFloat()
	{
		return xOffset;
	}

	public static float getYOffsetFloat()
	{
		return yOffset;
	}
}
