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
package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class TileWater extends Tile
{
	public TileWater(Sprite sprite)
	{
		super(sprite);
	}

	public void render(int x, int y, Screen screen)
	{
		boolean xEven = x % 2 == 0;
		boolean yEven = y % 2 == 0;

		if(xEven && yEven) setSprite(Sprite.SPRITE_WATER_0);
		else if(!xEven && yEven) setSprite(Sprite.SPRITE_WATER_1);
		else if(xEven && !yEven) setSprite(Sprite.SPRITE_WATER_2);
		else setSprite(Sprite.SPRITE_WATER_3);

		screen.renderTile(x << TILE_SIZE_SHIFTING, y << TILE_SIZE_SHIFTING, this);
	}

	public boolean liquid()
	{
		return true;
	}
}
