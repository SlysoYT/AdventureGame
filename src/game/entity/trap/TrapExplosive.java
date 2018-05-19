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

import game.entity.explosion.Explosion;
import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class TrapExplosive extends Trap
{
	public TrapExplosive(int x, int y, Mob source, UUID uuid)
	{
		super(x, y, 8.5F, source, new Hitbox(-5, -2, 8, 3), Sprite.TRAP_EXPLOSIVE_1, uuid);
	}

	@Override
	public void tickTrap()
	{
	}

	@Override
	protected void onTrigger(Mob trigger)
	{
		level.add(new Explosion(x, y, null, damage));
		this.remove();
	}

	public void render(Screen screen)
	{
		if(System.nanoTime() % 1_000_000_000 <= 500_000_000) sprite = Sprite.TRAP_EXPLOSIVE_1;
		else sprite = Sprite.TRAP_EXPLOSIVE_2;

		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
