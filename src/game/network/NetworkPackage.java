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
package game.network;

import game.Game;
import game.network.ingame.GetSendDataAsClient;
import game.network.ingame.GetSendDataAsHost;
import game.network.ingame.RecieveDataAsClient;
import game.network.ingame.RecieveDataAsHost;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SerializationReader;

public class NetworkPackage
{
	private static final short VERSION = 201;
	private boolean isClient;

	public NetworkPackage(boolean isClient)
	{
		this.isClient = isClient;
	}

	public byte[] getSendData(String IPAddress)
	{
		SObject object = new SObject("Debug");
		SField version = SField.Short("version", VERSION);
		object.addField(version);

		if(isClient) return GetSendDataAsClient.getData(object);
		else return GetSendDataAsHost.getData(object, IPAddress);
	}

	public void recieveDataAsClient(byte[] data)
	{
		RecieveDataAsClient.recieve(data);
	}

	public void recieveDataAsHost(byte[] data, String IPAddressSender)
	{
		RecieveDataAsHost.recieveData(data, IPAddressSender);
	}

	public static boolean validPacket(SObject object)
	{
		if(object == null)
		{
			Game.getPrinter().printError("Failed to deserialize!");
			return false;
		}

		if(object.getName().equals("Debug") && object.findField("version") != null)
		{
			if(SerializationReader.readShort(object.findField("version").getData(), 0) == VERSION) return true;

			Game.getPrinter().printError("Client version not matching with server version!");
			return false;
		}

		Game.getPrinter().printError("Invalid packet!");
		return false;
	}
}
