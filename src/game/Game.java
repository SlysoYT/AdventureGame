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

import javax.swing.JFrame;

import game.audio.PlaySound;
import game.chat.Chat;
import game.entity.mob.player.Player;
import game.graphics.HUD;
import game.graphics.Screen;
import game.graphics.Screens.OnlineScreen;
import game.graphics.Screens.ServerListScreen;
import game.graphics.Screens.TitleScreen;
import game.input.Keyboard;
import game.input.Mouse;
import game.level.GameLevel;
import game.level.Level;
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

	private JFrame frame;
	private static HUD hud;
	private static Keyboard key;
	private static Player clientPlayer;
	private static Level level;
	private static Chat chat;
	private Screen screen;
	private Thread thread;

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
		System.out.println("Launched game");
	}

	public synchronized void stop()
	{
		if(connection != null) connection.close();
		running = false;
		System.out.println("Stopped game");
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
				System.out.println("FPS: " + currentFPS + " | " + "TPS: " + currentTPS);
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
			TitleScreen.tickTitleScreen(key);
		}
		else if(gameState == GameState.IngameOnline || gameState == GameState.IngameOffline)
		{
			if(level == null) initLevel();

			if(gameState == GameState.IngameOnline)
			{
				if(!multiplayer)
				{
					connection.tick(null);
					multiplayer = connection.connectionEstablished;
				}
				else
				{
					if(!connection.connectionEstablished) multiplayer = false; //Connection interrupted
					else connection.tick(level);
				}
			}

			key.tick();
			level.tick();
			hud.tick(width, height, clientPlayer, level, key);

			if(key.escapeToggle)
			{
				unloadLevel();
				setGameState(GameState.TitleScreen);
			}
		}
		else if(gameState == GameState.LevelFinished)
		{
			hud.tick(width, height, clientPlayer, level, key);
			key.tick();
			hud.tickLevelEnd(key.enterToggle || key.spaceToggle);
		}
		else if(gameState == GameState.ServerListScreen)
		{
			ServerListScreen.tick(key);
		}
		else if(gameState == GameState.OnlineScreen)
		{
			OnlineScreen.tick(key);
		}
		else if(gameState == GameState.StartServer)
		{
			isHostingGame = true;
			startMultiplayer(null);
			setGameState(GameState.IngameOnline);
		}
		else if(gameState == GameState.ConnectToServer)
		{
			isHostingGame = false;
			startMultiplayer(hostIp);
			setGameState(GameState.IngameOnline);
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
			TitleScreen.renderTitleScreen(screen);
		}
		else if(gameState == GameState.IngameOffline || gameState == GameState.IngameOnline)
		{
			if(level == null) return;
			int xScroll = clientPlayer.getX() - screen.width / 2;
			int yScroll = clientPlayer.getY() - screen.height / 2;

			level.render(xScroll, yScroll, screen);
			hud.render(screen);
		}
		else if(gameState == GameState.Options)
		{

		}
		else if(gameState == GameState.ServerListScreen)
		{
			ServerListScreen.render(screen);
		}
		else if(gameState == GameState.OnlineScreen)
		{
			OnlineScreen.render(screen);
		}
		else if(gameState == GameState.StartServer)
		{
			//TODO
		}

		//Filters are to place here!
		//screen.applyAlpha(0.5F);

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
		else if(gameState == GameState.LevelFinished)
		{
			hud.renderLevelEnd(g);
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
		Image cursor = toolkit.getImage(Game.class.getResource("/textures/cursors/cursor.png"));

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

	public static void loadLevel(GameLevel newLevel)
	{
		if(Game.level != null) unloadLevel();
		Game.level = newLevel;
		Level.loadLevel(newLevel);
		if(newLevel.getLevelName().equals("TitleScreen")) return;
		clientPlayer = new Player(Game.level.getSpawnLocation().getX(), Game.level.getSpawnLocation().getY(), key);
		level.add(clientPlayer);
	}

	public static void unloadLevel()
	{
		Game.level.unloadLevel();
		Game.level = null;
		multiplayer = false;
	}

	private static void initLevel()
	{
		loadLevel(new GameLevel("/levels/TitleScreen.png", "Level-1", 2, 2));
		chat = new Chat(level);
		hud = new HUD(width, height, clientPlayer, level);
	}

	private static void startMultiplayer(String ip)
	{
		connection = new Connection(ip, isHostingGame);
		multiplayer = connection.connectionEstablished;
	}

	public static void terminate()
	{
		running = false;
	}

	public static Level getLevel()
	{
		return Game.level;
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

	public static GameState getGameState()
	{
		return Game.gameState;
	}

	public static int getGameStateTicksPassed()
	{
		return gameStateTicksPassed;
	}
}
