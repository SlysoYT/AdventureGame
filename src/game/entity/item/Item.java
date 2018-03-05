package game.entity.item;

import game.entity.Entity;
import game.entity.mob.player.Player;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public abstract class Item extends Entity
{
	protected boolean instantUse;
	protected Hitbox hitbox;
	protected Sprite sprite;
	private long start = System.nanoTime();

	protected Item(int x, int y, boolean instantUse, Hitbox hitbox, Sprite sprite)
	{
		this.x = x;
		this.y = y;
		this.instantUse = instantUse;
		this.hitbox = hitbox;
		this.sprite = sprite;
	}

	protected abstract void onPickup(Player trigger);

	public final void tick()
	{
		Player target = level.anyPlayerCollidedWithHitbox(x, y, hitbox);

		if(target != null)
		{
			if(instantUse)
			{
				onPickup(target);
				this.remove();
			}
			else
			{
				//If adding item successfully to target players inventory: onPickup(target); and remove
				if(target.getInventory().addItem(this))
				{
					onPickup(target);
					this.remove();
				}
			}
		}
	}

	public final void render(Screen screen)
	{
		int yOffset = (int) Math.abs((2 - ((System.nanoTime() - start) % 500000000 / 100000000)));
		screen.renderSprite(x + hitbox.getXOffset(), y + yOffset + hitbox.getYOffset(), sprite, true);
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public Sprite getSprite()
	{
		return sprite;
	}
}
