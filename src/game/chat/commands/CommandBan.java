package game.chat.commands;

import java.util.List;

import game.Game;
import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.player.Player;
import game.network.Server;

public class CommandBan extends Command
{
	public CommandBan()
	{
		super("ban", "ban <player>", "Ban a player from the game your hosting", (byte) 1);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		if(!Game.isHostingGame) Chat.addMessage(new Message("You aren't hosting a game!", "Server"));
		else
		{
			Player target = Game.getLevel().getPlayerByName(args.get(0));

			if(target != null)
			{
				Server.banClient(target.getIPAddress());
				Chat.addMessage(new Message("Banned player!", "Server"));
			}
			else Chat.addMessage(new Message("Player not found!", "Server"));
		}
	}

}
