package game.network.serialization;

public enum DataType
{
	UNKNOWN, BYTE, CHAR, SHORT, INTEGER, FLOAT, BOOLEAN, LONG;

	public static int getSize(DataType type)
	{
		switch(type)
		{
		case BYTE:
			return 1;
		case BOOLEAN:
			return 1;
		case CHAR:
			return 2;
		case SHORT:
			return 2;
		case INTEGER:
			return 4;
		case FLOAT:
			return 4;
		case LONG:
			return 8;
		case UNKNOWN:
			break;
		default:
			break;
		}
		return 0;
	}

	public static int getSize(int ordinal)
	{
		return getSize(DataType.values()[ordinal]);
	}
}
