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
package game.entity.trap;

import java.util.List;
import java.util.UUID;

import game.entity.Entity;
import game.entity.mob.Mob;
import game.graphics.Sprite;
import game.util.Hitbox;

public abstract class Trap extends Entity
{
	protected Sprite sprite;
	protected int x, y;
	protected float damage;
	protected Hitbox hitbox;
	protected Mob source;

	protected Trap(int x, int y, float damage, Mob source, Hitbox hitbox, Sprite sprite, UUID uuid)
	{
		this.x = x;
		this.y = y;
		this.damage = damage;
		this.source = source;
		this.hitbox = hitbox;
		this.sprite = sprite;
		setUUID(uuid);
	}

	protected abstract void tickTrap();

	protected abstract void onTrigger(Mob trigger);

	public final void tick()
	{
		tickTrap();

		//Handle onTrigger()
		List<Mob> mobs = level.getMobs();
		for(Mob mob : mobs)
		{
			if(mob == this.getSource()) continue;

			for(int corner = 0; corner < 4; corner++)
			{
				int projectileX = this.getX() + this.getHitbox().getXOffset() + this.getHitbox().getWidth() * (corner % 2);
				int projectileY = this.getY() + this.getHitbox().getYOffset() + this.getHitbox().getHeight() * (corner / 2);

				if(projectileX >= mob.getX() + mob.getHitbox().getXOffset()
						&& projectileX <= mob.getX() + mob.getHitbox().getXOffset() + mob.getHitbox().getWidth()
						&& projectileY >= mob.getY() + mob.getHitbox().getYOffset()
						&& projectileY <= mob.getY() + mob.getHitbox().getYOffset() + mob.getHitbox().getHeight())
				{
					onTrigger(mob);
					this.remove();
					return;
				}
			}
		}
	}

	public int getX()
	{
		return (int) x;
	}

	public int getY()
	{
		return (int) y;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public float getDamage()
	{
		return (float) damage;
	}

	public int getSpriteSize()
	{
		return sprite.SIZE;
	}

	public Mob getSource()
	{
		return source;
	}
}
