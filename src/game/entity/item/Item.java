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
package game.entity.item;

import java.util.List;

import game.entity.Entity;
import game.entity.mob.ability.Ability;
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
		List<Player> targets = level.playersCollidedWithHitbox(x, y, hitbox);

		if(!targets.isEmpty())
		{
			if(type == ItemType.InstantUse)
			{
				onPickup(targets.get(0));
				this.remove();
			}
			else
			{
				for(Player player : targets)
				{
					//If adding item successfully to target players inventory: onPickup(target); and remove
					if(player.getInventory().addItem(this))
					{
						onPickup(player);
						this.remove();
						break;
					}
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
	
	public Ability getItemAbility(Player player)
	{
		return null;
	}
}
