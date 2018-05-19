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
import game.entity.mob.Mob;
import game.entity.mob.player.Player;

public class CommandKill extends Command
{
	public CommandKill()
	{
		super("kill", "kill or kill <target>", "Kill yourself or the specified target", (byte) 0, (byte) 1);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		if(args.size() == 0)
		{
			sender.kill();
			return;
		}

		String target = args.get(0);

		if(target.equals("all"))
		{
			List<Mob> mobs = Game.getLevel().getMobs();
			for(Mob mob : mobs)
			{
				mob.kill();
			}
		}
		else Chat.addMessage(new Message("Unknown target: " + target, "Server"));
	}
}
