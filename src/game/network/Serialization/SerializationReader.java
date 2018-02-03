package game.network.Serialization;

public class SerializationReader
{
	public static byte readByte(byte[] data, int pointer)
	{
		return data[pointer];
	}

	public static boolean readBoolean(byte[] data, int pointer)
	{
		return data[pointer] != 0;
	}

	public static char readChar(byte[] data, int pointer)
	{
		return (char) ((data[pointer++] & 0xff) << 8 | (data[pointer] & 0xff));
	}

	public static short readShort(byte[] data, int pointer)
	{
		return (short) ((data[pointer++] & 0xff) << 8 | (data[pointer] & 0xff));
	}

	public static int readInt(byte[] data, int pointer)
	{
		return (data[pointer++] & 0xff) << 24 | (data[pointer++] & 0xff) << 16 | (data[pointer++] & 0xff) << 8 | (data[pointer] & 0xff);
	}

	public static float readFloat(byte[] data, int pointer)
	{
		return Float.intBitsToFloat(readInt(data, pointer));
	}

	public static String readString(byte[] data, int pointer, int length)
	{
		//System.out.println(pointer + length + " " + data.length);
		return new String(data, pointer, length);
	}
}
