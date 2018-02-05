package game.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import game.Game;
import game.level.Level;
import game.settings.Settings;
import game.util.GameState;
import game.util.Print;

public class Connection
{
	private DatagramSocket hostSocket = null;
	private DatagramSocket clientSocket = null;
	private NetworkPackage networkPackage = null;

	private String ip;
	private int port = 22592;
	private boolean isClient;
	public boolean connectionEstablished = false;

	private byte[] receiveData;

	public Connection(String ip, boolean asHost)
	{
		this.ip = ip;
		isClient = !asHost;
	}

	private void connect()
	{
		if(!isClient) connectAsHost();
		else connectAsClient();
		networkPackage = new NetworkPackage(isClient);
	}

	private void connectAsHost()
	{
		try
		{
			hostSocket = new DatagramSocket(port);
			isClient = false;
			if(ip == null) ip = getPublicIP();
			if(ip == null)
			{
				System.out.println("You are not connected to the internet!");
				Game.setGameState(GameState.TitleScreen);
				return;
			}

			Print.printInfo("Started server, IP: " + ip + " or " + InetAddress.getLocalHost());
		}
		catch(IOException e)
		{
			Print.printError(e.getMessage());
		}
	}

	private void connectAsClient()
	{
		try
		{
			clientSocket = new DatagramSocket(null);
		}
		catch(SocketException e)
		{
			Print.printError(e.getMessage());
		}
	}

	public void tick(Level level)
	{
		if(level == null) connect();

		if(isClient)
		{
			try
			{
				receiveData = new byte[1024];

				networkPackage.tick(level);

				InetAddress IPAddress = InetAddress.getByName(ip);
				//TODO: Try with binding or something
				//InetSocketAddress address = new InetSocketAddress("192.168.103.255", 3000);
				//clientSocket.bind(addr);

				//Send data
				byte[] data = networkPackage.getSendData(IPAddress.getHostName());
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
				clientSocket.setSoTimeout(10000);
				clientSocket.send(sendPacket);

				//Recieve data
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				networkPackage.recieveDataAsClient(receiveData);
				connectionEstablished = true;
			}
			catch(IOException e)
			{
				if(e instanceof UnknownHostException)
				{
					System.out.println("Unknown host!");
					Game.setGameState(GameState.TitleScreen);
				}
				else if(e instanceof SocketTimeoutException)
				{
					System.out.println("Connection failed!");
					Game.setGameState(GameState.TitleScreen);
				}
				connectionEstablished = false;
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				if(!Settings.serverIsPublic) return;
				receiveData = new byte[1024];

				networkPackage.tick(level);

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				hostSocket.setSoTimeout(3000);
				try
				{
					hostSocket.receive(receivePacket); //Waits for client to connect
					connectionEstablished = true;
				}
				catch(IOException e)
				{
					connectionEstablished = false;
					e.printStackTrace();
					close();
					return;
				}

				InetAddress IPAddress = receivePacket.getAddress();

				//Recieve data
				int port = receivePacket.getPort();
				networkPackage.recieveDataAsHost(receiveData, IPAddress.getHostName());

				//Send data
				byte[] data = networkPackage.getSendData(IPAddress.getHostName());
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
				hostSocket.send(sendPacket);
			}
			catch(IOException e)
			{
				if(e instanceof SocketException) connectAsHost();
				e.printStackTrace();
			}
		}
	}

	private String getPublicIP()
	{
		try
		{
			URL whatismyip;
			whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		}
		catch(IOException e)
		{
			Print.printError(e.getMessage());
			return null;
		}
	}

	public void close()
	{
		if(isClient) clientSocket.close();
		else hostSocket.close();

		System.out.println("Connection closed");
	}

	public boolean isClient()
	{
		return isClient;
	}
}
