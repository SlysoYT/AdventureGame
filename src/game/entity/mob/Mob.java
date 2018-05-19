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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import game.Game;
import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.DamageValue;
import game.entity.Entity;
import game.entity.item.ItemDrop;
import game.entity.mob.effect.Effect;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectile;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.GameState;
import game.util.Hitbox;

public abstract class Mob extends Entity
{
	private int dir = 0;
	private float xChangeFloat = 0, yChangeFloat = 0;
	private float xVelocity = 0, yVelocity = 0;
	private float movementSmoothness = 0.9F;
	private int disanceMoved = 0;
	private boolean moving = false;
	private ItemDrop itemDrop = new ItemDrop();
	protected Sprite sprite;

	private float maxHealth;
	private float currentHealth;
	private float speed;
	private float attackDamage;
	private int attackSpeed;
	private int currentAttackCooldown;

	private List<Effect> effects = new ArrayList<Effect>();

	protected abstract void tickMob();

	public abstract void render(Screen screen);

	/**
	 * Initialize all mob variables such as the starting location, the hitbox,
	 * etc.
	 */
	protected Mob(int xSpawn, int ySpawn, Hitbox hitbox, Sprite sprite, float maxHealth, float speed, float attackDamage, int attackSpeed, UUID uuid)
	{
		this.x = xSpawn;
		this.y = ySpawn;
		this.hitbox = hitbox;
		this.sprite = sprite;
		this.setMaxHealth(maxHealth);
		this.setSpeed(speed);
		this.setAttackDamage(attackDamage);
		this.setAttackSpeed(attackSpeed);
		setUUID(uuid);
	}

	private void move(int xChange, int yChange)
	{
		//If moving on both axis, split the movement up in two independent changes -> processes the collision
		if(xChange != 0 && yChange != 0)
		{
			move(xChange, 0);
			move(0, yChange);
			return;
		}

		if(!collision(xChange, yChange))
		{
			x += xChange;
			y += yChange;
			disanceMoved += Math.abs(xChange) + Math.abs(yChange);
		}
		else
		{
			if(xChange > 0)
			{
				for(int i = xChange; i > 0; i--)
				{
					if(!collision(i, 0))
					{
						move(i, 0);
						break;
					}
				}
				xVelocity = 0;
			}
			else if(xChange < 0)
			{
				for(int i = xChange; i < 0; i++)
				{
					if(!collision(i, 0))
					{
						move(i, 0);
						break;
					}
				}
				xVelocity = 0;
			}
			if(yChange > 0)
			{
				for(int i = yChange; i > 0; i--)
				{
					if(!collision(0, i))
					{
						move(0, i);
						break;
					}
				}
				yVelocity = 0;
			}
			else if(yChange < 0)
			{
				for(int i = yChange; i < 0; i++)
				{
					if(!collision(0, i))
					{
						move(0, i);
						break;
					}
				}
				yVelocity = 0;
			}
		}
	}

	/**
	 * Moves the mob in float precision, only moves the mob if not collided with
	 * a solid block. It's obviously not possible to move a mob for half a
	 * pixel, but the mob moves if the change is more than one pixel. The float
	 * overflow gets taken in account for the next ticks.
	 */
	private void move(float xChange, float yChange)
	{
		if(xChange == 0.0F && yChange == 0.0F) return;

		xChangeFloat += xChange;
		yChangeFloat += yChange;

		if(xChangeFloat >= 1 || yChangeFloat >= 1 || xChangeFloat <= -1 || yChangeFloat <= -1)
		{
			move((int) (xChangeFloat), (int) (yChangeFloat));
			xChangeFloat -= (int) (xChangeFloat);
			yChangeFloat -= (int) (yChangeFloat);
		}
	}

	/**
	 * Use this method to apply motion to a mob.
	 */
	public void motion(float xMotion, float yMotion)
	{
		if(Math.abs(xMotion) > Math.abs(this.xVelocity)) this.xVelocity = xMotion;
		if(Math.abs(yMotion) > Math.abs(this.yVelocity)) this.yVelocity = yMotion;
	}

