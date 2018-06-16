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

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.settings.Settings;
import game.util.GameState;

public class ScreenSettings
{
	private static final Setting[] settings = Settings.getSettings();
	private static int selection = 0;

	public static void tick(Keyboard key)
	{
		key.tick();

		if(key.downToggle) selection++;
		if(key.upToggle) selection--;
		if(selection > settings.length) selection = 0;
		if(selection < 0) selection = settings.length - 1;

		if(key.escapeToggle) Game.setGameState(GameState.TitleScreen);

		if(key.rightToggle) settings[selection].increaseValue();
		if(key.leftToggle) settings[selection].decreaseValue();
	}

	public static void render(Screen screen)
	{
		int x = Game.width / 2;
		int y = Game.height / 10;

		for(int i = 0; i < settings.length; i++)
		{
			if(selection == i) Sprite.writeText(settings[i].getString(),screen, x, y, 0x45C95E);
			else Sprite.writeText(settings[i].getString(), screen, x, y, 0xFFFFFF);

			y += 40;
		}
	}
}
