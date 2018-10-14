package game.graphics.Screens;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.util.GameState;

public class ScreenInfo
{
	private static String message = "";

	public static void tick(Keyboard key)
	{
		key.tick();
		if(key.escapeToggle || key.enterToggle || key.spaceToggle)
		{
			message = "";
			Game.setGameState(GameState.TitleScreen);
		}
	}

	public static void render(Screen screen)
	{
		Sprite.writeText(message, screen, screen.width / 2, screen.height / 2, 0xFFFFFF);
	}
	
	public static void setInfoMessage(String infoMessage)
	{
		message = infoMessage;
		Game.setGameState(GameState.InfoScreen);
	}
}
