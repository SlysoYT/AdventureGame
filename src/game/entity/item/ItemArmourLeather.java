package game.entity.item;

import game.entity.mob.player.Player;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemArmourLeather extends Item
{
	public ItemArmourLeather(int x, int y)
	{
		super(x, y, ItemType.Armour, new Hitbox(2, 5, 11, 6), Sprite.ITEM_ARMOUR_LEATHER);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}
}
