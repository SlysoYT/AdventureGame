package game.entity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Game;
import game.entity.spawner.ItemSpawner;

public class ItemDrop
{
	private Random rand = new Random();

	private List<Item> items = new ArrayList<Item>();
	private List<Integer> itemMaxAmount = new ArrayList<Integer>();
	private List<Integer> itemSpawnProbability = new ArrayList<Integer>();

	/**
	 * Add an item to the item drop
	 * 
	 * @param item
	 * @param maxAmount
	 * @param spawnProbability
	 *            Probability the item drops, ranging from 1-100%
	 */
	public void addItem(Item item, int maxAmount, int spawnProbability)
	{
		items.add(item);
		itemMaxAmount.add(maxAmount);

		if(spawnProbability <= 0) spawnProbability = 1;
		else if(spawnProbability > 100) spawnProbability = 100;
		itemSpawnProbability.add(spawnProbability);
	}

	public void spawnItemDrop(int xPos, int yPos)
	{
		for(int i = 0; i < items.size(); i++)
		{
			int itemAmount = rand.nextInt(itemMaxAmount.get(i)) + 1;

			if(rand.nextFloat() + itemSpawnProbability.get(i) / 100F >= 1F) new ItemSpawner(xPos, yPos, itemAmount, Game.getLevel(), items.get(i));
		}
	}
}
