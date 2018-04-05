package game.network.serialization;

import static game.network.serialization.SerializationWriter.writeBytes;

import java.util.ArrayList;
import java.util.List;

public class SObject
{
	private static final byte CONTAINER_TYPE = (byte) ContainerType.Object.ordinal();
	private short nameLength;
	private byte[] name;

	private List<SField> fields = new ArrayList<SField>();
	private List<SArray> arrays = new ArrayList<SArray>();
	private List<SString> strings = new ArrayList<SString>();

	private int size = 1 + 2 + 4 + 4 + 4 + 4;

	private SObject()
	{
	}

	public SObject(String name)
	{
		setName(name);
	}

	public void setName(String name)
	{
		if(this.name != null) size -= this.name.length;
		nameLength = (short) name.length();
		this.name = name.getBytes();
		size += name.length();
	}

	public int getBytes(byte[] destination, int pointer)
	{
		pointer = writeBytes(destination, pointer, CONTAINER_TYPE);
		pointer = writeBytes(destination, pointer, nameLength);
		pointer = writeBytes(destination, pointer, name);
		pointer = writeBytes(destination, pointer, size);

		pointer = writeBytes(destination, pointer, fields.size());

		for(SField field : fields)
		{
			pointer = field.getBytes(destination, pointer);
		}

		pointer = writeBytes(destination, pointer, arrays.size());

		for(SArray array : arrays)
		{
			pointer = array.getBytes(destination, pointer);
		}

		pointer = writeBytes(destination, pointer, strings.size());

		for(SString string : strings)
		{
			pointer = string.getBytes(destination, pointer);
		}
		return pointer;
	}

	public void addField(SField field)
	{
		fields.add(field);
		size += field.getSize();
	}

	public void addArray(SArray array)
	{
		arrays.add(array);
		size += array.getSize();
	}

	public void addString(SString string)
	{
		strings.add(string);
		size += string.getSize();
	}

	public List<SField> getFields()
	{
		return fields;
	}

	public List<SArray> getArrays()
	{
		return arrays;
	}

	public List<SString> getStrings()
	{
		return strings;
	}

	public SField findField(String name)
	{
		for(SField field : fields)
		{
			if(field.getName().equals(name)) return field;
		}

		return null;
	}

	public List<SField> findFields(String name)
	{
		List<SField> list = new ArrayList<SField>();

		for(SField field : fields)
		{
			if(field.getName().equals(name)) list.add(field);
		}

		return list;
	}

	public SString findString(String name)
	{
		for(SString string : strings)
		{
			if(string.getName().equals(name)) return string;
		}

		return null;
	}

	public List<SString> findStrings(String name)
	{
		List<SString> list = new ArrayList<SString>();

		for(SString string : strings)
		{
			if(string.getName().equals(name)) list.add(string);
		}

		return list;
	}

	public int getSize()
	{
		return size;
	}

	public String getName()
	{
		return SerializationReader.readString(name, 0, nameLength);
	}

	public static SObject deserialize(byte[] data, int pointer)
	{
		byte containerType = SerializationReader.readByte(data, pointer);
		pointer++;
		if(containerType != ContainerType.Object.ordinal()) return null;

		SObject object = new SObject();

		object.nameLength = SerializationReader.readShort(data, pointer);
		pointer += 2;
		object.name = SerializationReader.readString(data, pointer, object.nameLength).getBytes();
		pointer += object.nameLength;
		object.size = SerializationReader.readInt(data, pointer);
		pointer += 4;

		int fieldCount = SerializationReader.readInt(data, pointer);
		pointer += 4;
		for(int i = 0; i < fieldCount; i++)
		{
			SField field = SField.desirialize(data, pointer);
			if(field == null) return null;
			object.addField(field);
			pointer += field.getSize();
		}

		int arrayCount = SerializationReader.readInt(data, pointer);
		pointer += 4;
		for(int i = 0; i < arrayCount; i++)
		{
			//TODO
		}

		int stringCount = SerializationReader.readInt(data, pointer);
		pointer += 4;
		for(int i = 0; i < stringCount; i++)
		{
			SString string = SString.desirialize(data, pointer);
			if(string == null) return null;
			object.addString(string);
			pointer += string.getSize(); //TODO: That's probably the problem; -> if more than one string fails to deserialize cuz this is not working properly
		}

		return object;
	}
}
