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
package game.graphics.Screens;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.input.TextInput;
import game.util.GameState;

public class ScreenServerList
{
	private static String inputField = "";

	public static void tick(Keyboard key)
	{
		if(Game.getGameStateTicksPassed() == 0)
		{
			TextInput.clearTextInput();
			inputField = "";
		}

		key.tick();
		inputField = TextInput.getTextInput();

		if((key.enterToggle || key.spaceToggle) && Game.getGameStateTicksPassed() > 30)
		{
			Game.hostIp = inputField;
			Game.setGameState(GameState.ConnectToServer);
		}

		if(key.escapeToggle)
		{
			Game.setGameState(GameState.TitleScreen);
			inputField = "";
		}
	}

	public static void render(Screen screen)
	{
		Sprite.writeText(inputField, screen, screen.width / 2, screen.height / 2, 0xFFFFFF);
	}
}
