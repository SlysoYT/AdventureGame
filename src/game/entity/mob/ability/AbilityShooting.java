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
package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileBoomerang;
import game.entity.projectile.ProjectileBullet;
import game.entity.projectile.ProjectileGranade;
import game.entity.projectile.ProjectileGuardian;
import game.entity.projectile.Projectiles;
import game.input.Mouse;
import game.util.GameState;

public class AbilityShooting extends Ability
{
	Projectiles projectile;
	double angle;

	public AbilityShooting(Player player, Projectiles projectile, int cooldown)
	{
		super(player, cooldown, AbilityType.Primary);
		this.projectile = projectile;
	}

	@Override
	protected void onEnable()
	{
		int deltaX = Mouse.getX() / Game.SCALE - Game.getLevel().getClientPlayer().getX() + Game.getScreen().getXOffset();
		int deltaY = Mouse.getY() / Game.SCALE - Game.getLevel().getClientPlayer().getY() + Game.getScreen().getYOffset();

		angle = Math.atan2(deltaY, deltaX); //Atan = tan^-1, difference to atan: doesn't crash when dividing by 0, = atan(deltaY / deltaX)

		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(projectile == Projectiles.ProjectileBoomerang)
				player.shoot(new ProjectileBoomerang(player.getX(), player.getY(), angle, player, null));
			else if(projectile == Projectiles.ProjectileBullet) player.shoot(new ProjectileBullet(player.getX(), player.getY(), angle, player, null));
			else if(projectile == Projectiles.ProjectileGuardian)
				player.shoot(new ProjectileGuardian(player.getX(), player.getY(), angle, player, null));
			else if(projectile == Projectiles.ProjectileGranade)
				player.shoot(new ProjectileGranade(player.getX(), player.getY(), angle, player, null));
		}
		else
		{
			//if(projectile instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			//TODO
		}
	}

	public float getAngle()
	{
		return (float) angle;
	}
}
