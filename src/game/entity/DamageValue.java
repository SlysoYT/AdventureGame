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
package game.entity;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;

public class DamageValue extends Entity
{
	private float damageValue;
	private float xVelocity = 1 - rand.nextInt(3);
	private float yVelocity = -2.5F;

	public DamageValue(float damage, int x, int y)
	{
		this.damageValue = damage;
		this.x = x;
		this.y = y;
	}

	public void tick()
	{
		x += xVelocity;
		y += yVelocity;
		yVelocity += 0.2F;

		if(yVelocity > 3.5F) this.remove();
	}

	public void render(Screen screen)
	{
		if(damageValue > 999F) return;
		Sprite.writeValues(String.valueOf((int) (damageValue)), screen, x - Game.getScreen().getXOffset(), y - Game.getScreen().getYOffset(), 0x000000);
	}
}
