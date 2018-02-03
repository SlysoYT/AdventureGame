package game.network;

public class Client
{
	private String IPAddress;
	
	public Client(String IPAddress)
	{
		this.IPAddress = IPAddress;
	}
	
	public String getIPAddress()
	{
		return IPAddress;
	}
}
