package game.network.Serialization;

public class SerializationWriter
{
	public static int writeBytes(byte[] destination, int pointer, byte value)
	{
		destination[pointer++] = value;
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, byte[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			destination[pointer++] = src[i];
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, char value)
	{
		destination[pointer++] = (byte) ((value >> 8) & 0xff);
		destination[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, char[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, short value)
	{
		destination[pointer++] = (byte) ((value >> 8) & 0xff);
		destination[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, short[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, int value)
	{
		destination[pointer++] = (byte) ((value >> 24) & 0xff);
		destination[pointer++] = (byte) ((value >> 16) & 0xff);
		destination[pointer++] = (byte) ((value >> 8) & 0xff);
		destination[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, int[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, long value)
	{
		destination[pointer++] = (byte) ((value >> 56) & 0xff);
		destination[pointer++] = (byte) ((value >> 48) & 0xff);
		destination[pointer++] = (byte) ((value >> 40) & 0xff);
		destination[pointer++] = (byte) ((value >> 32) & 0xff);
		destination[pointer++] = (byte) ((value >> 24) & 0xff);
		destination[pointer++] = (byte) ((value >> 16) & 0xff);
		destination[pointer++] = (byte) ((value >> 8) & 0xff);
		destination[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, long[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, float value)
	{
		int data = Float.floatToIntBits(value);
		return writeBytes(destination, pointer, data);
	}

	public static int writeBytes(byte[] destination, int pointer, float[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, double value)
	{
		long data = Double.doubleToLongBits(value);
		return writeBytes(destination, pointer, data);
	}

	public static int writeBytes(byte[] destination, int pointer, double[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, boolean value)
	{
		destination[pointer++] = (byte) (value ? 1 : 0);
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, boolean[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}

	public static int writeBytes(byte[] destination, int pointer, String string)
	{
		return writeBytes(destination, pointer, string.getBytes());
	}

	public static int writeBytes(byte[] destination, int pointer, String[] src)
	{
		for(int i = 0; i < src.length; i++)
		{
			pointer = writeBytes(destination, pointer, src[i]);
		}
		return pointer;
	}
}
