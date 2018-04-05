package game.network.serialization;

public class SArray
{
	private static final byte CONTAINER_TYPE = (byte) ContainerType.Array.ordinal();
	private short nameLength;
	private byte[] name;
	private byte type;
	private int count;
	private byte data[];

	private boolean[] booleanData;
	private char[] charData;
	private short[] shortData;
	private int[] intData;
	private float[] floatData;

	private SArray()
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
		pointer = SerializationWriter.writeBytes(destination, pointer, type);
		pointer = SerializationWriter.writeBytes(destination, pointer, count);

		if(type == DataType.BYTE.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, data);
		else if(type == DataType.BOOLEAN.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, booleanData);
		else if(type == DataType.CHAR.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, charData);
		else if(type == DataType.SHORT.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, shortData);
		else if(type == DataType.INTEGER.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, intData);
		else if(type == DataType.FLOAT.ordinal()) pointer = SerializationWriter.writeBytes(destination, pointer, floatData);
		return pointer;
	}

	public int getSize()
	{
		return 1 + 2 + name.length + 1 + 4 + getDataSize();
	}

	public int getDataSize()
	{
		if(type == DataType.BYTE.ordinal()) return data.length * DataType.getSize(DataType.BYTE);
		else if(type == DataType.BOOLEAN.ordinal()) return booleanData.length * DataType.getSize(DataType.BOOLEAN);
		else if(type == DataType.CHAR.ordinal()) return charData.length * DataType.getSize(DataType.CHAR);
		else if(type == DataType.SHORT.ordinal()) return shortData.length * DataType.getSize(DataType.SHORT);
		else if(type == DataType.INTEGER.ordinal()) return intData.length * DataType.getSize(DataType.INTEGER);
		else if(type == DataType.FLOAT.ordinal()) return floatData.length * DataType.getSize(DataType.FLOAT);
		else return 0;
	}

	public static SArray Byte(String name, byte[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.BYTE.ordinal();
		array.count = data.length;
		array.data = data;
		return array;
	}

	public static SArray Boolean(String name, boolean[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.BOOLEAN.ordinal();
		array.count = data.length;
		array.booleanData = data;
		return array;
	}

	public static SArray Char(String name, char[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.CHAR.ordinal();
		array.count = data.length;
		array.charData = data;
		return array;
	}

	public static SArray Short(String name, short[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.SHORT.ordinal();
		array.count = data.length;
		array.shortData = data;
		return array;
	}

	public static SArray Integer(String name, int[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.INTEGER.ordinal();
		array.count = data.length;
		array.intData = data;
		return array;
	}

	public static SArray Float(String name, float[] data)
	{
		SArray array = new SArray();
		array.setName(name);
		array.type = (byte) DataType.FLOAT.ordinal();
		array.count = data.length;
		array.floatData = data;
		return array;
	}

	public static SArray desirialize(byte[] data, int pointer)
	{
		byte containerType = SerializationReader.readByte(data, pointer);
		pointer++;
		if(containerType != ContainerType.Array.ordinal()) return null;

		SArray array = new SArray();

		array.nameLength = SerializationReader.readShort(data, pointer);
		pointer += 2;
		array.name = SerializationReader.readString(data, pointer, array.nameLength).getBytes();
		pointer += array.nameLength;

		array.type = SerializationReader.readByte(data, pointer);
		pointer++;

		array.count = SerializationReader.readInt(data, pointer);
		pointer += 4;

		if(array.type == DataType.BYTE.ordinal())
		{
			//TODO
		}

		return array;
	}
}
