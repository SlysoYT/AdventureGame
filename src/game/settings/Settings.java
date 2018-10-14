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
package game.settings;

import game.Game;
import game.graphics.Screens.Settings.Setting;
import game.graphics.Screens.Settings.SettingBoolean;
import game.graphics.Screens.Settings.SettingIntegerRange;
import game.graphics.Screens.Settings.SettingType;

public class Settings
{
	private static Setting[] settings = {

			//Video settings
			new SettingBoolean("Multi monitor configuration", false), new SettingBoolean("Maximum graphics quality", true),
			new SettingBoolean("Fullscreen", false), new SettingIntegerRange("Buffer strategy", 2, 4, 2),
			
			//Audio settings
			new SettingBoolean("Music", false),

			//Server settings
			//TODO

			//Advanced
			new SettingBoolean("Debug mode", true) };

	public static Setting[] getSettings()
	{
		return settings;
	}

	public static boolean getSettingBool(String name)
	{
		for(int i = 0; i < settings.length; i++)
		{
			if(settings[i].getSettingName().equals(name))
			{
				if(settings[i].getType() == SettingType.Boolean) return settings[i].getBoolValue();
			}
		}
		Game.getPrinter().printError("Setting '" + name + "' not found!");
		return false;
	}

	public static int getSettingInt(String name)
	{
		for(int i = 0; i < settings.length; i++)
		{
			if(settings[i].getSettingName().equals(name))
			{
				if(settings[i].getType() == SettingType.Range) return settings[i].getIntValue();
			}
		}
		Game.getPrinter().printError("Setting '" + name + "' not found!");
		return -1;
	}
}
