package game.entity.item;

import game.entity.mob.player.Player;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemHealth extends Item
{
	private float amountOfHealthRestoring;

	public ItemHealth(int x, int y, float amountOfHealthRestoring)
	{
		super(x, y, ItemType.InstantUse, new Hitbox(1, 5, 14, 12), Sprite.ITEM_HEALTH);
		this.amountOfHealthRestoring = amountOfHealthRestoring;
	}

	@Override
	protected void onPickup(Player trigger)
	{
		trigger.setHealth(trigger.getCurrentHealth() + amountOfHealthRestoring);
	}
}
