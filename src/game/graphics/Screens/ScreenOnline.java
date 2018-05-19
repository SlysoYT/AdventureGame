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
import game.util.GameState;

public class ScreenOnline
{
	private static int selection = 0;

	public static void tick(Keyboard key)
	{
		key.tick();

		if(key.escapeToggle) Game.setGameState(GameState.TitleScreen);
		else if(key.down) selection = 1;
		else if(key.up) selection = 0;

		if((key.enterToggle || key.spaceToggle) && Game.getGameStateTicksPassed() > 30)
		{
			if(selection == 0)
			{
				Game.setGameState(GameState.ServerListScreen);
			}
			else if(selection == 1)
			{
				Game.setGameState(GameState.StartServer);
			}
		}
	}

	public static void render(Screen screen)
	{
		if(selection == 0)
		{
			Sprite.writeText("Connect to server", screen, screen.width / 2, screen.height / 2, 0xFFFFFF);
		}
		else if(selection == 1)
		{
			Sprite.writeText("Start new server", screen, screen.width / 2, screen.height / 2, 0xFFFFFF);
		}
	}
}
