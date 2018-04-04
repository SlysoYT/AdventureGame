package game.entity.item;

import game.entity.Entity;
import game.entity.mob.player.Player;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public abstract class Item extends Entity
{
	private ItemType type;
	protected Sprite sprite;
	protected Sprite[] sprites;
	private long start = System.nanoTime();

	protected Item(int x, int y, ItemType type, Hitbox hitbox, Sprite sprite)
	{
		this.x = x;
		this.y = y;
		this.type = type;
		this.hitbox = hitbox;
		this.sprite = sprite;
	}

	protected Item(int x, int y, ItemType type, Hitbox hitbox, Sprite[] sprites)
	{
		this.x = x;
		this.y = y;
		this.type = type;
		this.hitbox = hitbox;
		this.sprite = sprites[sprites.length - 1];
		this.sprites = sprites;
	}

	protected abstract void onPickup(Player trigger);

	public final void tick()
	{
		Player target = level.anyPlayerCollidedWithHitbox(x, y, hitbox);

		if(target != null)
		{
			if(type == ItemType.InstantUse)
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

		if(sprites == null)
		{
			screen.renderSprite(x, y + yOffset, sprite, true);
		}
		else
		{
			int index = (int) (Math.abs(3 - ((System.nanoTime() - start) % 700000000 / 100000000)));
			screen.renderSprite(x, y + yOffset, sprites[index], true);
		}
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public ItemType getType()
	{
		return type;
	}
}
