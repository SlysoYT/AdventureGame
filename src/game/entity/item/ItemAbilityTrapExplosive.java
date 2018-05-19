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
package game.entity.item;

import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityTrap;
import game.entity.mob.player.Player;
import game.entity.trap.Traps;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityTrapExplosive extends Item
{
	public ItemAbilityTrapExplosive(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(3, 7, 8, 3), Sprite.TRAP_EXPLOSIVE_1);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}

	public Ability getItemAbility(Player player)
	{
		return new AbilityTrap(player, Traps.TrapExplosive, 70);
	}
}
