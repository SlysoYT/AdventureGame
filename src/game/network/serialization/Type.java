package game.network.Serialization;

public enum Type
{
	UNKNOWN, BYTE, CHAR, SHORT, INTEGER, FLOAT, BOOLEAN, LONG;

	public static int getSize(Type type)
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

	public static int getSize(int type)
	{
		switch(type)
		{
		case 1:
			return 1;
		case 6:
			return 1;
		case 2:
			return 2;
		case 3:
			return 2;
		case 4:
			return 4;
		case 5:
			return 4;
		case 7:
			return 8;
		case 0:
			break;
		default:
			break;
		}
		return 0;
	}
}
