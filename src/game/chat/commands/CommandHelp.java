package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.player.Player;

public class CommandHelp extends Command
{
	public CommandHelp()
	{
		super("help", "help", "Display a list of commands", (byte) 0);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		List<Command> commands = Chat.getCommands();

		Chat.addMessage(new Message("--------[Help]--------", "Server"));

		for(Command command : commands)
		{
			Chat.addMessage(new Message("!" + command.getUsage() + ": " + command.getDescription(), "Server"));
		}

	}
}
