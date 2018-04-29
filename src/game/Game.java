package game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
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
import game.entity.mob.player.Player;
import game.graphics.GUI;
import game.graphics.HUD;
import game.graphics.Screen;
import game.graphics.Screens.ScreenOnline;
import game.graphics.Screens.ScreenServerList;
import game.graphics.Screens.ScreenTitle;
import game.input.Keyboard;
import game.input.Mouse;
import game.level.GameLevel;
import game.level.Level;
import game.network.Connection;
import game.settings.Settings;
import game.util.GameState;
import game.util.Print;
import game.util.TileCoordinate;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L; //Default serial version

	public static int width, height;
	public static final byte SCALE = 3;
	private static final String VERSION = "Alpha 0.1";

	private boolean debugMode;
	private static boolean running = false;
	private static int currentFPS = 0, currentTPS = 0;

	private JFrame frame;
	private Thread thread;

	private static Screen screen;
	private static Keyboard key;
	private static Level level;
	private static Player clientPlayer;
	private static HUD hud;
	private static GUI activeGui;

	private static Connection connection = null;
	private static boolean multiplayer = false;
	public static boolean isHostingGame;
	public static String hostIp;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //Creating image
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); //Accessing image

	private static final byte TPS = 60;
	private static int gameStateTicksPassed = -1;
	private static GameState gameState = GameState.TitleScreen;

	public Game(boolean debugMode)
	{
		Dimension size = new Dimension(width * SCALE, height * SCALE);
		setPreferredSize(size);

		frame = new JFrame();
		key = new Keyboard();

		screen = new Screen(width, height, key);

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
		Print.printInfo("Launched game");
	}

	public synchronized void stop()
	{
		if(connection != null) connection.close();
		running = false;
		Print.printInfo("Stopped game");
		System.exit(0);

		try
		{
			thread.join();
		}
		catch(InterruptedException e)
		{
			Print.printError(e.getMessage());
		}
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
			delta += (now - lastTime) / NS_PER_TICK;
			lastTime = now;

			//Waits until (1 / ticks per second) of a second is passed
			while(delta >= 1)
			{
				tick();
				tpsCount++;
				delta--;
			}

			render();
			fpsCount++;

			if(System.currentTimeMillis() - timer >= 1000)
			{
				timer += 1000;
				currentFPS = fpsCount;
				currentTPS = tpsCount;
				Print.printInfo(currentFPS + " FPS" + " | " + currentTPS + " TPS");
				tpsCount = 0;
				fpsCount = 0;
			}
		}
		stop();
	}

	public void tick()
	{
		gameStateTicksPassed++;

		if(gameState == GameState.TitleScreen)
		{
			ScreenTitle.tick(key);
		}
		else if(gameState == GameState.IngameOnline || gameState == GameState.IngameOffline)
		{
			if(level == null && !(gameState == GameState.IngameOnline && !isHostingGame)) loadLevel(null, -1);

			if(gameState == GameState.IngameOnline)
			{
				if(multiplayer)
				{
					if(isHostingGame)
					{
						if(connection == null) startMultiplayer(null);
					}
					else
					{
						connection.tick();
						if(!connection.connectionEstablished) multiplayer = false; //Connection interrupted
					}
				}
			}

			key.tick();
			Mouse.tick();
			level.tick();
			hud.tick();

			if(activeGui != null) activeGui.tick();

			if(key.escape && !clientPlayer.isTypingMessage())
			{
				if(gameState == GameState.IngameOnline)
				{
					connection.close();
					connection = null;
				}

				unloadLevel();
				setGameState(GameState.TitleScreen);
			}
		}
		else if(gameState == GameState.ServerListScreen)
		{
			ScreenServerList.tick(key);
		}
		else if(gameState == GameState.OnlineScreen)
		{
			ScreenOnline.tick(key);
		}
		else if(gameState == GameState.StartServer)
		{
			setGameState(GameState.IngameOnline);
			isHostingGame = true;
			multiplayer = true;
		}
		else if(gameState == GameState.ConnectToServer)
		{
			isHostingGame = false;
			startMultiplayer(hostIp);
			if(multiplayer) setGameState(GameState.IngameOnline);
			else setGameState(GameState.TitleScreen);
		}
		else
		{
			key.tick();
			if(key.escapeToggle) setGameState(GameState.TitleScreen);
		}
	}

	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if(bs == null)
		{
			if(Settings.bufferStrategy == 2) createBufferStrategy(2); //Creates the first time the render method runs either a double buffer
			else createBufferStrategy(3); //Or a triple buffer
			return;
		}
		Graphics g = bs.getDrawGraphics(); //Creates a link between the buffer and graphics

		screen.clear();

		if(gameState == GameState.TitleScreen)
		{
			ScreenTitle.render(screen);
		}
		else if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline)
		{
			if(level == null) return;

			level.render(screen);
			hud.render(screen);
			screen.renderGUI(activeGui);
		}
		else if(gameState == GameState.Options)
		{

		}
		else if(gameState == GameState.ServerListScreen)
		{
			ScreenServerList.render(screen);
		}
		else if(gameState == GameState.OnlineScreen)
		{
			ScreenOnline.render(screen);
		}
		else if(gameState == GameState.StartServer)
		{
			//TODO
		}

		//Filters are to place here!
		screen.applyBrightness();

		for(int i = 0; i < pixels.length; i++)
		{
			pixels[i] = screen.pixels[i];
		}

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); //Draws everything rendered to the screen

		//Draw with g only here
		if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline)
		{
			hud.render(g, debugMode);
		}

		g.dispose(); //Removes the graphics (not the buffer)
		bs.show(); //Shows and swaps the buffers
	}

	//Launch game
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
		if(!Settings.multiMonitorConfiguration)
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

			Game.width = (int) (screenSize.getWidth() / SCALE);
			Game.height = (int) (screenSize.getHeight() / SCALE);
		}
		else
		{
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

			Game.width = gd.getDisplayMode().getWidth() / SCALE;
			Game.height = gd.getDisplayMode().getWidth() / SCALE;
		}

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
		game.frame.setCursor(toolkit.createCustomCursor(cursor, new Point(game.frame.getX(), game.frame.getY()), "cursor"));
		game.frame.add(game);
		game.frame.pack(); //Apply the size to the window
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Makes that the process gets terminated when closing the window
		game.frame.setLocationRelativeTo(null); //Centers the frame
		game.requestFocus(); //So you don't have to click inside the window in order to get key input working
		game.frame.setVisible(true);
		game.frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				running = false;
			}

		});
	}

	/**
	 * Load a level. Example: <br>
	 * <code>
	 * loadLevel(new GameLevel("/levels/TitleScreen.png", "Level-1", 2, 2), -1); <br>
	 * loadLevel(null, -1); <br>
	 * loadLevel(null, -31415962L); <br>
	 * </code>
	 * 
	 * @param gameLevel
	 *            Must be null to generate level
	 * @param seed
	 *            Must be -1 to generate random level
	 */
	public static void loadLevel(GameLevel gameLevel, long seed)
	{
		if(gameLevel != null) initLevel(gameLevel);
		else
		{
			if(seed == -1L)
			{
				Random rand = new Random();
				initLevel(new GameLevel(rand.nextLong(), "Generated-Level", new TileCoordinate(256, 256)));
			}
			else
			{
				initLevel(new GameLevel(seed, "Generated-Level", new TileCoordinate(256, 256)));
			}
		}

		hud = new HUD(width, height, clientPlayer, level, key);
		Chat.init();
	}

	private static void initLevel(GameLevel newLevel)
	{
		if(Game.level != null) unloadLevel();
		Game.level = newLevel;
		if(newLevel.isCustomLevel()) level.loadLevel(newLevel);
		else level.generateLevel(newLevel);
		if(newLevel.getLevelName().equals("TitleScreen")) return;
		clientPlayer = new Player(Game.level.getSpawnLocation().getX(), Game.level.getSpawnLocation().getY(), key);
		level.add(clientPlayer);
	}

	public static void unloadLevel()
	{
		level.unloadLevel();
		level = null;
		multiplayer = false;
	}

	private static void startMultiplayer(String ip)
	{
		connection = new Connection(ip, isHostingGame);
		connection.connect();
		multiplayer = isHostingGame || connection.connectionEstablished;
	}

	public static void terminate()
	{
		running = false;
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
