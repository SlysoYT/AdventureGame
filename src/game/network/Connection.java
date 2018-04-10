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
import game.network.ingame.GetSendDataAsClient;
import game.settings.Settings;
import game.util.GameState;
import game.util.Print;

public class Connection implements Runnable
{
	private Thread thread;
	private boolean running = false;

	private DatagramSocket hostSocket = null;
	private DatagramSocket clientSocket = null;
	private NetworkPackage networkPackage = null;

	private String ip;
	private int port = 22592;
	private final int packetSize = 4096;
	private boolean isClient;
	public boolean connectionEstablished = false;

	private byte[] receiveData;

	public Connection(String ip, boolean asHost)
	{
		this.ip = ip;
		isClient = !asHost;
		if(!isClient) thread = new Thread(this, "Connection");
	}

	public void connect()
	{
		if(!isClient) connectAsHost();
		else connectAsClient();

		if(ip == null) return;

		networkPackage = new NetworkPackage(isClient);

		if(isClient) tick();
		else startThread();
	}

	private void connectAsHost()
	{
		try
		{
			hostSocket = new DatagramSocket(port);
			hostSocket.setSoTimeout(1000);
			isClient = false;
			if(ip == null) ip = getPublicIP();
			if(ip == null)
			{
				Print.printError("You are not connected to the internet!");
				close();
				Game.setGameState(GameState.TitleScreen);
				return;
			}

			Print.printImportantInfo("Started server, IP: " + ip + " or " + InetAddress.getLocalHost());
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
			clientSocket.setSoTimeout(2000);
		}
		catch(SocketException e)
		{
			Print.printError(e.getMessage());
		}
	}

	public void tick()
	{
		if(isClient)
		{
			try
			{
				receiveData = new byte[packetSize];

				InetAddress IPAddress = InetAddress.getByName(ip);

				//Send data
				byte[] data = networkPackage.getSendData(IPAddress.getHostName());
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
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
					Print.printError("Unknown host!");
					Game.setGameState(GameState.TitleScreen);
				}
				else if(e instanceof SocketTimeoutException)
				{
					Print.printError("Connection failed! Server not found!");
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
				receiveData = new byte[packetSize];

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try
				{
					hostSocket.receive(receivePacket); //Waits for client to connect
					connectionEstablished = true;
				}
				catch(IOException e)
				{
					connectionEstablished = false;
					e.printStackTrace();
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
			Print.printError("Couldn't connect, " + e.getMessage());
			return null;
		}
	}

	public void close()
	{
		if(isClient) GetSendDataAsClient.disconnect();
		tick();

		if(isClient) clientSocket.close();
		else hostSocket.close();
		if(!isClient) stopThread();
		Print.printInfo("Connection closed");
	}

	public boolean isClient()
	{
		return isClient;
	}

	private synchronized void startThread()
	{
		running = true;
		thread = new Thread(this, "Connection");
		thread.start();
	}

	private synchronized void stopThread()
	{
		running = false;

		try
		{
			thread.join();
		}
		catch(InterruptedException e)
		{
			Print.printError(e.getMessage());
		}
	}

	public void run()
	{
		while(running)
		{
			tick(); //Runs at same TPS as the game
		}
	}
}
