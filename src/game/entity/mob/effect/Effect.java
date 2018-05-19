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
package game.entity.mob.effect;

import game.entity.mob.Mob;
import game.entity.mob.player.Player;

public abstract class Effect
{
	private int duration;
	private int passedTime;
	private boolean active;
	protected int amplifier;
	protected Mob mob;
	protected Player player;

	protected abstract void tickEffect();

	protected abstract void onEnable();

	protected abstract void onDisable();

	protected Effect(int duration, int amplifier, Mob mob)
	{
		this.duration = duration;
		this.amplifier = amplifier;
		this.mob = mob;

		onEnable();
		active = true;
	}

	protected Effect(int duration, int amplifier, Player player)
	{
		this.duration = duration;
		this.amplifier = amplifier;
		this.player = player;

		onEnable();
		active = true;
	}

	public void tick()
	{
		if(passedTime >= duration)
		{
			onDisable();
			active = false;
		}
		else tickEffect();

		passedTime++;
	}

	public boolean isActive()
	{
		return active;
	}
}