	public final void tick()
	{
		if(this instanceof Player || Game.isHostingGame || Game.getGameState() == GameState.IngameOffline) tickMob();

		if(this.isDead())
		{
			if((this instanceof Player))
			{
				moving = false;
				dir = 0;
				xVelocity = 0;
				yVelocity = 0;
				xChangeFloat = 0;
				yChangeFloat = 0;
			}
			else
			{
				itemDrop.spawnItemDrop(x, y); //Spawn possible items
				remove();
			}
			return;
		}

		updateCurrentAttackCooldown();
		tickEffects();

		if(xVelocity != 0)
		{
			move(xVelocity, 0);
			if(Math.abs(xVelocity) > 0.15F) xVelocity *= movementSmoothness;
			else xVelocity = 0;
		}
		if(yVelocity != 0)
		{
			move(0, yVelocity);
			if(Math.abs(yVelocity) > 0.15F) yVelocity *= movementSmoothness;
			else yVelocity = 0;
		}

		if(xVelocity != 0 || yVelocity != 0) moving = true;
		else moving = false;

		if(yVelocity < 0) dir = 0;
		if(xVelocity > 0) dir = 1;
		if(yVelocity > 0) dir = 2;
		if(xVelocity < 0) dir = 3;
	}

	public void shoot(Projectile projectile)
	{
		level.add(projectile);
	}

	public void damage(float damage)
	{
		if(damage == 0F) return;
		currentHealth -= Math.abs(damage);
		PlaySound.playSound(Sounds.hurt);
		spawnBlood();
		level.add(new DamageValue(damage, x, y));
	}

	public void heal()
	{
		currentHealth = maxHealth;
	}

	public void kill()
	{
		damage(Float.POSITIVE_INFINITY);
	}

	private void spawnBlood()
	{
		new ParticleSpawner(x, y, 0.85F, 0.75F, 100, 20, level, Sprite.PARTICLE_BLOOD);
	}

	private void updateCurrentAttackCooldown()
	{
		if(currentAttackCooldown > 0) currentAttackCooldown--;
	}

	private void tickEffects()
	{
		for(int i = 0; i < effects.size(); i++)
		{
			if(effects.get(i).isActive()) effects.get(i).tick();
			else effects.remove(i);
		}
	}

	public void applyEffect(Effect effect)
	{
		effects.add(effect);
	}

	/**
	 * The mob attacks the specified target mob if the current attack cooldown
	 * is 0 and deals its attack damage to the target.
	 */
	public void attack(Mob target)
	{
		if(currentAttackCooldown != 0) return;
		currentAttackCooldown = attackSpeed;
		target.damage(attackDamage);
	}

	public boolean collision(int xChange, int yChange)
	{
		return level.hitboxCollidesWithSolidTile(x + xChange, y + yChange, hitbox);
	}

	public boolean isInLiquid()
	{
		return Game.getLevel()
				.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING).liquid()
				|| Game.getLevel()
						.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.liquid();
	}

	public boolean isOnBooster()
	{
		return Game.getLevel()
				.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.booster()
				|| Game.getLevel()
						.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.booster();
	}

	public boolean isOnIce()
	{
		return Game.getLevel()
				.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.sliding()
				|| Game.getLevel()
						.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.sliding();
	}

	public boolean isOnDeadly()
	{
		return Game.getLevel()
				.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING).deadly()
				|| Game.getLevel()
						.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.deadly();
	}

	public boolean isOnCheckpoint()
	{
		return Game.getLevel()
				.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.checkpoint()
				&& Game.getLevel()
						.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.checkpoint();
	}

	public boolean isDead()
	{
		if(currentHealth <= 0) return true;
		return false;
	}

	public boolean canAttack()
	{
		if(currentAttackCooldown < 0 || attackDamage <= 0) return false;
		if(currentAttackCooldown > 0) return false;
		return true;
	}

	//Setters

	public void setAttackDamage(float attackDamage)
	{
		this.attackDamage = attackDamage;
	}

	public void setAttackSpeed(int attackSpeed)
	{
		this.attackSpeed = attackSpeed;
	}

	public void setHealth(float health)
	{
		currentHealth = health;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
	}

	public void setMaxHealth(float health)
	{
		maxHealth = health;
		currentHealth = maxHealth;
	}

	public void setMovementSmoothness(float movementSmoothness)
	{
		this.movementSmoothness = movementSmoothness;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	//Getters

	public float getAttackDamage()
	{
		return attackDamage;
	}

	public int getAttackSpeed()
	{
		return attackSpeed;
	}

	public int getCurrentAttackCooldown()
	{
		return currentAttackCooldown;
	}

	public float getCurrentHealth()
	{
		return currentHealth;
	}

	public float getXVelocity()
	{
		return xVelocity;
	}

	public float getYVelocity()
	{
		return yVelocity;
	}

	public int getDirectionFacing()
	{
		return dir;
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public ItemDrop getItemDrop()
	{
		return itemDrop;
	}

	public float getMaxHealth()
	{
		return maxHealth;
	}

	public float getSpeed()
	{
		return speed;
	}

	public int getDistanceMoved()
	{
		return disanceMoved;
	}

	public boolean isMoving()
	{
		return moving;
	}

	public void setMoving(boolean moving)
	{
		moving = this.moving;
	}
}
