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
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;

public class GetSendDataAsClient
{
	private static boolean disconnect = false;

	public static byte[] getData(SObject object)
	{
		if(disconnect)
		{
			object.addField(SField.Boolean("disconnect", true));
		}
		else
		{
			if(Game.getLevel() == null)
			{
				SString joinPlayer = SString.String("requestJoin", "player123"); //TODO: Custom name
				object.addString(joinPlayer);
				object.addField(SField.Boolean("loadedLevel", false));
			}
			else
			{
				object.addField(SField.Boolean("loadedLevel", true));

				object.addField(SField.Float("xVel", Game.getLevel().getClientPlayer().getXVelocity()));
				object.addField(SField.Float("yVel", Game.getLevel().getClientPlayer().getYVelocity()));

				object = AbilityOnline.tick(object);
			}
		}

		byte[] data = new byte[object.getSize()];
		object.getBytes(data, 0);

		return data;
	}

	public static void disconnect()
	{
		disconnect = true;
	}
}
