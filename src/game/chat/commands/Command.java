package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.player.Player;

public abstract class Command
{
	private String command;
	private String usage;
	private String description;
	private byte numberOfArguments = -1;
	private byte minNumberOfArguments = -1;
	private byte maxNumberOfArguments = -1;

	protected abstract void onEnable(List<String> args, Player sender);

	protected Command(String command, String usage, String description, byte numberOfArguments)
	{
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.numberOfArguments = numberOfArguments;
	}

	protected Command(String command, String usage, String description, byte minNumberOfArguments, byte maxNumberOfArguments)
	{
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.minNumberOfArguments = minNumberOfArguments;
		this.maxNumberOfArguments = maxNumberOfArguments;
	}

	public void enableCommand(List<String> args, Player sender)
	{
		if(numberOfArguments > -1)
		{
			if(args.size() != numberOfArguments)
			{
				if(numberOfArguments == 0) Chat.addMessage(new Message("No arguments expected. Usage: !" + usage, "Server"));
				else if(args.size() > numberOfArguments) Chat.addMessage(new Message("Less arguments expected. Usage: !" + usage, "Server"));
				else Chat.addMessage(new Message("More arguments expected. Usage: !" + usage, "Server"));
			}
			else onEnable(args, sender);

			return;
		}
		else if(minNumberOfArguments > -1 && maxNumberOfArguments > minNumberOfArguments)
		{
			if(args.size() < minNumberOfArguments) Chat.addMessage(new Message("More arguments expected. Usage: !" + usage, "Server"));
			else if(args.size() > maxNumberOfArguments)  Chat.addMessage(new Message("Less arguments expected. Usage: !" + usage, "Server"));
			else onEnable(args, sender);
		}
	}

	public String getCommand()
	{
		return command;
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
