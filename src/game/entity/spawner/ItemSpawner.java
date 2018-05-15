package game.entity.spawner;

import game.entity.Entity;
import game.entity.item.Item;
import game.level.Level;

public class ItemSpawner extends Entity
{
	public ItemSpawner(int x, int y, int amount, Level level, Item item)
	{
		for(int i = 0; i < amount; i++)
		{
			item.setPosition(x, y);
			level.add(item);
		}
	}
}
