package game;

import game.settings.Settings;

public class Launcher
{
	public static void main(String[] args)
	{
		if(Settings.fullscreen)
		{
			Game.launchFullscreenGame(Settings.debugMode);
		}
		else
		{
			Game.launchWindowedGame(300, 200, Settings.debugMode);
		}
	}
}