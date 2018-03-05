package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.item.ItemHealth;
import game.entity.mob.player.Player;
import game.level.Level;

public class CommandGive extends Command
{
	private Level level;

	public CommandGive(Level level)
	{
		super("give", "give <item> <amount>", "Give yourself any items", (byte) 2);
		this.level = level;
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		String item = args.get(0);
		int amount = Integer.parseInt(args.get(1));

		if(item.equals("health"))
		{
			for(int i = 0; i < amount; i++)
				level.getClientPlayer().getInventory().addItem(new ItemHealth(10));
		}
		else Chat.addMessage(new Message("Unknown item", "Server"));
	}
}
