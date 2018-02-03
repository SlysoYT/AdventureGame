package game.entity.projectile;

import java.util.UUID;

import game.entity.Entity;
import game.entity.mob.Mob;
import game.graphics.Sprite;
import game.util.Hitbox;

public abstract class Projectile extends Entity
{
	protected final int xOrigin, yOrigin;
	protected double angle;
	protected Sprite sprite;
	protected double x, y;
	protected double newX, newY;
	protected double distance;
	protected double speed, range;
	protected float damage;
	protected int fireCooldown;
	protected Hitbox hitbox;
	protected Mob source;

	public Projectile(int x, int y, double direction, Mob source, UUID uuid)
	{
		setUUID(uuid);
		xOrigin = x;
		yOrigin = y;
		angle = direction;
		this.x = x;
		this.y = y;
		this.source = source;
	}

	protected void move()
	{
		distance = Math.sqrt((xOrigin - x) * (xOrigin - x) + (yOrigin - y) * (yOrigin - y));

		newX = speed * Math.cos(angle);
		newY = speed * Math.sin(angle);

		x += newX;
		y += newY;
		if(distance >= range) remove();
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

	public double getDirection()
	{
		return angle;
	}

	public double getSpeed()
	{
		return speed;
	}

	public float getDamage()
	{
		return (float) damage;
	}

	public int getSpriteSize()
	{
		return sprite.SIZE;
	}

	public int getFireCooldown()
	{
		return fireCooldown;
	}

	public Mob getSource()
	{
		return source;
	}
}
