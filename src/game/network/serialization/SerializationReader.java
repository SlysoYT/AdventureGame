package game.network.serialization;

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
		return (char) ((data[pointer++] & 0xFF) << 8 | (data[pointer] & 0xFF));
	}

	public static short readShort(byte[] data, int pointer)
	{
		return (short) ((data[pointer++] & 0xFF) << 8 | (data[pointer] & 0xFF));
	}

	public static int readInt(byte[] data, int pointer)
	{
		return (data[pointer++] & 0xFF) << 24 | (data[pointer++] & 0xFF) << 16 | (data[pointer++] & 0xFF) << 8 | (data[pointer] & 0xFF);
	}

	public static long readLong(byte[] data, int pointer)
	{
		long val = 0;
		for(int i = 0; i < 8; i++)
		{
			val <<= 8;
			val |= (data[i] & 0xFF);
		}
		return val;
	}

	public static float readFloat(byte[] data, int pointer)
	{
		return Float.intBitsToFloat(readInt(data, pointer));
	}

	public static String readString(byte[] data, int pointer, int length)
	{
		return new String(data, pointer, length);
	}
}
