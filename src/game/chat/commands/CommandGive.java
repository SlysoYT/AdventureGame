package game.chat.commands;

import java.util.List;

import game.chat.Chat;
import game.chat.Message;
import game.entity.item.ItemArmourLeather;
import game.entity.item.ItemCoin;
import game.entity.item.ItemHealth;
import game.entity.mob.player.Player;

public class CommandGive extends Command
{
	public CommandGive()
	{
		super("give", "give <item> <amount>", "Give yourself any items", (byte) 2);
	}

	@Override
	protected void onEnable(List<String> args, Player sender)
	{
		String item = args.get(0);
		int amount = Integer.parseInt(args.get(1));

		if(item.equals("health"))
		{
			for(int i = 0; i < amount; i++)
				if(!sender.getInventory().addItem(new ItemHealth(0, 0, 10))) break;
		}
		else if(item.equals("coin") || item.equals("coins") || item.equals("money"))
		{
			for(int i = 0; i < amount; i++)
				if(!sender.getInventory().addItem(new ItemCoin(0, 0))) break;
		}
		else if(item.equals("armour"))
		{
			for(int i = 0; i < amount; i++)
				if(!sender.getInventory().addItem(new ItemArmourLeather(0, 0))) break;
		}
		else
		{
			Chat.addMessage(new Message("Unknown item", "Server"));
			return;
		}

		Chat.addMessage(new Message("Given item " + item, "Server"));
	}
}
