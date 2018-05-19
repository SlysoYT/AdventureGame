/*******************************************************************************
 * Copyright (C) 2018 Thomas Zahner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package game.graphics.GUIs;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.entity.item.Item;
import game.entity.item.ItemType;
import game.graphics.GUI;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Mouse;

public class GUIInventory extends GUI
{
	private final int maxSlotSize = 64;

	private Item[][] slots = new Item[5 * 6][maxSlotSize];
	private Item[][] armourSlots = new Item[2 * 2][maxSlotSize];
	private Item[][] skillSlots = new Item[3 * 1][maxSlotSize];
	private Item[][] coinShopSlot = new Item[1 * 1][maxSlotSize];
	private Item[][] itemShopSlot = new Item[1 * 1][maxSlotSize];

	private List<ShopOffer> shopOffers = new ArrayList<ShopOffer>();
	private int selectedShopOffer = 0;

	private Item[] movingStack = new Item[maxSlotSize];
	private boolean isMovingStack = false;

	private GUIInventoryType type = GUIInventoryType.PlayerInv;

	public GUIInventory()
	{
		super(Sprite.INVENTORY);
	}

	public void setType(GUIInventoryType type)
	{
		this.type = type;
		if(type == GUIInventoryType.Shop) this.sprite = Sprite.INVENTORY_SHOP;
		else this.sprite = Sprite.INVENTORY;
	}

	public void tick()
	{
		if(Mouse.getButton() != 1 || !Mouse.onClick()) return;
		//Normal slots
		for(int x = 0; x < 5; x++)
		{
			for(int y = 0; y < 6; y++)
			{
				//If mouse hovering over slot
				if(Mouse.getX() / Game.SCALE >= getXOffset() + 3 + x * 17 && Mouse.getX() / Game.SCALE <= getXOffset() + 3 + x * 17 + 16
						&& Mouse.getY() / Game.SCALE >= getYOffset() + 3 + y * 17 && Mouse.getY() / Game.SCALE <= getYOffset() + 3 + y * 17 + 16)
				{
					tickSlots(x, y, slots, 5, null);
				}
			}
		}

		if(type == GUIInventoryType.PlayerInv)
		{
			//Armour slots
			for(int x = 0; x < 2; x++)
			{
				for(int y = 0; y < 2; y++)
				{
					//If mouse hovering over slot
					if(Mouse.getX() / Game.SCALE >= getXOffset() + 104 + x * 22 && Mouse.getX() / Game.SCALE <= getXOffset() + 104 + x * 22 + 16
							&& Mouse.getY() / Game.SCALE >= getYOffset() + 26 + y * 20
							&& Mouse.getY() / Game.SCALE <= getYOffset() + 26 + y * 20 + 16)
					{
						tickSlots(x, y, armourSlots, 2, ItemType.Armour);
					}
				}
			}

			//Skill slots
			for(int x = 0; x < 3; x++)
			{
				//If mouse hovering over slot
				if(Mouse.getX() / Game.SCALE >= getXOffset() + 96 + x * 21 && Mouse.getX() / Game.SCALE <= getXOffset() + 96 + x * 21 + 16
						&& Mouse.getY() / Game.SCALE >= getYOffset() + 89 && Mouse.getY() / Game.SCALE <= getYOffset() + 89 + 16)
				{
					tickSlots(x, 0, skillSlots, 3, ItemType.Skill);
				}
			}
		}
		else if(type == GUIInventoryType.Shop)
		{
			//Coin slot
			//If mouse hovering over slot
			if(Mouse.getX() / Game.SCALE >= getXOffset() + 117 && Mouse.getX() / Game.SCALE <= getXOffset() + 117 + 16
					&& Mouse.getY() / Game.SCALE >= getYOffset() + 18 && Mouse.getY() / Game.SCALE <= getYOffset() + 18 + 16)
			{
				tickSlots(0, 0, coinShopSlot, 1, ItemType.Coin);
			}

			if(getSizeOfStack(coinShopSlot[0]) >= shopOffers.get(selectedShopOffer).getAmountOfCoins() && getSizeOfStack(itemShopSlot[0]) == 0)
			{
				itemShopSlot[0][0] = shopOffers.get(selectedShopOffer).getItemToBuy();
				for(int i = 0; i < shopOffers.get(selectedShopOffer).getAmountOfCoins(); i++)
					removeOneItemFromStack(coinShopSlot[0]);
			}

			//Item slot
			//If mouse hovering over slot
			if(Mouse.getX() / Game.SCALE >= getXOffset() + 117 && Mouse.getX() / Game.SCALE <= getXOffset() + 117 + 16
					&& Mouse.getY() / Game.SCALE >= getYOffset() + 75 && Mouse.getY() / Game.SCALE <= getYOffset() + 75 + 16)
			{
				tickSlots(0, 0, itemShopSlot, 1, ItemType.None);
			}
		}
	}

	private void tickSlots(int x, int y, Item[][] slots, int width, ItemType requiredItemType)
	{
		//Move item stack from slot to moving stack
		if(!isMovingStack && slots[x + y * width][0] != null)
		{
			//Remove the players skill
			if(requiredItemType == ItemType.Skill && slots == skillSlots) Game.getLevel().getClientPlayer().setAbility(null, x);

			for(int i = 0; i < slots[x + y * width].length; i++)
			{
				movingStack[i] = slots[x + y * width][i];
			}
			clearSlot(slots, x + y * width);
			isMovingStack = true;
		}
		//Copy moving stack back to selected slot
		else if(isMovingStack)
		{
			if(requiredItemType != null && movingStack[0].getType() != requiredItemType) return;

			if(slots[x + y * width][0] == null)
			{
				//Set the players skill
				if(requiredItemType == ItemType.Skill && slots == skillSlots)
					Game.getLevel().getClientPlayer().setAbility(movingStack[0].getItemAbility(Game.getLevel().getClientPlayer()), x);

				for(int i = 0; i < movingStack.length; i++)
				{
					slots[x + y * width][i] = movingStack[i];
					movingStack[i] = null;
				}
				isMovingStack = false;
			}
			else if(movingStack[0].getClass().equals(slots[x + y * width][0].getClass()))
			{
				while(getSizeOfStack(slots[x + y * width]) < maxSlotSize && getSizeOfStack(movingStack) > 0)
				{
					addItemToStack(movingStack[getSizeOfStack(movingStack) - 1], slots[x + y * width]);
					removeOneItemFromStack(movingStack);
				}

				if(getSizeOfStack(movingStack) <= 0) isMovingStack = false;
			}
		}
	}

	public void render(Screen screen)
	{
		//Inventory
		screen.renderSprite(getXOffset(), getYOffset(), sprite, false);

		//Items in the slots
		renderSlots(slots, 3, 3, 17, 17, 5, 6, screen);

		if(type == GUIInventoryType.PlayerInv)
		{
			renderSlots(armourSlots, 104, 26, 22, 20, 2, 2, screen);
			renderSlots(skillSlots, 95, 89, 21, 0, 3, 1, screen);
		}
		else if(type == GUIInventoryType.Shop)
		{
			renderSlots(coinShopSlot, 117, 18, 0, 0, 1, 1, screen);
			renderSlots(itemShopSlot, 117, 75, 0, 0, 1, 1, screen);
		}

		//Render moving stack at cursor location
		if(isMovingStack)
		{
			Sprite itemSprite = movingStack[0].getSprite();
			screen.renderSprite(Mouse.getX() / Game.SCALE - 8, Mouse.getY() / Game.SCALE - 8, itemSprite, false);
			Sprite.writeValues(String.valueOf(getSizeOfStack(movingStack)), screen, Mouse.getX() / Game.SCALE + 1, Mouse.getY() / Game.SCALE + 1,
					0x000000);
		}
	}

	private void renderSlots(Item[][] slots, int xStart, int yStart, int xSpace, int ySpace, int width, int height, Screen screen)
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				if(slots[x + y * width][0] == null) continue;
				Sprite itemSprite = slots[x + y * width][0].getSprite();
				int xPos = getXOffset() + xStart + x * xSpace;
				int yPos = getYOffset() + yStart + y * ySpace;
				screen.renderSprite(xPos, yPos, itemSprite, false);
				Sprite.writeValues(String.valueOf(getSizeOfStack(slots[x + y * width])), screen, xPos + 9, yPos + 9, 0x000000);
			}
		}
	}

	private int getSizeOfStack(Item[] stack)
	{
		for(int i = 0; i < stack.length; i++)
		{
			if(stack[i] == null) return i;
		}
		return maxSlotSize;
	}

	private void addItemToStack(Item item, Item[] stack)
	{
		if(!stack[0].getClass().equals(item.getClass()) && stack[0] != null) return; //Don't mix up different types of items in one stack
		for(int i = 0; i < maxSlotSize; i++)
		{
			if(stack[i] == null)
			{
				stack[i] = item;
				return;
			}
		}
	}

	private void removeOneItemFromStack(Item[] stack)
	{
		if(stack[0] == null) return;
		for(int i = maxSlotSize - 1; i >= 0; i--)
		{
			if(stack[i] != null)
			{
				stack[i] = null;
				return;
			}
		}
	}

	private void clearSlot(Item[][] slots, int slotNumber)
	{
		for(int i = 0; i < slots[slotNumber].length; i++)
		{
			slots[slotNumber][i] = null;
		}
	}

	public void addShopOffer(int amountOfCoins, Item itemToBuy)
	{
		shopOffers.add(new ShopOffer(amountOfCoins, itemToBuy));
	}

	public void resetShopOffers()
	{
		shopOffers.clear();
		selectedShopOffer = 0;
	}

	/**
	 * Attempt to add the item to the inventory. Return false if failed to add
	 * the specified item to the inventory.
	 * 
	 * @param item
	 * @return
	 */
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

	public boolean armourEquipped()
	{
		for(int i = 0; i < 4; i++)
		{
			if(armourSlots[i][0] != null) return true;
		}
		return false;
	}
}
