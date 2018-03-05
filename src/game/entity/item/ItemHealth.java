package game.entity.item;

import game.entity.mob.player.Player;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemHealth extends Item
{
	private float amountOfHealthRestoring;

	public ItemHealth(float amountOfHealthRestoring)
	{
		super(0, 0, true, new Hitbox(-8, -8, 16, 16), Sprite.ITEM_HEALTH);
		this.amountOfHealthRestoring = amountOfHealthRestoring;
	}
	
	public ItemHealth(int x, int y, float amountOfHealthRestoring)
	{
		super(x, y, true, new Hitbox(-8, -8, 16, 16), Sprite.ITEM_HEALTH);
		this.amountOfHealthRestoring = amountOfHealthRestoring;
	}

	@Override
	protected void onPickup(Player trigger)
	{
		trigger.setHealth(trigger.getCurrentHealth() + amountOfHealthRestoring);
	}
}
