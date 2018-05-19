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

public class SString
{
	private static final byte CONTAINER_TYPE = (byte) ContainerType.String.ordinal();
	private short nameLength;
	private byte[] name;
	private int size = 0;
	private int count = 0;

	private String string;

	private SString()
	{
	}

	public void setName(String name)
	{
		nameLength = (short) name.length();
		this.name = name.getBytes();
	}

	public int getBytes(byte[] destination, int pointer)
	{
		pointer = SerializationWriter.writeBytes(destination, pointer, CONTAINER_TYPE);
		pointer = SerializationWriter.writeBytes(destination, pointer, nameLength);
		pointer = SerializationWriter.writeBytes(destination, pointer, name);
		pointer = SerializationWriter.writeBytes(destination, pointer, size);
		pointer = SerializationWriter.writeBytes(destination, pointer, count);
		pointer = SerializationWriter.writeBytes(destination, pointer, string);

		return pointer;
	}

	public int getSize()
	{
		return 1 + 2 + name.length + 4 + 4 + string.length();
	}

	public String getName()
	{
		return SerializationReader.readString(name, 0, nameLength);
	}

	public String getString()
	{
		return string;
	}

	public static SString String(String name, String data)
	{
		SString string = new SString();
		string.setName(name);
		string.count = data.length();
		string.string = data;
		return string;
	}

	public static SString desirialize(byte[] data, int pointer)
	{
		byte containerType = SerializationReader.readByte(data, pointer);
		pointer++;
		if(containerType != ContainerType.String.ordinal())
		{
			System.out.println(containerType);
			return null;
		}

		SString string = new SString();

		string.nameLength = SerializationReader.readShort(data, pointer);
		pointer += 2;
		string.name = SerializationReader.readString(data, pointer, string.nameLength).getBytes();
		pointer += string.nameLength;
		string.size = SerializationReader.readInt(data, pointer);
		pointer += 4;
		string.count = SerializationReader.readInt(data, pointer);
		pointer += 4;
		string.string = SerializationReader.readString(data, pointer, string.count);

		return string;
	}
}
