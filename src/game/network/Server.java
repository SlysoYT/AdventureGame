package game.network;

import java.util.ArrayList;
import java.util.List;

public class Server
{
	private static List<Client> clients = new ArrayList<Client>();
	private static List<Client> bannedClients = new ArrayList<Client>();

	public static void addClient(String IPAddress)
	{
		for(int i = 0; i < bannedClients.size(); i++)
			if(bannedClients.get(i).getIPAddress().equals(IPAddress)) return;

		clients.add(new Client(IPAddress));
	}

	public static void removeClient(String IPAddress)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getIPAddress().equals(IPAddress))
			{
				clients.remove(i);
				return;
			}
		}
	}

	public static void banClient(String IPAddress)
	{
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
}
