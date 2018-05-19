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
