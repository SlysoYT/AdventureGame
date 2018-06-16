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
package game.graphics.Screens.Settings;

public abstract class Setting
{
	private String settingName;
	private SettingType type;

	protected boolean boolValue;
	protected int intValue;
	protected int rangeValueMin, rangeValueMax;

	public void SettingBool(String name, boolean standardValue)
	{
		settingName = name;
		this.type = SettingType.Boolean;
		setValue(standardValue);
	}

	public void SettingRange(String name, int min, int max, int standardValue)
	{
		settingName = name;
		this.type = SettingType.Range;
		this.rangeValueMin = min;
		this.rangeValueMax = max;
		setValue(standardValue);
	}

	public int getIntValue()
	{
		return intValue;
	}

	public boolean getBoolValue()
	{
		return boolValue;
	}

	public SettingType getType()
	{
		return type;
	}

	public String getSettingName()
	{
		return settingName;
	}

	public String getString()
	{
		if(type == SettingType.Boolean) return settingName + ": " + boolValue;
		return settingName + ": " + intValue;
	}

	public void setValue(boolean value)
	{
		this.boolValue = value;
	}

	public void setValue(int value)
	{
		intValue = value;
		if(intValue < rangeValueMin) intValue = rangeValueMax;
		if(intValue > rangeValueMax) intValue = rangeValueMin;
	}

	public void increaseValue()
	{
		if(type == SettingType.Boolean) setValue(!boolValue);
		else if(type == SettingType.Range) setValue(intValue + 1);
	}

	public void decreaseValue()
	{
		if(type == SettingType.Boolean) setValue(!boolValue);
		else if(type == SettingType.Range) setValue(intValue - 1);
	}
}
