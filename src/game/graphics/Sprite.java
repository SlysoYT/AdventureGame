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

import java.util.Arrays;
import java.util.Random;

import game.Game;
import game.level.tile.Tile;

public class Sprite
{
	public final int SIZE;
	private static final int DEFAULT_TILE_SIZE = Tile.DEFAULT_TILE_SIZE;
	private int x, y;
	private int width, height;
	public int[] pixels;
	private SpriteSheet spriteSheet;
	private static Random rand = new Random();

	//Sprites

	//Special sprites
	public static final Sprite SPRITE_VOID = new Sprite(DEFAULT_TILE_SIZE, 0xFF00FF);
	public static final Sprite SPRITE_ERROR = new Sprite(DEFAULT_TILE_SIZE, 0, 0, SpriteSheet.SPRITE_SHEET);

	//Natural sprites
	public static final Sprite SPRITE_DIRT = new Sprite(DEFAULT_TILE_SIZE, 1, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_FLOWER_0 = new Sprite(DEFAULT_TILE_SIZE, 0, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_FLOWER_1 = new Sprite(DEFAULT_TILE_SIZE, 1, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_FLOWER_2 = new Sprite(DEFAULT_TILE_SIZE, 2, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_FLOWER_3 = new Sprite(DEFAULT_TILE_SIZE, 3, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_ROCK_GRASS = new Sprite(DEFAULT_TILE_SIZE, 4, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_ROCK_SAND = new Sprite(DEFAULT_TILE_SIZE, 5, 2, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_GRASS = new Sprite(DEFAULT_TILE_SIZE, 0, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_SAND = new Sprite(DEFAULT_TILE_SIZE, 2, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_WATER_0 = new Sprite(DEFAULT_TILE_SIZE, 3, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_WATER_1 = new Sprite(DEFAULT_TILE_SIZE, 4, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_WATER_2 = new Sprite(DEFAULT_TILE_SIZE, 5, 1, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_WATER_3 = new Sprite(DEFAULT_TILE_SIZE, 6, 1, SpriteSheet.SPRITE_SHEET);

	//Arena sprites
	public static final Sprite SPRITE_BOOSTER = new Sprite(DEFAULT_TILE_SIZE, 1, 4, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_CHECKPOINT = new Sprite(DEFAULT_TILE_SIZE, 5, 4, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_ICE = new Sprite(DEFAULT_TILE_SIZE, 2, 4, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_KILLER = new Sprite(DEFAULT_TILE_SIZE, 3, 4, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_QUARTZ = new Sprite(DEFAULT_TILE_SIZE, 0, 4, SpriteSheet.SPRITE_SHEET);
	public static final Sprite SPRITE_QUARTZ_WALL = new Sprite(DEFAULT_TILE_SIZE, 4, 4, SpriteSheet.SPRITE_SHEET);

	//Particles
	public static final Sprite[] PARTICLE_SLIME = { new Sprite(2, 0x3E7CC2), new Sprite(3, 0x3371B6), new Sprite(2, 0x2160A6),
			new Sprite(2, 0x205DA1) };
	public static final Sprite[] PARTICLE_BLOOD = { new Sprite(2, 0xEB3D3D), new Sprite(3, 0xEE4D59), new Sprite(3, 0xEE4D7F),
			new Sprite(2, 0xFF0048) };
	public static final Sprite[] PARTICLE_SPARK = { new Sprite(1, 0xEDD92D), new Sprite(2, 0xEDB02D), new Sprite(2, 0xED6C2D),
			new Sprite(1, 0xF3D650) };

	//Explosion
	public static final Sprite[] EXPLOSION = { new Sprite(DEFAULT_TILE_SIZE, 55, 63, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 55, 62, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 55, 61, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 55, 60, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 55, 59, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 55, 58, SpriteSheet.SPRITE_SHEET) };

	//Traps
	public static final Sprite TRAP_EXPLOSIVE_1 = new Sprite(DEFAULT_TILE_SIZE, 57, 62, SpriteSheet.SPRITE_SHEET);
	public static final Sprite TRAP_EXPLOSIVE_2 = new Sprite(DEFAULT_TILE_SIZE, 57, 61, SpriteSheet.SPRITE_SHEET);

	//Projectiles
	public static final Sprite PROJECTILE_BULLET = new Sprite(DEFAULT_TILE_SIZE, 57, 63, SpriteSheet.SPRITE_SHEET);
	public static final Sprite PROJECTILE_BOOMERANG = new Sprite(DEFAULT_TILE_SIZE, 58, 63, SpriteSheet.SPRITE_SHEET);
	public static final Sprite PROJECTILE_GUARDIAN = new Sprite(DEFAULT_TILE_SIZE, 56, 63, SpriteSheet.SPRITE_SHEET);
	public static final Sprite PROJECTILE_GRANADE = new Sprite(DEFAULT_TILE_SIZE, 58, 62, SpriteSheet.SPRITE_SHEET);

	//Items
	public static final Sprite ITEM_HEALTH = new Sprite(DEFAULT_TILE_SIZE, 0, 6, SpriteSheet.SPRITE_SHEET);
	public static final Sprite[] COIN = { new Sprite(DEFAULT_TILE_SIZE, 1, 9, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 1, 8, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 1, 7, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 1, 6, SpriteSheet.SPRITE_SHEET) };
	public static final Sprite ITEM_ARMOUR_LEATHER = new Sprite(DEFAULT_TILE_SIZE, 2, 6, SpriteSheet.SPRITE_SHEET);
	//Item skills
	public static final Sprite ITEM_SKILL_TELEPORTING = new Sprite(DEFAULT_TILE_SIZE, 3, 6, SpriteSheet.SPRITE_SHEET);
	public static final Sprite ITEM_SKILL_SPEED = new Sprite(DEFAULT_TILE_SIZE, 3, 7, SpriteSheet.SPRITE_SHEET);
	public static final Sprite ITEM_SKILL_RAGE = new Sprite(DEFAULT_TILE_SIZE, 3, 8, SpriteSheet.SPRITE_SHEET);

	//GUIs
	public static final Sprite INVENTORY = new Sprite(864, 16, 160, 109, SpriteSheet.SPRITE_SHEET);
	public static final Sprite INVENTORY_SHOP = new Sprite(864, 127, 160, 109, SpriteSheet.SPRITE_SHEET);

	//Bars
	public static final Sprite BAR_EMPTY = new Sprite(DEFAULT_TILE_SIZE, 63, 0, SpriteSheet.SPRITE_SHEET);
	public static final Sprite BAR_SECONDARY = new Sprite(DEFAULT_TILE_SIZE, 62, 0, SpriteSheet.SPRITE_SHEET);
	public static final Sprite BAR_PASSIVE = new Sprite(DEFAULT_TILE_SIZE, 61, 0, SpriteSheet.SPRITE_SHEET);
	public static final Sprite BAR_HEALTH = new Sprite(DEFAULT_TILE_SIZE, 60, 0, SpriteSheet.SPRITE_SHEET);

	//Player
	public static final Sprite[] PLAYER_DOWN = { new Sprite(DEFAULT_TILE_SIZE, 63, 63, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 63, 62, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 63, 61, SpriteSheet.SPRITE_SHEET) };
	public static final Sprite[] PLAYER_UP = { new Sprite(DEFAULT_TILE_SIZE, 62, 63, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 62, 62, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 62, 61, SpriteSheet.SPRITE_SHEET) };
	public static final Sprite[] PLAYER_LEFT = { new Sprite(DEFAULT_TILE_SIZE, 61, 63, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 61, 62, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 61, 61, SpriteSheet.SPRITE_SHEET) };
	public static final Sprite[] PLAYER_RIGHT = { new Sprite(DEFAULT_TILE_SIZE, 60, 63, SpriteSheet.SPRITE_SHEET),
			new Sprite(DEFAULT_TILE_SIZE, 60, 62, SpriteSheet.SPRITE_SHEET), new Sprite(DEFAULT_TILE_SIZE, 60, 61, SpriteSheet.SPRITE_SHEET) };

	//Salesman
	public static final Sprite SALESMAN = new Sprite(DEFAULT_TILE_SIZE, 63, 60, SpriteSheet.SPRITE_SHEET);

	//Guardian
	public static final Sprite GUARDIAN_RECOVERED = new Sprite(DEFAULT_TILE_SIZE, 59, 62, SpriteSheet.SPRITE_SHEET);
	public static final Sprite GUARDIAN_EMPTY = new Sprite(DEFAULT_TILE_SIZE, 59, 61, SpriteSheet.SPRITE_SHEET);

	//Slime
	public static final Sprite SLIME_DOWN = new Sprite(DEFAULT_TILE_SIZE, 59, 63, SpriteSheet.SPRITE_SHEET);

	//Text
	private static final String[] LETTER_ORDER = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "?", "!", ".", ",", " " };
	private static final Sprite[] LETTERS = { new Sprite(0, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(22, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(45, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(67, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(89, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(112, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(134, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(157, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(179, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(202, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(224, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(246, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(269, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(291, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(313, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(336, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(358, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(381, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(403, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(425, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(448, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(470, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(493, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(515, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(537, 994, 22, 30, SpriteSheet.SPRITE_SHEET),
			new Sprite(560, 994, 22, 30, SpriteSheet.SPRITE_SHEET), new Sprite(0, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(22, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(45, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(67, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(89, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(112, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(134, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(157, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(179, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(202, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(226, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(246, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(269, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(291, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(313, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(336, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(358, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(381, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(403, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(425, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(448, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(470, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(493, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(515, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(537, 960, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(560, 960, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(0, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(22, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(45, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(67, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(89, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(112, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(134, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(157, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(179, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(202, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(224, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(246, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(269, 926, 22, 34, SpriteSheet.SPRITE_SHEET),
			new Sprite(291, 926, 22, 34, SpriteSheet.SPRITE_SHEET), new Sprite(22, 34, 0xFFFF00FF) };

	private static final String[] NUMBER_ORDER = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };

	private static final Sprite[] NUMBERS = { new Sprite(0, 915, 7, 13, SpriteSheet.SPRITE_SHEET),
			new Sprite(8, 915, 7, 13, SpriteSheet.SPRITE_SHEET), new Sprite(16, 915, 7, 13, SpriteSheet.SPRITE_SHEET),
			new Sprite(25, 915, 7, 13, SpriteSheet.SPRITE_SHEET), new Sprite(34, 915, 7, 13, SpriteSheet.SPRITE_SHEET),
			new Sprite(42, 915, 7, 13, SpriteSheet.SPRITE_SHEET), new Sprite(51, 915, 7, 13, SpriteSheet.SPRITE_SHEET),
			new Sprite(59, 915, 7, 13, SpriteSheet.SPRITE_SHEET), new Sprite(68, 915, 7, 13, SpriteSheet.SPRITE_SHEET),
			new Sprite(77, 915, 7, 13, SpriteSheet.SPRITE_SHEET) };

	public static Sprite[] getParticleSpritesFromPosition(int xPos, int yPos, int amountOfParticles)
	{
		if(xPos < 0) xPos = Sprite.DEFAULT_TILE_SIZE * Game.getLevel().getLevelWidth() + 1;
		if(yPos < 0) yPos = Sprite.DEFAULT_TILE_SIZE * Game.getLevel().getLevelHeight() + 1;

		Sprite[] particles = new Sprite[amountOfParticles];
		Tile tile = Game.getLevel().getTile(xPos >> Game.getScreen().TILE_SIZE_SHIFTING, yPos >> Game.getScreen().TILE_SIZE_SHIFTING);

		xPos = xPos % Tile.DEFAULT_TILE_SIZE;
		yPos = yPos % Tile.DEFAULT_TILE_SIZE;

		int rgb = tile.getSprite() != null ? tile.getSprite().pixels[xPos + yPos * tile.getSprite().width] : 0;

		int r = (rgb & 0xFF0000) >> 16;
		int g = (rgb & 0x00FF00) >> 8;
		int b = (rgb & 0x0000FF);

		for(int i = 0; i < particles.length; i++)
		{
			int colour = ((int) (r * (0.75 + rand.nextFloat() / 4)) << 16) | ((int) (g * (0.75 + rand.nextFloat() / 4)) << 8)
					| ((int) (b * (0.75 + rand.nextFloat() / 4)));
			particles[i] = new Sprite(1 + rand.nextInt(3), colour);
		}

		return particles;
	}

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
			for(int j = 0; j < LETTER_ORDER.length; j++)
			{
				if(currentLetter.equals(LETTER_ORDER[j]))
				{
					renderText[i] = LETTERS[j];
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

	/**
	 * Writes small numbers to the screen. Note: The position in the center of
	 * the text
	 * 
	 * @param text
	 * @param screen
	 * @param xPos
	 * @param yPos
	 */
	public static void writeValues(String numbers, Screen screen, int xPos, int yPos, int colour)
	{
		if(numbers == null) return;
		Sprite[] renderText = new Sprite[numbers.length()];
		int renderTextWidth = 0;

		for(int i = 0; i < numbers.length(); i++)
		{
			String currentLetter = numbers.substring(i, i + 1);
			for(int j = 0; j < NUMBER_ORDER.length; j++)
			{
				if(currentLetter.equals(NUMBER_ORDER[j]))
				{
					renderText[i] = NUMBERS[j];
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
