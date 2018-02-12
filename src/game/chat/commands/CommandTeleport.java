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
