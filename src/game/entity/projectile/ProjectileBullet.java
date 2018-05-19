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
package game.entity.projectile;

import java.util.UUID;

import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileBullet extends Projectile
{
	public ProjectileBullet(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 2.5D, 100, 7.0F, source, new Hitbox(-2, -2, 3, 3), Sprite.PROJECTILE_BULLET, Projectiles.ProjectileBullet, uuid);
	}

	@Override
	protected void tickProjectile()
	{
	}

	@Override
	protected void onMobHit(Mob mob)
	{
		mob.damage(damage);
	}

	@Override
	protected void onTileCollisioin()
	{
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
