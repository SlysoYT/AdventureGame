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
import game.entity.mob.Guardian;
import game.entity.mob.Salesman;
import game.entity.mob.Slime;
import game.entity.mob.player.Player;

public class CommandSummon extends Command
{
	public CommandSummon()
	{
		super("summon", "summon <mob> or summon <mob> <amount>", "Summon one or multiple mobs", (byte) 1, (byte) 2);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		if(args.size() == 1)
		{
			String mob = args.get(0);
			boolean success = true;

			if(mob.equals("slime")) Game.getLevel().add(new Slime(sender.getX(), sender.getY(), null));
			else if(mob.equals("salesman")) Game.getLevel().add(new Salesman(sender.getX(), sender.getY(), null));
			else if(mob.equals("guardian")) Game.getLevel().add(new Guardian(sender.getX(), sender.getY(), null));
			else success = false;

			if(success) Chat.addMessage(new Message("Summoned the mob successfully!", "Server"));
			else Chat.addMessage(new Message("Unknown mob!", "Server"));
		}
		else
		{
			int amount = Integer.parseInt(args.get(1));
			args.remove(1);

			for(int i = 0; i < amount; i++)
				onEnable(args, sender);
		}
	}
}
