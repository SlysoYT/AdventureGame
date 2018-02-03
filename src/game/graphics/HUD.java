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
	private Sprite bar;

	private int width, height;
	private final int SCALE = Game.SCALE;

	private float cooldown0 = 0;
	private float cooldown1 = 0;
	private float cooldown2 = 0;
	private float health = 0;
	private static String titleText = "";
	private static int titleTextTimer;

	private Font font = new Font("Verdana", Font.BOLD, 25);
	private Font playerNameFont = new Font("Verdana", 0, 22);
	private Font debugFont = new Font("Verdana", 0, 14);

	public HUD(int width, int height, Player player, Level level)
	{
		this.level = level;
		this.player = player;
		this.width = width;
		this.height = height;
	}

	public void tick(int width, int height, Player player, Level level, Keyboard key)
	{
		this.level = level;
		this.player = player;
		this.width = width;
		this.height = height;

		Chat.tick(key);

		if(player.isDead()) return;
		cooldown0 = 1.0F - player.getPrimaryAbilityCooldownProgress();
		cooldown1 = 1.0F - player.getSecondaryAbilityCooldownProgress();
		cooldown2 = 1.0F - player.getPassiveAbilityCooldownProgress();
		health = player.getCurrentHealth() / player.getMaxHealth();
		if(titleTextTimer > 0) titleTextTimer--;
	}

	public void tickLevelEnd(boolean enterPressed)
	{
		if(enterPressed)
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
		//Health bar
		for(int i = 0; i < 9; i++)
		{
			if(health <= ((i + 1) * (1.0F / 9)))
			{
				bar = Sprite.healthBar[i];
				break;
			}
		}
		screen.renderSprite(width / 2 - Tile.DEFAULT_TILE_SIZE / 2, height / 2 - (int) (1.25 * Tile.DEFAULT_TILE_SIZE), bar, false);

		//Primary cooldown
		for(int i = 0; i < 9; i++)
		{
			if(cooldown0 <= ((i + 1) * (1.0F / 9)))
			{
				bar = Sprite.cooldownBar0[i];
				break;
			}
		}
		screen.renderSprite(width / 2 - Tile.DEFAULT_TILE_SIZE / 2, height / 2 + 3, bar, false);

		//Secondary cooldown
		for(int i = 0; i < 9; i++)
		{
			if(cooldown1 <= ((i + 1) * (1.0F / 9)))
			{
				bar = Sprite.cooldownBar1[i];
				break;
			}
		}
		screen.renderSprite(width / 2 - Tile.DEFAULT_TILE_SIZE / 2, height / 2 + 6, bar, false);

		//Passive cooldown
		for(int i = 0; i < 9; i++)
		{
			if(cooldown2 <= ((i + 1) * (1.0F / 9)))
			{
				bar = Sprite.cooldownBar0[i];
				break;
			}
		}
		screen.renderSprite(width / 2 - Tile.DEFAULT_TILE_SIZE / 2, height / 2 + 9, bar, false);

	}

	public void render(Graphics g, boolean debugMode)
	{
		Chat.render(g);
		renderTimer(g);
		renderPlayerNames(g);
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
		g.setFont(font);
		g.setColor(Color.ORANGE);
		float time = Timer.getPassedTime(false);
		String timeStr = String.format("%.1f", time);
		g.drawString(timeStr, width * SCALE - g.getFontMetrics().stringWidth(timeStr), font.getSize());
	}

	private void renderPlayerNames(Graphics g)
	{
		g.setFont(playerNameFont);
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
						- player.getHitbox().getHeight() / 2 - playerNameFont.getSize();
				g.drawString(playerName, xPos, yPos);
			}
		}
	}

	private void renderAlert(Graphics g)
	{
		if(titleTextTimer > 0)
		{
			g.setFont(font);
			g.setColor(Color.BLUE);
			g.drawString(titleText, ((width / 2) * SCALE) - g.getFontMetrics().stringWidth(titleText) / 2, height / 4 * SCALE);
		}
	}

	private void renderDebugScreen(Graphics g)
	{
		g.setFont(debugFont);
		g.setColor(Color.BLUE);
		g.drawString("Debug mode (Version: " + Game.getVersion() + ")", 5, (debugFont.getSize() + 3) * 1);
		g.setColor(Color.WHITE);
		g.drawString("X: " + player.getX() + " | Y: " + player.getY(), 5, (debugFont.getSize() + 3) * 2);
		g.drawString("Velocity: " + player.getXVelocity() + ", " + player.getYVelocity(), 5, (debugFont.getSize() + 3) * 3);
		g.drawString("Dir: " + player.getDir(), 5, (debugFont.getSize() + 3) * 4);
		g.drawString("Mouse: " + Mouse.getX() + ", " + Mouse.getY() + ", " + Mouse.getButton(), 5, (debugFont.getSize() + 3) * 5);
		g.drawString("Level: " + Game.getLevel().getLevelName(), 5, (debugFont.getSize() + 3) * 6);

		String fpsTpsInfo = "FPS: " + Game.getCurrentFPS() + " | TPS: " + Game.getCurrentTPS();
		g.drawString(fpsTpsInfo, Game.width * SCALE - g.getFontMetrics().stringWidth(fpsTpsInfo) - 3, Game.height * SCALE - 3);
	}

	private void renderDeathScreen(Graphics g)
	{
		int cooldown = level.getClientPlayer().getCurrentRespawnCooldown();
		if(cooldown > level.getClientPlayer().getRespawnCooldown() * 0.5)
		{
			titleText = "You died!";
			g.setFont(font);
			g.setColor(Color.BLUE);
			g.drawString(titleText, ((width / 2) * SCALE) - g.getFontMetrics().stringWidth(titleText) / 2, height / 4 * SCALE);
		}
	}

	public void renderLevelEnd(Graphics g)
	{
		g.setFont(font);
		g.setColor(Color.ORANGE);
		String time = Timer.getPassedTime(true) + "";
		g.drawString(time, width * SCALE / 2 - g.getFontMetrics().stringWidth(time) / 2, height * SCALE / 2 - font.getSize() / 2);
	}

	public static void setTitleText(String text, int displayTimeInTicks)
	{
		titleText = text;
		titleTextTimer = displayTimeInTicks;
	}
}