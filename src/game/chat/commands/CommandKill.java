package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.Mob;
import game.entity.mob.player.Player;
import game.level.Level;

public class CommandKill extends Command
{
	private Level level;

	public CommandKill(Level level)
	{
		super("kill", "kill <target>", "Kill the sender", (byte) 1);
		this.level = level;
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
			List<Mob> mobs = level.getMobs();
			for(Mob mob : mobs)
			{
				mob.kill();
			}
		}
		else Chat.addMessage(new Message("Unknown target: " + target, "Server"));
	}
}
