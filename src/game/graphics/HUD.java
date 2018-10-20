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

public class HUD
{
	private Player clientPlayer;
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

	public HUD(int width, int height, Level level, Keyboard keyboard)
	{
		this.level = level;
		this.clientPlayer = level.getClientPlayer();
		this.width = width;
		this.height = height;
		this.key = keyboard;
	}

	public void tick()
	{
		Chat.tick(key);

		if(clientPlayer.isDead()) return;
		cooldownSecondary = 1.0F - clientPlayer.getSecondaryAbilityCooldownProgress();
		cooldownPassive = 1.0F - clientPlayer.getPassiveAbilityCooldownProgress();
		health = clientPlayer.getCurrentHealth() / clientPlayer.getMaxHealth();
		if(titleTextTimer > 0) titleTextTimer--;
	}

	public void render(Screen screen)
	{
		//Other infos
		//TODO
		//Player infos
		if(clientPlayer.isDead()) return;

		int xPos = clientPlayer.getX() - Game.getScreen().getXOffset() - Tile.DEFAULT_TILE_SIZE / 2;
		int yPos = clientPlayer.getY() - Game.getScreen().getYOffset();

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
		renderPlayerNames(g);
		Chat.render(g);
		if(!clientPlayer.isDead()) renderAlert(g);
		else renderDeathScreen(g);
		if(debugMode)
		{
			renderDebugScreen(g);
			level.renderHitboxes(g);
		}
	}

	private void renderPlayerNames(Graphics g)
	{
		g.setFont(PLAYER_NAME_FONT);
		g.setColor(Color.BLUE);

		List<Player> players = level.getPlayersAlive();

		for(Player player : players)
		{
			if(player instanceof OnlinePlayer)
			{
				String playerName = ((OnlinePlayer) player).getPlayerName();
				int xPos = player.getX() * Game.SCALE - Game.getScreen().getXOffset() * Game.SCALE - g.getFontMetrics().stringWidth(playerName) / 2;
				int yPos = player.getY() * Game.SCALE - Game.getScreen().getYOffset() * Game.SCALE - player.getHitbox().getHeight() / 2
						- PLAYER_NAME_FONT.getSize();
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
		g.drawString("X: " + clientPlayer.getX() + " | Y: " + clientPlayer.getY(), 5, (DEBUG_FONT.getSize() + 3) * 2);
		g.drawString("Velocity: " + clientPlayer.getXVelocity() + ", " + clientPlayer.getYVelocity(), 5, (DEBUG_FONT.getSize() + 3) * 3);
		g.drawString("Dir: " + clientPlayer.getDirectionFacing(), 5, (DEBUG_FONT.getSize() + 3) * 4);
		g.drawString("Mouse: " + Mouse.getX() + ", " + Mouse.getY() + ", " + Mouse.getButton(), 5, (DEBUG_FONT.getSize() + 3) * 5);
		g.drawString("Level: " + Game.getLevel().getLevelName() + ", " + Game.getLevel().getSeed(), 5, (DEBUG_FONT.getSize() + 3) * 6);

		String fpsTpsInfo = "FPS: " + Game.getCurrentFPS() + " (" + Game.getCurrentFrameTime() + " ms) | TPS: " + Game.getCurrentTPS();
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

	public static void setTitleText(String text, int displayTimeInTicks)
	{
		titleText = text;
		titleTextTimer = displayTimeInTicks;
	}
}
