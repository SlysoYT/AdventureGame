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

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import game.input.Keyboard;
import game.level.generation.GenerateLevel;

public class LevelLoader
{
	private int width, height;
	private int[] tiles;
	
	private String levelName;
	private boolean customLevel;
	
	public LevelLoader fromFile(String filePath)
	{
		this.customLevel = true;
		
		try
		{
			BufferedImage image = ImageIO.read(LevelLoader.class.getResource(filePath));
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
		
		return this;
	}

	public LevelLoader fromSeed(long seed)
	{
		this.customLevel = false;
		
		height = width = 512;
		tiles = GenerateLevel.generateLevel(seed);
		
		return this;
	}
	
	public Level load(Keyboard keyboard, String levelName)
	{
		return new Level(width, height, tiles, keyboard, levelName);
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
