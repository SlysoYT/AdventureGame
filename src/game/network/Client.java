package game.network;

public class Client
{
	private String IPAddress;

	private boolean isBanned = false;
	private boolean shouldLoadLevel = true;

	public Client(String IPAddress)
	{
		this.IPAddress = IPAddress;
	}

	public String getIPAddress()
	{
		return IPAddress;
	}

	public boolean isBanned()
	{
		return isBanned;
	}

	public boolean shouldLoadLevel()
	{
		return shouldLoadLevel;
	}

	public void ban()
	{
		isBanned = true;
	}

	public void setHasLoadedLevel(boolean hasLoadedLevel)
	{
		this.shouldLoadLevel = !hasLoadedLevel;
	}
}
