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
import game.graphics.Screens.ScreenInfo;
import game.network.ingame.GetSendDataAsClient;

public class Connection implements Runnable
{
	private Thread connectionThread;
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
		if(!isClient) connectionThread = new Thread(this, "Connection");
	}

	public boolean connect()
	{
		if(!isClient)
		{
			if(!connectAsHost()) return false;
		}
		else prepareClient();

		if(isClient && ip == null) return false;

		networkPackage = new NetworkPackage(isClient);

		if(isClient) tick();
		else startThread();

		if(isClient && !connectionEstablished) return false;
		return true;
	}

	private boolean connectAsHost()
	{
		try
		{
			hostSocket = new DatagramSocket(port);
			hostSocket.setSoTimeout(1000);
			isClient = false;

			ip = getPublicIP();

			if(ip == null)
			{
				Game.getPrinter().printImportantInfo("You are not connected to the internet!");
				Game.getPrinter().printImportantInfo("Started server, local IP: " + InetAddress.getLocalHost().getHostAddress());
				return true;
			}

			Game.getPrinter().printImportantInfo("Started server, IP: " + ip + " or " + InetAddress.getLocalHost().getHostAddress() + " (local)");
			return true;
		}
		catch(IOException e)
		{
			Game.getPrinter().printError(e.getMessage());
			return false;
		}
	}

	private void prepareClient()
	{
		try
		{
			clientSocket = new DatagramSocket(null);
			clientSocket.setSoTimeout(2000);
		}
		catch(SocketException e)
		{
			Game.getPrinter().printError(e.getMessage());
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
				connectionEstablished = false;
				String message;

				if(e instanceof UnknownHostException) message = "Unknown host!";
				else if(e instanceof SocketTimeoutException) message = "Connection timeout!";
				else message = "Network is unreachable!";

				Game.getPrinter().printWarning(message);
				ScreenInfo.setInfoMessage(message);
			}
		}
		else
		{
			try
			{
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

					if(!(e instanceof SocketTimeoutException)) e.printStackTrace();
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
			Game.getPrinter().printWarning("Couldn't connect, " + e.getMessage());
			return null;
		}
	}

	public void close()
	{
		if(isClient && !connectionEstablished) return;

		if(isClient)
		{
			GetSendDataAsClient.disconnect();
			tick();
			clientSocket.close();
		}
		else
		{
			running = false;

			try
			{
				connectionThread.join();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		Game.getPrinter().printInfo("Connection closed");
	}

	public boolean isClient()
	{
		return isClient;
	}

	private synchronized void startThread()
	{
		running = true;
		connectionThread = new Thread(this, "Connection");
		connectionThread.start();
	}

	public synchronized void run()
	{
		while(running)
		{
			tick(); //Runs at same TPS as the game
		}

		hostSocket.close();
	}
}
