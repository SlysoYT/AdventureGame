package game.network;

import game.network.ingame.GetSendDataAsClient;
import game.network.ingame.GetSendDataAsHost;
import game.network.ingame.RecieveDataAsClient;
import game.network.ingame.RecieveDataAsHost;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SerializationReader;
import game.util.Print;

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
			Print.printError("Failed to deserialize!");
			return false;
		}

		if(object.getName().equals("Debug") && object.findField("version") != null)
		{
			if(SerializationReader.readShort(object.findField("version").getData(), 0) == VERSION) return true;

			Print.printError("Client version not matching with server version!");
			return false;
		}

		Print.printError("Invalid packet!");
		return false;
	}
}
