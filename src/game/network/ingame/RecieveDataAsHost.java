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
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.level.Level;
import game.network.NetworkPackage;
import game.network.Server;
import game.network.serialization.SObject;
import game.network.serialization.SerializationReader;

public class RecieveDataAsHost
{
	public static void recieveData(byte[] data, String IPAddressSender)
	{
		Level level = Game.getLevel();

		if(level == null) return;
		if(Server.isClientBanned(IPAddressSender)) return;

		SObject object = SObject.deserialize(data, 0);
		if(!NetworkPackage.validPacket(object)) return;

		if(!Server.isClientOnline(IPAddressSender))
		{
			if(object.findString("requestJoin") != null)
			{
				Server.addClient(IPAddressSender);

				level.add(new OnlinePlayer(level.getSpawnLocation().getX(), level.getSpawnLocation().getY(), IPAddressSender,
						object.findString("requestJoin").getString()));
				Game.getPrinter().printImportantInfo(((OnlinePlayer) level.getPlayerByIP(IPAddressSender)).getPlayerName() + " joined the game!");
			}

			return;
		}

		if(object.findField("disconnect") != null)
		{
			if(SerializationReader.readBoolean(object.findField("disconnect").getData(), 0))
			{
				Server.removeClient(IPAddressSender);
				level.getPlayerByIP(IPAddressSender).remove();
				Game.getPrinter().printImportantInfo(((OnlinePlayer) level.getPlayerByIP(IPAddressSender)).getPlayerName() + " disconnected from the game!");
			}
		}

		if(object.findField("loadedLevel") != null)
		{
			Server.setClientHasLoadedLevel(IPAddressSender, SerializationReader.readBoolean(object.findField("loadedLevel").getData(), 0));
		}

		Player senderPlayer = level.getPlayerByIP(IPAddressSender);

		if(object.findField("xVel") != null && object.findField("yVel") != null)
		{
			float xVelocity = SerializationReader.readFloat(object.findField("xVel").getData(), 0);
			float yVelocity = SerializationReader.readFloat(object.findField("yVel").getData(), 0);

			//TODO: Anti cheat and stuff

			if(Math.abs(xVelocity) <= senderPlayer.getSpeed() && Math.abs(yVelocity) <= senderPlayer.getSpeed())
				senderPlayer.motion(xVelocity, yVelocity);
			else Game.getPrinter().printInfo("Anti cheat detected illegal movement: " + xVelocity + " " + yVelocity);

			AbilityOnline.recieveAsHost(IPAddressSender, object);
		}
	}
}
