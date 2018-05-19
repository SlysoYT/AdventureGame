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
package game.util;

public class Vector2i
{
	private int x, y;

	public Vector2i()
	{
		set(0, 0);
	}

	public Vector2i(Vector2i vector)
	{
		set(vector.getX(), vector.getY());
	}

	public Vector2i(int x, int y)
	{
		set(x, y);
	}

	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public Vector2i setX(int x)
	{
		this.x = x;
		return this;
	}

	public int getY()
	{
		return y;
	}

	public Vector2i setY(int y)
	{
		this.y = y;
		return this;
	}

	public Vector2i add(Vector2i vector)
	{
		this.x += vector.getX();
		this.y += vector.getY();
		return this;
	}

	public Vector2i subtract(Vector2i vector)
	{
		this.x -= vector.getX();
		this.y -= vector.getY();
		return this;
	}
	
	public boolean equals(Object object)
	{
		if(!(object instanceof Vector2i)) return false;
		Vector2i vector = (Vector2i) object;
		if(vector.getX() == this.getX() && vector.getY() == this.getY()) return true;
		return false;
	}
}
