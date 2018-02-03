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

public class TitleScreen
{
	private static boolean scrollTitleScreenRight = true;
	private static byte currentTitleScreenSelection = 0;
	private static int keyDownTicks = 0;
	private static int keyUpTicks = 0;

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

		if(input.enter)
		{
			if(currentTitleScreenSelection == 0) Game.setGameState(GameState.IngameOffline);
			else if(currentTitleScreenSelection == 1) Game.setGameState(GameState.OnlineScreen);
			else if(currentTitleScreenSelection == 2) Game.setGameState(GameState.Options);
			else if(currentTitleScreenSelection == 3) Game.terminate();
		}

		if(input.down && keyDownTicks == 0)
		{
			keyDownTicks = 15;
			currentTitleScreenSelection++;
		}
		else if(input.up && keyUpTicks == 0)
		{
			keyUpTicks = 12;
			currentTitleScreenSelection--;
		}
		else if(keyDownTicks > 0) keyDownTicks--;
		else if(keyUpTicks > 0) keyUpTicks--;

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

		//screen.renderSprite(Game.width / 2 - Sprite.playButton.getWidth() / 2, getYPos(0), Sprite.playButton, false);
		//screen.renderSprite(Game.width / 2 - Sprite.playButton.getWidth() / 2, getYPos(1), Sprite.playButton, false);
		//screen.renderSprite(Game.width / 2 - Sprite.optionsButton.getWidth() / 2, getYPos(2), Sprite.optionsButton, false);
		//screen.renderSprite(Game.width / 2 - Sprite.quitButton.getWidth() / 2, getYPos(3), Sprite.quitButton, false);
		int white = 0xFFFFFF;
		int black = 0x000000;

		String[] selections = { "Singleplayer", "Multiplayer", "Settings", "Quit" };

		for(int i = 0; i < 4; i++)
		{
			if(currentTitleScreenSelection == i) Sprite.writeText(selections[i], screen, screen.width / 2, getYPos(i), white);
			else Sprite.writeText(selections[i], screen, screen.width / 2, getYPos(i), black);
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
			e.printStackTrace();
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
