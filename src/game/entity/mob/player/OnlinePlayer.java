package game.entity.mob.player;

import java.util.UUID;

import game.graphics.Screen;
import game.level.tile.Tile;

public class OnlinePlayer extends Player
{
	private String playerName;

	public OnlinePlayer(int xPos, int yPos, String IPAddress, String playerName)
	{
		super(xPos, yPos, IPAddress);
		this.playerName = playerName;
	}

	public OnlinePlayer(int xPos, int yPos, UUID uuid, String playerName)
	{
		super(xPos, yPos, uuid);
		this.playerName = playerName;
	}

	public void render(Screen screen)
	{
		if(this.isDead()) return;

		getWalkingSprite();

		screen.renderMob(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, this);
	}

	public String getPlayerName()
	{
		return playerName;
	}
}
