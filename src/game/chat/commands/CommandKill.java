package game.chat.commands;

import java.util.List;

import game.entity.mob.player.Player;

public class CommandKill extends Command
{
	public CommandKill()
	{
		super("kill", "kill", "Kill the sender", (byte) 0);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		sender.kill();
	}
}
