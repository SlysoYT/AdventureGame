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
package game.entity;

import java.util.Random;
import java.util.UUID;

import game.graphics.Screen;
import game.level.Level;
import game.util.Hitbox;

public abstract class Entity
{
	private boolean removed = false;
	private UUID uuid = UUID.randomUUID();

	protected int x, y;
	protected Hitbox hitbox;
	protected Level level;
	protected final Random rand = new Random();

	public void tick()
	{
	}

	public void render(Screen screen)
	{
	}

	//Remove from level
	public void remove()
	{
		removed = true;
	}

	public boolean isRemoved()
	{
		return removed;
	}

	public void init(Level level)
	{
		this.level = level;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		if(uuid == null) return;
		this.uuid = uuid;
	}
}
