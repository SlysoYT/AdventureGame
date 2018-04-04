package game.entity.mob.player;

import java.util.UUID;

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

	public String getPlayerName()
	{
		return playerName;
	}
}
