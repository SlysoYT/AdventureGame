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

import game.entity.mob.player.Player;

public class CommandTeleport extends Command
{
	public CommandTeleport()
	{
		super("tp", "tp <xPos> <yPos>", "Teleport the sender to a specific location", (byte) 2);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		int xPos = Integer.parseInt(args.get(0));
		int yPos = Integer.parseInt(args.get(1));
		sender.setPosition(xPos, yPos);
	}
}
