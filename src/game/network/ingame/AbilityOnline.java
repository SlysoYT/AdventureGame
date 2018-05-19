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
package game.network.ingame;

import game.Game;
import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileBoomerang;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SerializationReader;

public class AbilityOnline
{
	public static Ability primaryAbility;
	public static Ability secondaryAbility;
	public static Ability ultimateAbility;

	public static SObject tick(SObject object)
	{
		if(primaryAbility != null)
		{
			if(primaryAbility instanceof AbilityShooting)
			{
				SField ability = SField.Float("abltyShoot", ((AbilityShooting) (primaryAbility)).getAngle());
				object.addField(ability);
				primaryAbility = null;
			}
		}
		if(secondaryAbility != null)
		{
			SField ability = SField.Boolean("abltySec", true);
			object.addField(ability);
			secondaryAbility = null;
		}
		if(ultimateAbility != null)
		{
			SField ability = SField.Boolean("abltyUlt", true);
			object.addField(ability);
			ultimateAbility = null;
		}

		return object;
	}

	public static void recieveAsHost(String IPAddressSender, SObject object)
	{
		Player sender = Game.getLevel().getPlayerByIP(IPAddressSender);

		if(object.findField("abltyShoot") != null)
		{
			Game.getLevel().add(new ProjectileBoomerang(sender.getX(), sender.getY(),
					SerializationReader.readFloat(object.findField("abltyShoot").getData(), 0), sender, null));
		}
	}
}
