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

import static game.network.serialization.SerializationWriter.writeBytes;

public class SField
{
	private static final byte CONTAINER_TYPE = (byte) ContainerType.Field.ordinal();
	private short nameLength;
	private byte[] name;
	private byte type;
	private byte data[];

	private SField()
	{
	}

	public void setName(String name)
	{
		nameLength = (short) name.length();
		this.name = name.getBytes();
	}

	public int getBytes(byte[] destination, int pointer)
	{
		pointer = writeBytes(destination, pointer, CONTAINER_TYPE);
		pointer = writeBytes(destination, pointer, nameLength);
		pointer = writeBytes(destination, pointer, name);
		pointer = writeBytes(destination, pointer, type);
		pointer = writeBytes(destination, pointer, data);
		return pointer;
	}

	public int getSize()
	{
		return 1 + 2 + name.length + 1 + data.length;
	}

	public String getName()
	{
		return SerializationReader.readString(name, 0, nameLength);
	}

	public byte[] getData()
	{
		return data;
	}

	public static SField Byte(String name, byte value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.BYTE.ordinal();
		field.data = new byte[DataType.getSize(DataType.BYTE)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Boolean(String name, boolean value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.BOOLEAN.ordinal();
		field.data = new byte[DataType.getSize(DataType.BOOLEAN)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Char(String name, char value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.CHAR.ordinal();
		field.data = new byte[DataType.getSize(DataType.CHAR)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Short(String name, short value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.SHORT.ordinal();
		field.data = new byte[DataType.getSize(DataType.SHORT)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Integer(String name, int value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.INTEGER.ordinal();
		field.data = new byte[DataType.getSize(DataType.INTEGER)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Float(String name, float value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.FLOAT.ordinal();
		field.data = new byte[DataType.getSize(DataType.FLOAT)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField Long(String name, long value)
	{
		SField field = new SField();
		field.setName(name);
		field.type = (byte) DataType.LONG.ordinal();
		field.data = new byte[DataType.getSize(DataType.LONG)];
		writeBytes(field.data, 0, value);
		return field;
	}

	public static SField desirialize(byte[] data, int pointer)
	{
		byte containerType = SerializationReader.readByte(data, pointer);
		pointer++;
		if(containerType != ContainerType.Field.ordinal()) return null;

		SField field = new SField();

		field.nameLength = SerializationReader.readShort(data, pointer);
		pointer += 2;
		field.name = SerializationReader.readString(data, pointer, field.nameLength).getBytes();
		pointer += field.nameLength;

		field.type = SerializationReader.readByte(data, pointer);
		pointer++;

		field.data = new byte[DataType.getSize(field.type)];

		for(int i = 0; i < field.data.length; i++)
		{
			field.data[i] = data[pointer];
			pointer++;
		}

		return field;
	}
}
