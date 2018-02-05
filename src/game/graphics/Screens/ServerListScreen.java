package game.graphics.Screens;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.input.TextInput;
import game.util.GameState;

public class ServerListScreen
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
