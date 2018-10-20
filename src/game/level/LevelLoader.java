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
