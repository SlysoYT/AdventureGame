package game.entity.mob;

import java.util.ArrayList;
import java.util.List;

import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.DamageValue;
import game.entity.Entity;
import game.entity.item.ItemHealth;
import game.entity.mob.effect.Effect;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectile;
import game.entity.spawner.ParticleSpawner;
import game.entity.trap.Trap;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.Level;
import game.util.Hitbox;

public abstract class Mob extends Entity
{
	protected int dir = 0;
	private float xChangeFloat = 0, yChangeFloat = 0;
	private float xVelocity = 0, yVelocity = 0;
	private float movementSmoothness = 0.9F;
	private boolean moving = false;
	protected Hitbox hitbox;
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
	protected Mob(int xSpawn, int ySpawn, Hitbox hitbox, Sprite sprite, float maxHealth, float speed, float attackDamage, int attackSpeed)
	{
		this.x = xSpawn;
		this.y = ySpawn;
		this.hitbox = hitbox;
		this.sprite = sprite;
		this.setMaxHealth(maxHealth);
		this.setSpeed(speed);
		this.setAttackDamage(attackDamage);
		this.setAttackSpeed(attackSpeed);
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
		tickMob();

		if(this.isDead())
		{
			if((this instanceof Player))
			{
				dir = 0;
				xVelocity = 0;
				yVelocity = 0;
				xChangeFloat = 0;
				yChangeFloat = 0;
			}
			else
			{
				if(rand.nextInt(7) % 7 == 0) level.add(new ItemHealth(this.getX(), this.getY(), 10.0F));
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

	public void setTrap(Trap trap)
	{
		level.add(trap);
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
		return level.hitboxCollidesWithSolid(x + xChange, y + yChange, hitbox);
	}

	public boolean isInLiquid()
	{
		return Level.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.liquid()
				|| Level.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.liquid();
	}

	public boolean isOnBooster()
	{
		return Level.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.booster()
				|| Level.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.booster();
	}

	public boolean isOnIce()
	{
		return Level.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.sliding()
				|| Level.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.sliding();
	}

	public boolean isOnDeadly()
	{
		return Level.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.deadly()
				|| Level.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
						.deadly();
	}

	public boolean isOnCheckpoint()
	{
		return Level.getTile((x - hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
				.checkpoint()
				&& Level.getTile((x + hitbox.getWidth() / 2) >> Screen.TILE_SIZE_SHIFTING, (y + hitbox.getHeight() / 2) >> Screen.TILE_SIZE_SHIFTING)
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

	public int getDir()
	{
		return dir;
	}

	public Hitbox getHitbox()
	{
		return hitbox;
	}

	public float getMaxHealth()
	{
		return maxHealth;
	}

	public float getSpeed()
	{
		return speed;
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
