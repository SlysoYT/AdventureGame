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
		super("summon", "summon <mob>", "Summon the specified target", (byte) 1);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
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
}
