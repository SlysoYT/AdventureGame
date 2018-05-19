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

import java.util.ArrayList;
import java.util.List;

import game.Game;

public class Server
{
	private static List<Client> clients = new ArrayList<Client>();

	public static void addClient(String IPAddress)
	{
		if(isClientBanned(IPAddress)) return;
		clients.add(new Client(IPAddress));
	}

	private static Client getClient(String IPAddress)
	{
		for(Client client : clients)
			if(client.getIPAddress().equals(IPAddress)) return client;

		return null;
	}

	public static void kickClient(String IPAddress)
	{
		Client client = getClient(IPAddress);
		if(client != null)
		{
			Game.getLevel().getPlayerByIP(IPAddress).remove();
			clients.remove(client);
		}
	}

	public static void removeClient(String IPAddress)
	{
		Client client = getClient(IPAddress);
		if(client != null) clients.remove(client);
	}

	public static void banClient(String IPAddress)
	{
		if(isClientOnline(IPAddress)) Game.getLevel().getPlayerByIP(IPAddress).remove();

		Client client = getClient(IPAddress);
		if(client != null) client.ban();
	}

	public static boolean isClientOnline(String IPAddress)
	{
		if(getClient(IPAddress) == null) return false;
		return true;
	}

	public static boolean clientShouldLoadLevel(String IPAddress)
	{
		Client client = getClient(IPAddress);

		if(client != null) return client.shouldLoadLevel();
		return false;
	}

	public static void setClientHasLoadedLevel(String IPAddress, boolean loadedLevel)
	{
		Client client = getClient(IPAddress);
		if(client != null) client.setHasLoadedLevel(loadedLevel);
	}

	public static boolean isClientBanned(String IPAddress)
	{
		Client client = getClient(IPAddress);
		if(client != null) return client.isBanned();

		return false;
	}
}
