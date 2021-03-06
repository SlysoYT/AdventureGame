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
package game.entity.mob;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.item.ItemAbilityProjectileBoomerang;
import game.entity.item.ItemAbilityProjectileBullet;
import game.entity.item.ItemAbilityProjectileGranade;
import game.entity.item.ItemAbilityTrapExplosive;
import game.entity.item.ItemCoin;
import game.entity.mob.player.Player;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;
import game.util.Node;
import game.util.PathFinder;
import game.util.Vector2i;

public class Slime extends Mob
{
	private float xChange = 0, yChange = 0;
	private int xGoal, yGoal;

	private float jumpHeight = 3.5F;
	private float jumpHeightOffset = 0.0F;
	private float yVelocity = jumpHeight;
	private float gravity = 0.17F + rand.nextFloat() / 6;

	private boolean shouldIncreaseY = false;
	private boolean shouldDecreaseY = false;
	private byte jumpDelay = 0;

	private List<Node> path = null;
	private PathFinder pathFinder;

	public Slime(int x, int y, UUID uuid)
	{
		super(x, y, new Hitbox(-7, -1, 13, 7), Sprite.SLIME_DOWN, 20.0F, 0.75F, 10.0F, 30, uuid);

		getItemDrop().addItem(new ItemCoin(x, y), 3, 50);
		getItemDrop().addItem(new ItemAbilityProjectileGranade(x, y), 1, 5);
		getItemDrop().addItem(new ItemAbilityProjectileBoomerang(x, y), 1, 5);
		getItemDrop().addItem(new ItemAbilityProjectileBullet(x, y), 1, 5);
		getItemDrop().addItem(new ItemAbilityTrapExplosive(x, y), 1, 5);

		this.setMovementSmoothness(0.8F);

		xGoal = x;
		yGoal = y;
	}

	public void tickMob()
	{
		xChange = 0;
		yChange = 0;

		Player target = level.getNearestPlayer(this);
		if(pathFinder == null) pathFinder = new PathFinder(level);

		if(target != null)
		{
			if(rand.nextInt(100) == 0) //Randomness: don't execute performance costly calculation every tick
			{
				Vector2i start = new Vector2i(this.getX() >> Game.getScreen().TILE_SIZE_SHIFTING, this.getY() >> Game.getScreen().TILE_SIZE_SHIFTING);
				Vector2i end = new Vector2i(target.getX() >> Game.getScreen().TILE_SIZE_SHIFTING,
						target.getY() >> Game.getScreen().TILE_SIZE_SHIFTING);

				path = pathFinder.findPath(start, end);
				if(path == null) return;
				if(path.size() > 0)
				{
					Vector2i pathVector = path.get(path.size() - 1).tile;
					xGoal = (pathVector.getX() << Game.getScreen().TILE_SIZE_SHIFTING) + this.getHitbox().getXOffset() + this.getHitbox().getWidth()
							+ 1;
					yGoal = (pathVector.getY() << Game.getScreen().TILE_SIZE_SHIFTING) + this.getHitbox().getYOffset() + this.getHitbox().getHeight()
							+ 1;
				}
			}
		}
		else
		{
			if(Math.sqrt(Math.pow(xGoal - this.getX(), 2) + Math.pow(yGoal - this.getY(), 2)) < 10)
			{
				int xPos = 0, yPos = 0;
				while(true)
				{
					xPos = rand.nextInt(Tile.DEFAULT_TILE_SIZE * Game.getLevel().getLevelWidth());
					yPos = rand.nextInt(Tile.DEFAULT_TILE_SIZE * Game.getLevel().getLevelHeight());
					if(!Game.getLevel().hitboxCollidesWithSolidTile(xPos, yPos, hitbox)) break;
				}
				xGoal = xPos;
				yGoal = yPos;
			}
		}

		if(jumpDelay > 0)
		{
			jumpDelay--;
			return;
		}

		if(yGoal > y) shouldDecreaseY = true;
		else shouldDecreaseY = false;
		if(yGoal < y) shouldIncreaseY = true;
		else shouldIncreaseY = false;

		if(shouldIncreaseY) jumpHeightOffset = -2.0F;
		else if(shouldDecreaseY) jumpHeightOffset = 1.0F;
		else jumpHeightOffset = 0.0F;

		yChange -= yVelocity;
		if(yVelocity > -(jumpHeight + jumpHeightOffset + (rand.nextFloat() - 0.5F))) yVelocity -= gravity;
		else
		{
			resetJump();
			jumpDelay = (byte) (5 + rand.nextInt(30));
			level.add(new ParticleSpawner(x, y + 15, 0.01F, 0.01F, 20, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner(x + 4, y + 15, 0.5F, 0.15F, 20, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner(x - 4, y + 15, 0.5F, 0.15F, 20, 3, level, Sprite.PARTICLE_SLIME));
		}

		if(xGoal <= x) xChange -= getSpeed();
		else xChange += getSpeed();

		this.motion(xChange, yChange);

		//Attacking
		List<Player> collidedPlayers = level.playersCollidedWithHitbox(x, y, hitbox);
		if(!collidedPlayers.isEmpty())
		{
			this.attack(collidedPlayers.get(0));
		}
	}

	public void render(Screen screen)
	{
		sprite = Sprite.SLIME_DOWN;
		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}

	private void resetJump()
	{
		yVelocity = jumpHeight;
	}
}
