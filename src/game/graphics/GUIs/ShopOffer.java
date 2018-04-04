package game.graphics.GUIs;

import game.entity.item.Item;

public class ShopOffer
{
	private int amountOfCoins;
	private Item itemToBuy;

	public ShopOffer(int amountOfCoins, Item itemToBuy)
	{
		this.amountOfCoins = amountOfCoins;
		this.itemToBuy = itemToBuy;
	}

	public int getAmountOfCoins()
	{
		return amountOfCoins;
	}

	public Item getItemToBuy()
	{
		return itemToBuy;
	}
}
