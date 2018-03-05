package game.graphics;

import game.Game;
import game.entity.item.Item;
import game.entity.mob.player.Player;
import game.input.Keyboard;

public class Inventory
{
	private Player player;

	private boolean isActive;
	private final int invWidth = 9;
	private final int invHeight = 6;
	private Item[][] slots = new Item[invWidth * invHeight][64];

	public Inventory(Player player)
	{
		this.player = player;
	}

	public void tick(Keyboard key)
	{
		isActive = key.inventory;
	}

	public void render(Screen screen)
	{
		if(!isActive) return;

		//Inventory
		int xOffset = Game.width / 2 - Sprite.INVENTORY.getWidth() / 2;
		int yOffset = Game.height / 2 - Sprite.INVENTORY.getHeight() / 2;
		screen.renderSprite(xOffset, yOffset, Sprite.INVENTORY, false);

		//Items in the slots
		for(int x = 0; x < invWidth; x++)
		{
			for(int y = 0; y < invHeight; y++)
			{
				if(slots[x + y * invWidth][0] == null) continue;
				Sprite itemSprite = slots[x + y * invWidth][0].getSprite();
				int xPos = xOffset + 3 + x * 17;
				int yPos = yOffset + 3 + y * 17;
				screen.renderSprite(xPos, yPos, itemSprite, false);
				Sprite.writeValues(String.valueOf(getStackSizeOfSlot(x + y * invWidth)), screen, xPos + 9, yPos + 9, 0x000000);
			}
		}
	}

	private int getStackSizeOfSlot(int slotNumber)
	{
		for(int i = 0; i < slots[slotNumber].length; i++)
		{
			if(slots[slotNumber][i] == null) return i;
		}
		return 64;
	}

	public boolean addItem(Item item)
	{
		for(int i = 0; i < slots.length; i++)
		{
			//Begin to stack new slot
			if(slots[i][0] == null)
			{
				slots[i][0] = item;
				return true;
			}
			//Stack non empty slot containing the same item type
			if(slots[i][0].getClass().equals(item.getClass()))
			{
				for(int j = 0; j < slots[i].length; j++)
				{
					if(slots[i][j] == null)
					{
						slots[i][j] = item;
						return true;
					}
				}
			}
		}
		return false; //Couldn't add item to any slot, cause all filled with different types or if there are any same types, they are full as well
	}
}
