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
import game.entity.mob.effect.EffectMovementSpeed;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileBoomerang extends Projectile
{
	private int liveTime = 0;

	public ProjectileBoomerang(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 1.8D, 250, 0.0F, source, new Hitbox(-3, -3, 5, 5), Sprite.PROJECTILE_BOOMERANG, Projectiles.ProjectileBoomerang, uuid);
	}

	@Override
	protected void tickProjectile()
	{
		liveTime++;
		damage = 3 + liveTime / 5;

		double deltaX = this.getX() - getSource().getX();
		double deltaY = this.getY() - getSource().getY();

		if(distance > 70) speed -= 0.05D;
		if(speed < 0)
		{
			distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
			angle = Math.atan2(deltaY, deltaX);

			//Player catched projectile
			if(distance < 3.0D)
			{
				this.remove();
				getSource().applyEffect(new EffectMovementSpeed(120, 2, getSource()));
			}
		}
	}

	@Override
	protected void onMobHit(Mob mob)
	{
		mob.motion((float) (2 * speed * Math.cos(angle)), (float) (2 * speed * Math.sin(angle)));
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
