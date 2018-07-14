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
import game.entity.mob.ability.AbilityTeleporting;
import game.entity.mob.player.Player;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityTeleporting extends Item
{
	public ItemAbilityTeleporting(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(0, 1, 14, 14), Sprite.ITEM_SKILL_TELEPORTING);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}

	public Ability getItemAbility(Player player)
	{
		return new AbilityTeleporting(player, 170, 200);
	}
}
