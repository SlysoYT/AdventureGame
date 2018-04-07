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
