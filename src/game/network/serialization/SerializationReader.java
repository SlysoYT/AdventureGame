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
