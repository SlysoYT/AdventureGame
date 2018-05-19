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

import game.entity.mob.player.Player;

public abstract class AbilityDuration extends Ability
{
	private boolean enabled = false;
	protected int duration;
	private int durationLeft;

	protected abstract void onDisable();

	protected abstract void tickAbility();

	public AbilityDuration(Player player, int cooldown, int duration, AbilityType type)
	{
		super(player, cooldown, type);

		if(duration > 0) this.duration = duration;
		else this.duration = 1;

		this.durationLeft = this.duration;
	}

	public void enable()
	{
		if(currentCooldown <= 0)
		{
			enabled = true;
			onEnable();
			currentCooldown = cooldown;
			durationLeft = duration;
		}
	}

	public void tick()
	{
		tickAbility();

		if(!enabled && currentCooldown > 0) currentCooldown--;
		if(enabled)
		{
			if(durationLeft > 0) durationLeft--;
			else
			{
				enabled = false;
				onDisable();
				currentCooldown = cooldown;
			}
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
