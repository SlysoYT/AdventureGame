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

import game.entity.mob.effect.EffectAbilityCooldown;
import game.entity.mob.effect.EffectMovementSpeed;
import game.entity.mob.player.Player;

public class AbilitySpeed extends Ability
{
	private int amplifier;
	private int duration;

	public AbilitySpeed(Player player, int cooldown, int duration, int amplifier)
	{
		super(player, cooldown, AbilityType.Secondary);
		this.amplifier = amplifier;
		this.duration = duration;
	}

	@Override
	protected void onEnable()
	{
		player.applyEffect(new EffectMovementSpeed(duration, amplifier, player));
		player.applyEffect(new EffectAbilityCooldown(duration, amplifier, player));
	}
}
