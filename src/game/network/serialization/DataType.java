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
