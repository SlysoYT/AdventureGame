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
import game.level.tile.Tile;
import game.settings.Settings;
import game.util.GameState;

public class ScreenTitle
{
	private static byte currentTitleScreenSelection = 0;
	private static float xOffset = 0;
	private static float yOffset = 0;
	private static float xVelocity = 0.5F;
	private static float yVelocity = 0.5F;

	public static void tick(Keyboard input)
	{
		if(Game.getLevel() == null) init();

		input.tick();
		Game.getLevel().tick();

		xOffset += xVelocity;
		yOffset += yVelocity;

		if(yOffset > Game.getLevel().getLevelHeight() * Tile.DEFAULT_TILE_SIZE - Game.height) yVelocity = -yVelocity;
		else if(yOffset < 0) yVelocity = -yVelocity;
		if(xOffset > Game.getLevel().getLevelWidth() * Tile.DEFAULT_TILE_SIZE - Game.width) xVelocity = -xVelocity;
		else if(xOffset < 0) xVelocity = -xVelocity;

		if(input.enterToggle || input.spaceToggle)
		{
			unloadTitleScreen();
			if(currentTitleScreenSelection == 0) Game.setGameState(GameState.IngameOffline);
			else if(currentTitleScreenSelection == 1) Game.setGameState(GameState.OnlineScreen);
			else if(currentTitleScreenSelection == 2) Game.setGameState(GameState.Options);
			else if(currentTitleScreenSelection == 3) Game.terminate();
		}

		if(input.downToggle) currentTitleScreenSelection++;
		else if(input.upToggle) currentTitleScreenSelection--;

		if(currentTitleScreenSelection > 3) currentTitleScreenSelection = 0;
		if(currentTitleScreenSelection < 0) currentTitleScreenSelection = 3;
	}

	public static void render(Screen screen)
	{
		if(Game.getLevel() != null) Game.getLevel().render((int) xOffset, (int) yOffset, screen);

		if(Settings.maximumGraphicsQuality)
		{
			screen.blur();
			screen.blur();
		}
		else screen.blur();

		String[] selections = { "Singleplayer", "Multiplayer", "Settings", "Quit" };

		for(int i = 0; i < selections.length; i++)
		{
			if(currentTitleScreenSelection == i) Sprite.writeText(selections[i], screen, screen.width / 2, Game.height / 2 + (i * 50 - 70), 0x45C95E);
			else Sprite.writeText(selections[i], screen, screen.width / 2, Game.height / 2 + (i * 50 - 70), 0x000000);
		}
	}

	private static void init()
	{
		Game.loadTitleScreenLevel();
	}

	private static void unloadTitleScreen()
	{
		Game.unloadLevel();
	}
}
