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
package game.graphics;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.entity.lighting.LightSource;
import game.level.tile.Tile;
import game.util.GameState;

public class Screen
{
	public int width, height;
	public int[] pixels;

	public static final byte TILE_SIZE_SHIFTING = 4; //3: 8x8 Tiles, 4: 16x16 Tiles, 5: 32x32 Tiles, 2^tileSize = tiles in pixels

	private static float xOffset;
	private static float yOffset;

	private List<LightSource> visibleLightSources = new ArrayList<LightSource>();

	public Screen(int width, int height)
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

	public void applyBrightness()
	{
		if(Game.getLevel() == null || !(Game.getGameState() == GameState.IngameOffline || Game.getGameState() == GameState.IngameOnline)) return;
		float time = Game.getLevel().getTime() / 150F; //hour format, time ranging from 0 to 24

		float gammaBase = Math.abs(Math.abs(time - 12) - 12) / 12; //Dependent on the daytime only
		float gamma;

		if(gammaBase < 0.2F) gammaBase = 0.2F; //Max darkness

		visibleLightSources = Game.getLevel().getVisibleLightSources((int) xOffset, (int) yOffset, width, height);

		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				gamma = getGammaFromLightSources(x, y, gammaBase);
				if(gamma >= 1F) continue; //Max brightness
				applyGamma(gamma, x, y);
			}
		}
	}

	public void applyGamma(float gamma, int xPixel, int yPixel)
	{
		if(gamma < 0F || gamma == 1F) return;

		int r = (pixels[xPixel + yPixel * width] & 0xFF0000) >> 16;
		int g = (pixels[xPixel + yPixel * width] & 0x00FF00) >> 8;
		int b = (pixels[xPixel + yPixel * width] & 0x0000FF);

		r *= gamma;
		g *= gamma;
		b *= gamma;

		if(r > 255) r = 255;
		if(g > 255) g = 255;
		if(b > 255) b = 255;

		pixels[xPixel + yPixel * width] = (r << 16) | (g << 8) | (b);
	}

	public float getGammaFromLightSources(int xPixel, int yPixel, float gammaBase)
	{
		xPixel += xOffset;
		yPixel += yOffset;

		float gamma;

		for(LightSource lightSource : visibleLightSources)
		{
			if(!lightSource.affectsPixelAt(xPixel, yPixel)) continue;

			gamma = lightSource.getGammaAtPixel(xPixel, yPixel);
			if(gamma <= 1.0F) continue; //It shouldn't be possible for a light source to darken

			gammaBase *= gamma;
		}

		return gammaBase;
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
