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

import game.Game;

public abstract class GUI
{
	protected Sprite sprite;

	public GUI(Sprite sprite)
	{
		this.sprite = sprite;
	}
	
	protected final int getXOffset()
	{
		return Game.width / 2 - sprite.getWidth() / 2;
	}
	
	protected final int getYOffset()
	{
		return Game.height / 2 - sprite.getHeight() / 2;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
}
