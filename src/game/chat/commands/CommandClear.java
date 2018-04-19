package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.entity.mob.player.Player;

public class CommandClear extends Command
{
	public CommandClear()
	{
		super("clear", "clear", "Clears all chat messages", (byte) 0);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		Chat.clearChatMessages();
	}
}
