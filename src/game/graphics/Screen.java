package game.graphics;

import game.entity.mob.Mob;
import game.entity.projectile.Projectile;
import game.input.Keyboard;
import game.level.tile.Tile;

public class Screen
{
	public int width, height;
	public int[] pixels;

	public static final byte TILE_SIZE_SHIFTING = 4; //3: 8x8 Tiles, 4: 16x16 Tiles, 5: 32x32 Tiles, 2^tileSize = tiles in pixels

	private static int xOffset;
	private static int yOffset;

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

	public void renderTile(int xPos, int yPos, Tile tile)
	{
		xPos -= xOffset;
		yPos -= yOffset;
		for(int y = 0; y < tile.sprite.SIZE; y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < tile.sprite.SIZE; x++)
			{
				int xAbsolute = x + xPos;
				if(xAbsolute < -tile.sprite.SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break; //-tile.sprite.SIZE renders 1 more tile
				if(xAbsolute < 0) xAbsolute = 0; //Prevents array index out of bounds exception

				int pixelColour = tile.sprite.pixels[x + y * tile.sprite.SIZE];
				if(pixelColour != 0xFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered
			}
		}
	}

	public void renderProjectile(int xPos, int yPos, Projectile projectile)
	{
		xPos -= xOffset;
		yPos -= yOffset;
		for(int y = 0; y < projectile.getSpriteSize(); y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < projectile.getSpriteSize(); x++)
			{
				int xAbsolute = x + xPos;
				if(xAbsolute < -projectile.getSpriteSize() || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break; //-tile.sprite.SIZE renders 1 more tile
				if(xAbsolute < 0) xAbsolute = 0; //Prevents array index out of bounds exception

				int pixelColour = projectile.getSprite().pixels[x + y * projectile.getSpriteSize()];
				if(pixelColour != 0xFFFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered
			}
		}
	}

	public void renderSprite(int xPos, int yPos, Sprite sprite)
	{
		xPos -= xOffset;
		yPos -= yOffset;
		for(int y = 0; y < Tile.DEFAULT_TILE_SIZE; y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < Tile.DEFAULT_TILE_SIZE; x++)
			{
				int xAbsolute = x + xPos;
				if(xAbsolute < -Tile.DEFAULT_TILE_SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break; //-tile.sprite.SIZE renders 1 more tile
				if(xAbsolute < 0) xAbsolute = 0; //Prevents array index out of bounds exception

				int pixelColour = sprite.pixels[x + y * Tile.DEFAULT_TILE_SIZE];
				if(pixelColour != 0xFFFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered
			}
		}
	}

	public void renderMob(int xPos, int yPos, Mob mob)
	{
		xPos -= xOffset;
		yPos -= yOffset;
		for(int y = 0; y < Tile.DEFAULT_TILE_SIZE; y++)
		{
			int yAbsolute = y + yPos;

			for(int x = 0; x < Tile.DEFAULT_TILE_SIZE; x++)
			{
				int xAbsolute = x + xPos;
				if(xAbsolute < -Tile.DEFAULT_TILE_SIZE || xAbsolute >= width || yAbsolute < 0 || yAbsolute >= height) break; //-tile.sprite.SIZE renders 1 more tile
				if(xAbsolute < 0) xAbsolute = 0; //Prevents array index out of bounds exception

				int pixelColour = mob.getSprite().pixels[x + y * Tile.DEFAULT_TILE_SIZE];
				if(pixelColour != 0xFFFF00FF) pixels[xAbsolute + yAbsolute * width] = pixelColour; //So the magic pink doesn't get rendered
			}
		}
	}

	public void applyAlpha(float alpha)
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				int r = (pixels[x + y * width] & 0xFF0000) >> 16;
				int g = (pixels[x + y * width] & 0x00FF00) >> 8;
				int b = (pixels[x + y * width] & 0x0000FF);

				r *= alpha;
				g *= alpha;
				b *= alpha;

				if(r > 255) r = 255;
				if(g > 255) g = 255;
				if(b > 255) b = 255;

				pixels[x + y * width] = (r << 16) | (g << 8) | (b << 0);
			}
		}
	}

	public void setOffset(int xOffset, int yOffset)
	{
		Screen.xOffset = xOffset;
		Screen.yOffset = yOffset;
	}

	public static int getXOffset()
	{
		return xOffset;
	}

	public static int getYOffset()
	{
		return yOffset;
	}
}