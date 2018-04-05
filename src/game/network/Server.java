package game.network;

import java.util.ArrayList;
import java.util.List;

import game.Game;

public class Server
{
	private static List<Client> clients = new ArrayList<Client>();
	private static List<Client> bannedClients = new ArrayList<Client>();

	public static void addClient(String IPAddress)
	{
		if(isClientBanned(IPAddress)) return;
		clients.add(new Client(IPAddress));
	}

	public static void kickClient(String IPAddress)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getIPAddress().equals(IPAddress))
			{
				Game.getLevel().getPlayerByIP(IPAddress).remove();
				clients.remove(i);
				return;
			}
		}
	}

	public static void banClient(String IPAddress)
	{
		if(Game.getLevel().getPlayerByIP(IPAddress) != null) Game.getLevel().getPlayerByIP(IPAddress).remove();
		bannedClients.add(new Client(IPAddress));
	}

	public static boolean isClientOnline(String IPAddress)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getIPAddress().equals(IPAddress)) return true;
		}
		return false;
	}

	public static boolean isClientBanned(String IPAddress)
	{
		for(int i = 0; i < bannedClients.size(); i++)
			if(bannedClients.get(i).getIPAddress().equals(IPAddress)) return true;

		return false;
	}
}
