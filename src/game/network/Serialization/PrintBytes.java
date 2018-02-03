package game.network.Serialization;

public class PrintBytes
{
	public static void printBytes(byte[] data)
	{
		for(int i = 0; i < data.length; i++)
		{
			System.out.printf("0x%x ", data[i]);
		}
		System.out.println("");
	}
}
