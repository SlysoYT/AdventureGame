package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.player.Player;

public abstract class Command
{
	private String name;
	private String usage;
	private String description;
	private byte numberOfArguments;

	protected abstract void onEnable(List<String> args, Player sender);

	protected Command(String name, String usage, String description, byte numberOfArguments)
	{
		this.name = name;
		this.usage = usage;
		this.description = description;
		this.numberOfArguments = numberOfArguments;
	}

	public void enableCommand(List<String> args, Player sender)
	{
		if(args.size() != numberOfArguments)
		{
			if(numberOfArguments == 0) Chat.addMessage(new Message("No arguments expected. Usage: !" + usage, "Server"));
			else
			{
				if(args.size() < numberOfArguments) Chat.addMessage(new Message("More arguments expected. Usage: !" + usage, "Server"));
				else Chat.addMessage(new Message("Less arguments expected. Usage: !" + usage, "Server"));
			}
		}
		else onEnable(args, sender);
	}

	public String getName()
	{
		return name;
	}

	public String getUsage()
	{
		return usage;
	}

	public String getDescription()
	{
		return description;
	}
}
