package game.entity.item;

import game.entity.mob.player.Player;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemCoin extends Item
{
	public ItemCoin(int x, int y)
	{
		super(x, y, false, new Hitbox(4, 4, 8, 10), Sprite.COIN);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}
}
