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
package game.util;

import game.level.tile.Tile;

public class TileCoordinate
{
	private int x, y;
	
	public TileCoordinate(int x, int y)
	{
		this.x = x * Tile.DEFAULT_TILE_SIZE;
		this.y = y * Tile.DEFAULT_TILE_SIZE;
	}
	
	public TileCoordinate(int x, int y, Hitbox hitbox)
	{
		this.x = x * Tile.DEFAULT_TILE_SIZE + Math.abs(hitbox.getXOffset());
		this.y = y * Tile.DEFAULT_TILE_SIZE + Math.abs(hitbox.getYOffset());
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
