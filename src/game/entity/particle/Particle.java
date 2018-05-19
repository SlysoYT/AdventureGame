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
package game.entity.particle;

import game.entity.Entity;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;

public class Particle extends Entity
{
	private Sprite sprite;

	private int life;
	private int deltaTicks = 0;

	protected double xx, yy, zz;
	protected double xAmount, yAmount, zAmount;

	public Particle(int x, int y, float xSpeed, float ySpeed, int life, Sprite sprite)
	{
		this.x = x;
		this.y = y;
		this.xx = x;
		this.yy = y;
		this.life = life + (rand.nextInt((int) (life * 0.8F)) - (int) (life * 0.4F));
		this.sprite =sprite;

		this.xAmount = rand.nextGaussian() * xSpeed;
		this.yAmount = rand.nextGaussian() * ySpeed;
		this.zz = rand.nextFloat() * 5;
	}

	public void tick()
	{
		deltaTicks++;
		if(deltaTicks > life) remove();
		zAmount -= 0.2D;

		if(zz < 0)
		{
			zz = 0;
			zAmount *= -0.6D; //Makes them bounce of the floor and get slower
			xAmount *= 0.55D;
			yAmount *= 0.55D;
		}

		move(xx + xAmount, (yy + yAmount) + (zz + zAmount));
	}

	private void move(double x, double y)
	{
		if(collision(x + xAmount, y + yAmount))
		{
			this.xAmount *= -0.6F;
			this.yAmount *= -0.6F;
			this.zAmount *= -0.6F;
		}
		this.xx += xAmount;
		this.yy += yAmount;
		this.zz += zAmount;
	}

	private boolean collision(double x, double y)
	{
		boolean solid = false;

		for(int corner = 0; corner < 4; corner++)
		{
			//Transforms pixel into tile precision and "asks" the appropriate tile, if it's solid
			double xt = (x - corner % 2 * 16) / Tile.DEFAULT_TILE_SIZE; //With values after corner % 2 or corner / 2, it's possible
			double yt = (y - corner / 2 * 16) / Tile.DEFAULT_TILE_SIZE; //to modify the position and size of the hitbox
			int intX = (int) (Math.ceil(xt));
			int intY = (int) (Math.ceil(yt));
			if(corner % 2 == 0) intX = (int) Math.floor(xt); //Left side
			if(corner / 2 == 0) intY = (int) Math.floor(yt); //Upper side
			if(level.getTile(intX, intY).solid()) solid = true;
		}

		return solid;
	}

	public void render(Screen screen)
	{
		screen.renderSprite((int) xx, (int) yy - (int) zz, sprite, true);
	}
}
