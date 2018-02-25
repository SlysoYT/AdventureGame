package game.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import game.Game;
import game.chat.Chat;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.input.Keyboard;
import game.input.Mouse;
import game.level.Level;
import game.level.tile.Tile;
import game.util.Timer;

public class HUD
{
	private Player player;
	private Level level;
	private Keyboard key;

	private int width, height;
	private final int SCALE = Game.SCALE;

	private float cooldownSecondary = 0;
	private float cooldownPassive = 0;
	private float health = 0;
	private static String titleText = "";
	private static int titleTextTimer;

	private final Font FONT = new Font("Verdana", Font.BOLD, 25);
	private Font PLAYER_NAME_FONT = new Font("Verdana", 0, 22);
	private Font DEBUG_FONT = new Font("Verdana", 0, 14);

	public HUD(int width, int height, Player player, Level level, Keyboard keyboard)
	{
		this.level = level;
		this.player = player;
		this.width = width;
		this.height = height;
		this.key = keyboard;
	}

	public void tick()
	{
		Chat.tick(key);

		if(player.isDead()) return;
		cooldownSecondary = 1.0F - player.getSecondaryAbilityCooldownProgress();
		cooldownPassive = 1.0F - player.getPassiveAbilityCooldownProgress();
		health = player.getCurrentHealth() / player.getMaxHealth();
		if(titleTextTimer > 0) titleTextTimer--;
	}

	public void tickLevelEnd()
	{
		if(key.enterToggle || key.spaceToggle)
		{
			//Game.setGameState(GameState.Ingame);
			//TODO
		}
	}

	public void render(Screen screen)
	{
		//Other infos
		//TODO
		//Player infos
		if(player.isDead()) return;

		int xPos = player.getX() - Screen.getXOffset() - Tile.DEFAULT_TILE_SIZE / 2;
		int yPos = player.getY() - Screen.getYOffset();

		//Health bar
		screen.renderSprite(xPos, (int) (yPos - Tile.DEFAULT_TILE_SIZE * 1.25), Sprite.BAR_EMPTY, false);
		screen.renderSprite(xPos, (int) (yPos - Tile.DEFAULT_TILE_SIZE * 1.25), (int) (16 * health), 16, Sprite.BAR_HEALTH, false);

		//Secondary cooldown
		screen.renderSprite(xPos, (int) (yPos + Tile.DEFAULT_TILE_SIZE * 0.5), Sprite.BAR_EMPTY, false);
		screen.renderSprite(xPos, (int) (yPos + Tile.DEFAULT_TILE_SIZE * 0.5), (int) (16 * cooldownSecondary), 16, Sprite.BAR_SECONDARY, false);

		//Passive cooldown
		screen.renderSprite(xPos, (int) (yPos + Tile.DEFAULT_TILE_SIZE * 0.25), Sprite.BAR_EMPTY, false);
		screen.renderSprite(xPos, (int) (yPos + Tile.DEFAULT_TILE_SIZE * 0.25), (int) (16 * cooldownPassive), 16, Sprite.BAR_PASSIVE, false);

	}

	public void render(Graphics g, boolean debugMode)
	{
		renderTimer(g);
		renderPlayerNames(g);
		Chat.render(g);
		if(!player.isDead()) renderAlert(g);
		else renderDeathScreen(g);
		if(debugMode)
		{
			renderDebugScreen(g);
			level.renderHitboxes(g);
		}
	}

	private void renderTimer(Graphics g)
	{
		g.setFont(FONT);
		g.setColor(Color.ORANGE);
		float time = Timer.getPassedTime(false);
		String timeStr = String.format("%.1f", time);
		g.drawString(timeStr, width * SCALE - g.getFontMetrics().stringWidth(timeStr), FONT.getSize());
	}

	private void renderPlayerNames(Graphics g)
	{
		g.setFont(PLAYER_NAME_FONT);
		g.setColor(Color.BLUE);

		List<Player> players = level.getPlayers();

		for(Player player : players)
		{
			if(player instanceof OnlinePlayer)
			{
				String playerName = ((OnlinePlayer) player).getPlayerName();
				int xPos = player.getX() * Game.SCALE - level.getClientPlayer().getX() * Game.SCALE + Game.width * Game.SCALE / 2
						- g.getFontMetrics().stringWidth(playerName) / 2;
				int yPos = player.getY() * Game.SCALE - level.getClientPlayer().getY() * Game.SCALE + Game.height * Game.SCALE / 2
						- player.getHitbox().getHeight() / 2 - PLAYER_NAME_FONT.getSize();
				g.drawString(playerName, xPos, yPos);
			}
		}
	}

	private void renderAlert(Graphics g)
	{
		if(titleTextTimer > 0)
		{
			g.setFont(FONT);
			g.setColor(Color.BLUE);
			g.drawString(titleText, ((width / 2) * SCALE) - g.getFontMetrics().stringWidth(titleText) / 2, height / 4 * SCALE);
		}
	}

	private void renderDebugScreen(Graphics g)
	{
		g.setFont(DEBUG_FONT);
		g.setColor(Color.BLUE);
		g.drawString("Debug mode (Version: " + Game.getVersion() + ")", 5, (DEBUG_FONT.getSize() + 3) * 1);
		g.setColor(Color.WHITE);
		g.drawString("X: " + player.getX() + " | Y: " + player.getY(), 5, (DEBUG_FONT.getSize() + 3) * 2);
		g.drawString("Velocity: " + player.getXVelocity() + ", " + player.getYVelocity(), 5, (DEBUG_FONT.getSize() + 3) * 3);
		g.drawString("Dir: " + player.getDir(), 5, (DEBUG_FONT.getSize() + 3) * 4);
		g.drawString("Mouse: " + Mouse.getX() + ", " + Mouse.getY() + ", " + Mouse.getButton(), 5, (DEBUG_FONT.getSize() + 3) * 5);
		g.drawString("Level: " + Game.getLevel().getLevelName(), 5, (DEBUG_FONT.getSize() + 3) * 6);

		String fpsTpsInfo = "FPS: " + Game.getCurrentFPS() + " | TPS: " + Game.getCurrentTPS();
		g.drawString(fpsTpsInfo, Game.width * SCALE - g.getFontMetrics().stringWidth(fpsTpsInfo) - 3, Game.height * SCALE - 3);
	}

	private void renderDeathScreen(Graphics g)
	{
		int cooldown = level.getClientPlayer().getCurrentRespawnCooldown();
		if(cooldown > level.getClientPlayer().getRespawnCooldown() * 0.5)
		{
			titleText = "You died!";
			g.setFont(FONT);
			g.setColor(Color.BLUE);
			g.drawString(titleText, ((width / 2) * SCALE) - g.getFontMetrics().stringWidth(titleText) / 2, height / 4 * SCALE);
		}
	}

	public void renderLevelEnd(Graphics g)
	{
		g.setFont(FONT);
		g.setColor(Color.ORANGE);
		String time = Timer.getPassedTime(true) + "";
		g.drawString(time, width * SCALE / 2 - g.getFontMetrics().stringWidth(time) / 2, height * SCALE / 2 - FONT.getSize() / 2);
	}

	public static void setTitleText(String text, int displayTimeInTicks)
	{
		titleText = text;
		titleTextTimer = displayTimeInTicks;
	}
}
