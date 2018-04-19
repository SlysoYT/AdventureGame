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
