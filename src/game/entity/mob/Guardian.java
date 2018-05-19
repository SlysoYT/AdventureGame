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
package game.entity.mob;

import java.util.UUID;

import game.entity.item.ItemCoin;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileGuardian;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class Guardian extends Mob
{
	private final int COOLDOWN = 50;
	private int currentCooldown = COOLDOWN;

	public Guardian(int x, int y, UUID uuid)
	{
		super(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 10.0F, 0.0F, 10.0F, 60, uuid);
		getItemDrop().addItem(new ItemCoin(x, y), 1, 75);
	}

	public void tickMob()
	{
		if(currentCooldown > 0) currentCooldown--;
		else
		{
			Player target = level.getNearestPlayer(this);
			if(target != null)
			{
				if(Math.sqrt(Math.pow(this.x - target.getX(), 2) + Math.pow(this.y - target.getY(), 2)) <= new ProjectileGuardian(0, 0, 0, null, null)
						.getRange())
					shoot(target);
			}
		}
	}

	private void shoot(Player target)
	{
		int predictedTravelTicks = (int) (Math.sqrt(Math.pow(this.x - target.getX(), 2) + Math.pow(this.y - target.getY(), 2))
				/ new ProjectileGuardian(0, 0, 0, null, null).getSpeed());
		int predictedXPos = (int) ((target.getX() - this.x) + predictedTravelTicks * target.getXVelocity());
		int predictedYPos = (int) ((target.getY() - this.y) + predictedTravelTicks * target.getYVelocity());

		double dir = Math.atan2(predictedYPos, predictedXPos);

		level.add(new ProjectileGuardian(x, y, dir, this, null));
		currentCooldown = COOLDOWN;
	}

	public void render(Screen screen)
	{
		if(currentCooldown > 15) sprite = Sprite.GUARDIAN_EMPTY;
		else sprite = Sprite.GUARDIAN_RECOVERED;
		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}
}
