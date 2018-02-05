package game.graphics.Screens;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.util.GameState;
import game.util.Print;

public class TitleScreen
{
	private static boolean scrollTitleScreenRight = true;
	private static byte currentTitleScreenSelection = 0;

	private static float xOffsetTitleScreen;

	private static BufferedImage fullTitleScreen = null;
	private static BufferedImage scaledTitleScreen = null;
	private static double titleScreenScaling;

	public static void tickTitleScreen(float xOffset, Keyboard input)
	{
		input.tick();

		if((xOffsetTitleScreen - Game.width) <= -(scaledTitleScreen.getWidth() * titleScreenScaling)) scrollTitleScreenRight = false;
		if(xOffsetTitleScreen >= 0) scrollTitleScreenRight = true;

		if(scrollTitleScreenRight) xOffsetTitleScreen -= xOffset;
		else xOffsetTitleScreen += xOffset;

		if(input.enterToggle || input.spaceToggle)
		{
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

	public static void renderTitleScreen(Screen screen)
	{
		for(int y = 0; y < scaledTitleScreen.getHeight(); y++)
		{
			int yAbsolute = y + 0;
			if(yAbsolute > Game.height || yAbsolute < 0) continue; //Performance, don't render image if out of screen

			for(int x = 0; x < scaledTitleScreen.getWidth(); x++)
			{
				int xAbsolute = (int) (x + xOffsetTitleScreen);
				if(xAbsolute > Game.width || xAbsolute < 0) continue; //Performance, don't render image if out of screen
				if(xAbsolute >= screen.width || yAbsolute < 0 || yAbsolute >= screen.height) break;
				if(xAbsolute < 0) continue; //Prevents array index out of bounds exception

				screen.pixels[xAbsolute + yAbsolute * screen.width] = scaledTitleScreen.getRGB(x, y);
			}
		}

		String[] selections = { "Singleplayer", "Multiplayer", "Settings", "Quit" };

		for(int i = 0; i < 4; i++)
		{
			if(currentTitleScreenSelection == i) Sprite.writeText(selections[i], screen, screen.width / 2, getYPos(i), 0xFFFFFF);
			else Sprite.writeText(selections[i], screen, screen.width / 2, getYPos(i), 0x000000);
		}
	}

	public static void initTitleScreen()
	{
		try
		{
			fullTitleScreen = ImageIO.read(Screen.class.getResource("/textures/titleScreen/c.jpg"));
		}
		catch(IOException e)
		{
			Print.printError(e.getMessage());
		}

		int w = fullTitleScreen.getWidth();
		int h = fullTitleScreen.getHeight();
		scaledTitleScreen = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		titleScreenScaling = 1 / ((double) (h) / (double) (Game.height));
		at.scale(titleScreenScaling, titleScreenScaling);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		scaledTitleScreen = scaleOp.filter(fullTitleScreen, scaledTitleScreen);
	}

	private static int getYPos(int number)
	{
		return Game.height / 2 + (number * 50 - 70);
	}
}
