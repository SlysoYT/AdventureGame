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
package game.entity.trap;

import java.util.UUID;

import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class TrapBounce extends Trap
{
	public TrapBounce(int x, int y, Mob source, UUID uuid)
	{
		super(x, y, 0F, source, new Hitbox(-3, -3, 6, 6), Sprite.SPRITE_CHECKPOINT, uuid);
	}

	@Override
	public void tickTrap()
	{
	}

	@Override
	protected void onTrigger(Mob trigger)
	{
		double deltaX = trigger.getX() - x;
		double deltaY = trigger.getY() - y;

		if(deltaX < 0) trigger.motion(-4.5F, 0F);
		else trigger.motion(4.5F, 0F);

		if(deltaY < 0) trigger.motion(0F, -4.5F);
		else trigger.motion(0F, 4.5F);
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
