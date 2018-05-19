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
package game.chat.commands;

import java.util.List;

import game.Game;
import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.player.Player;
import game.network.Server;

public class CommandKick extends Command
{
	public CommandKick()
	{
		super("kick", "kick <player>", "Kick a player from the game your hosting", (byte) 1);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		if(!Game.isHostingGame) Chat.addMessage(new Message("You aren't hosting a game!", "Server"));
		else
		{
			Player target = Game.getLevel().getPlayerByName(args.get(0));

			if(target != null)
			{
				Server.kickClient(target.getIPAddress());
				Chat.addMessage(new Message("Kicked player!", "Server"));
			}
			else Chat.addMessage(new Message("Player not found!", "Server"));
		}
	}

}
