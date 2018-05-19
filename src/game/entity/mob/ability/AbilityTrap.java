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
import game.entity.trap.TrapBounce;
import game.entity.trap.TrapExplosive;
import game.entity.trap.Traps;
import game.util.GameState;

public class AbilityTrap extends Ability
{
	Traps trap;

	public AbilityTrap(Player player, Traps trap, int cooldown)
	{
		super(player, cooldown, AbilityType.Secondary);
		this.trap = trap;
	}

	@Override
	protected void onEnable()
	{
		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(trap.ordinal() == 0) Game.getLevel().add(new TrapBounce(player.getX(), player.getY(), player, null));
			else if(trap.ordinal() == 1) Game.getLevel().add(new TrapExplosive(player.getX(), player.getY(), player, null));
		}
		else
		{
			//if(trap instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			//TODO
		}
	}
}
