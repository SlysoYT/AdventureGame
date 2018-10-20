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
package game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import javax.swing.JFrame;

import game.audio.PlaySound;
import game.chat.Chat;
import game.graphics.GUI;
import game.graphics.HUD;
import game.graphics.Screen;
import game.graphics.Screens.ScreenInfo;
import game.graphics.Screens.ScreenOnline;
import game.graphics.Screens.ScreenServerList;
import game.graphics.Screens.ScreenTitle;
import game.graphics.Screens.Settings.ScreenSettings;
import game.input.Keyboard;
import game.input.Mouse;
import game.level.Level;
import game.level.LevelLoader;
import game.network.Connection;
import game.settings.Settings;
import game.util.GameState;
import game.util.Print;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L; //Default serial version

	public static int width, height;
	public static final byte SCALE = 3;
	private static final String VERSION = "Alpha 0.1";

	private boolean debugMode;
	private static boolean running = false;
	private static int currentFPS = 0, currentTPS = 0;
	private static float currentFrameTime = 0;

	private JFrame frame;
	private static Thread thread;

	private static Screen screen;
	private static Keyboard key;
	private static Level level;
	private static HUD hud;
	private static GUI activeGui;
	private static Print printer = new Print();

	private static Connection connection = null;
	private static boolean multiplayer = false;
	public static boolean isHostingGame;
	public static String hostIp;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //Creating image
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); //Accessing image

	private static final byte TPS = 60;
	private static int gameStateTicksPassed = -1;
	private static GameState gameState = GameState.TitleScreen;

	private Game(boolean debugMode)
	{
		Dimension size = new Dimension(width * SCALE, height * SCALE);
		setPreferredSize(size);

		frame = new JFrame();
		key = new Keyboard();

		screen = new Screen(width, height);

		this.debugMode = debugMode;

		addKeyListener(key);

		Mouse mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		PlaySound.initAudioSystem();
	}

	public synchronized void start()
	{
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
		printer.printInfo("Launched game");
	}

	public static synchronized void stop()
	{
		printer.printInfo("Stopping game");
		if(connection != null) connection.close();
		running = false;
	}

	public void run()
	{
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double NS_PER_TICK = 1_000_000_000.0 / TPS;
		double delta = 0;
		int fpsCount = 0;
		int tpsCount = 0;

		while(running)
		{
			long now = System.nanoTime();
			long frameTimeStart;
			long frameTimeEnd;
			delta += (now - lastTime) / NS_PER_TICK;
			lastTime = now;

			//Waits until (1 / ticks per second) of a second is passed
			while(delta >= 1)
			{
				tick();
				tpsCount++;
				delta--;
			}

			frameTimeStart = System.nanoTime();

			render();
			fpsCount++;

			frameTimeEnd = System.nanoTime();

			if(System.currentTimeMillis() - timer >= 1000)
			{
				timer += 1000;
				currentFPS = fpsCount;
				currentTPS = tpsCount;
				tpsCount = 0;
				fpsCount = 0;

				currentFrameTime = Math.round((frameTimeEnd - frameTimeStart) / 1_000_000.0F * 100F) / 100F;
			}
		}

		System.exit(0);
	}

	public void tick()
	{
		gameStateTicksPassed++;

		if(gameState == GameState.TitleScreen) ScreenTitle.tick(key);
		else if(gameState == GameState.ServerListScreen) ScreenServerList.tick(key);
		else if(gameState == GameState.OnlineScreen) ScreenOnline.tick(key);
		else if(gameState == GameState.Settings) ScreenSettings.tick(key);
		else if(gameState == GameState.InfoScreen) ScreenInfo.tick(key);
		else if(gameState == GameState.IngameOnline || gameState == GameState.IngameOffline)
		{
			if(gameState == GameState.IngameOffline && level == null) loadLevel(0);
			if(gameState == GameState.IngameOnline)
			{
				if(multiplayer)
				{
					if(!isHostingGame)
					{
						connection.tick();
						if(!connection.connectionEstablished)
						{
							abortMultiplayer(); //Connection interrupted
							return;
						}
					}
				}
			}

			Mouse.tick();
			key.tick();
			level.tick();
			hud.tick();
			screen.tick();

			if(activeGui != null) activeGui.tick();

			if(key.escape && !level.getClientPlayer().isTypingMessage())
			{
				if(gameState == GameState.IngameOnline) abortMultiplayer();
				else unloadLevel();
				if(gameState != GameState.InfoScreen) setGameState(GameState.TitleScreen);
			}
		}
		else if(gameState == GameState.StartServer)
		{
			multiplayer = true;
			isHostingGame = true;
			startMultiplayer(null);

			if(connection != null)
			{
				loadLevel(0);
				if(gameState != GameState.InfoScreen) setGameState(GameState.IngameOnline);
			}
		}
		else if(gameState == GameState.ConnectToServer)
		{
			multiplayer = true;
			isHostingGame = false;
			startMultiplayer(hostIp);
			if(connection != null && gameState != GameState.InfoScreen) setGameState(GameState.IngameOnline);
		}
	}

	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if(bs == null)
		{
			createBufferStrategy(Settings.getSettingInt("Buffer strategy")); //Creates the first time the render method runs a double buffer, triple buffer, etc.
			return;
		}
		Graphics g = bs.getDrawGraphics(); //Creates a link between the buffer and graphics

		screen.clear();

		if(gameState == GameState.TitleScreen) ScreenTitle.render(screen);
		else if(gameState == GameState.Settings) ScreenSettings.render(screen);
		else if(gameState == GameState.ServerListScreen) ScreenServerList.render(screen);
		else if(gameState == GameState.OnlineScreen) ScreenOnline.render(screen);
		else if(gameState == GameState.InfoScreen) ScreenInfo.render(screen);
		else if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline)
		{
			if(level == null || hud == null) return;
			level.render(screen);
			hud.render(screen);
		}
		else if(gameState == GameState.StartServer)
		{
			//TODO
		}

		//Filters are to place here!
		screen.applyBrightness();

		//Ingame GUI shouldn't be affected by the brightness
		if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline) screen.renderGUI(activeGui);

		for(int i = 0; i < pixels.length; i++)
		{
			pixels[i] = screen.pixels[i];
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); //Draw rendered stuff to the buffer

		//Draw with graphics g on the same buffer
		if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline)
		{
			hud.render(g, debugMode);
		}

		g.dispose(); //Removes the graphics (not the buffer)
		bs.show(); //Shows and swaps the buffers
	}

	public static void launchWindowedGame(int width, int height, boolean debugMode)
	{
		Game.width = width;
		Game.height = height;

		Game game = new Game(debugMode);
		makeWindow(game);
		game.start();
	}

	public static void launchFullscreenGame(boolean debugMode)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Game.width = (int) (screenSize.getWidth() / SCALE);
		Game.height = (int) (screenSize.getHeight() / SCALE);

		Game game = new Game(debugMode);
		game.frame.setUndecorated(true);
		makeWindow(game);
		game.start();
	}

	private static void makeWindow(Game game)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursor = toolkit.getImage(Game.class.getResource("/textures/cursors/cursor.png")).getScaledInstance(32, 32, 0);

		game.frame.setResizable(false);
		game.frame.setTitle("2D Adventure" + " (" + VERSION + ")");
		//game.frame.setCursor(toolkit.createCustomCursor(cursor, new Point(game.frame.getX(), game.frame.getY()), "cursor")); //TODO: fix
		game.frame.add(game);
		game.frame.pack(); //Apply the size to the window
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Makes that the process gets terminated when closing the window
		game.frame.setLocationRelativeTo(null); //Centers the frame
		game.requestFocus(); //So you don't have to click inside the window in order to get key input working
		game.frame.setVisible(true);

		game.frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				stop();

				try
				{
					thread.join();
				}
				catch(InterruptedException e)
				{
					printer.printError(e.getMessage());
				}
			}
		});
	}

	public static void loadLevel(long seed)
	{
		Random rand = new Random();
		level = new LevelLoader().fromSeed(seed == 0 ? rand.nextLong() : seed).load(key, "GeneratedLevel");

		hud = new HUD(width, height, level, key);
		Chat.init();
	}

	public static void loadTitleScreenLevel()
	{
		Random rand = new Random();
		level = new LevelLoader().fromSeed(rand.nextLong()).load(key, "TitleScreen");
	}

	public static void unloadLevel()
	{
		level = null;
		connection = null;
		multiplayer = false;
	}

	/**
	 * Start multiplayer. Automatically abort if any problem occurs.
	 * 
	 * @param ip
	 *            The host ip as string or null if hosting a game.
	 */
	private static void startMultiplayer(String ip)
	{
		connection = new Connection(ip, isHostingGame);
		boolean success = connection.connect();
		if(!success) abortMultiplayer();
	}

	private static void abortMultiplayer()
	{
		if(connection != null) connection.close();
		if(level != null) unloadLevel();
		else
		{
			connection = null;
			multiplayer = false;
		}
		setGameState(GameState.InfoScreen);
	}

	public static Level getLevel()
	{
		return Game.level;
	}

	public static Screen getScreen()
	{
		return Game.screen;
	}

	public static byte getTPS()
	{
		return TPS;
	}

	public static int getCurrentFPS()
	{
		return currentFPS;
	}

	public static int getCurrentTPS()
	{
		return currentTPS;
	}

	public static float getCurrentFrameTime()
	{
		return currentFrameTime;
	}

	public static String getVersion()
	{
		return VERSION;
	}

	public static void setGameState(GameState gameState)
	{
		gameStateTicksPassed = -1;
		Game.gameState = gameState;
	}

	public static void setActiveGUI(GUI gui)
	{
		Game.activeGui = gui;
	}

	public static GameState getGameState()
	{
		return Game.gameState;
	}

	public static GUI getActiveGUI()
	{
		return Game.activeGui;
	}

	public static int getGameStateTicksPassed()
	{
		return gameStateTicksPassed;
	}

	public static Print getPrinter()
	{
		return printer;
	}

	public static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static boolean isUnix()
	{
		return System.getProperty("os.name").toLowerCase().contains("nix") || System.getProperty("os.name").toLowerCase().contains("nux")
				|| System.getProperty("os.name").toLowerCase().contains("aix");
	}

	public static boolean isMac()
	{
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}
}
