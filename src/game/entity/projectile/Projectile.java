package game.entity.projectile;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.Entity;
import game.entity.mob.Mob;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Sprite;
import game.util.GameState;
import game.util.Hitbox;
import game.util.Vector2i;

public abstract class Projectile extends Entity
{
	private final int xOrigin, yOrigin;
	private double newX, newY;
	private Hitbox hitbox;
	private Mob source;
	private Projectiles type;

	protected double x, y;
	protected double angle;
	protected Sprite sprite;
	protected double distance;
	protected double speed;
	protected int range;
	protected float damage;

	protected Projectile(int x, int y, double direction, double speed, int range, float damage, Mob source, Hitbox hitbox, Sprite sprite,
			Projectiles type, UUID uuid)
	{
		this.x = x;
		this.y = y;
		xOrigin = x;
		yOrigin = y;
		angle = direction;
		this.speed = speed;
		this.range = range;
		this.damage = damage;
		this.source = source;
		this.hitbox = hitbox;
		this.sprite = sprite;
		this.type = type;
		setUUID(uuid);
	}

	protected abstract void tickProjectile();

	protected abstract void onMobHit(Mob mob);

	public void tick()
	{
		tickProjectile();

		if(!level.hitboxCollidesWithSolidTile(getX() + getNewX(), getY() + getNewY(), getHitbox())) move();
		else
		{
			Vector2i collisionPoint = level.hitboxCollidesWithSolidTileVector(getX() + getNewX(), getY() + getNewY(), getHitbox());

			if(collisionPoint != null)
			{
				Sprite[] particleSprites = Sprite.getParticleSpritesFromPosition(collisionPoint.getX(), collisionPoint.getY(), 20);
				level.add(new ParticleSpawner(getX(), getY(), 1.0F, 1.0F, 80, level, particleSprites));
			}
			else System.exit(0);

			PlaySound.playSound(Sounds.hit);
			this.remove();
			return;
		}

		if(Game.getGameState() == GameState.IngameOnline && !Game.isHostingGame) return;

		//Call onMobHit() if hit
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
					if(mob.isDead()) break;
					onMobHit(mob);
					this.remove();
					return;
				}
			}
		}
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

	public int getNewX()
	{
		return (int) newX;
	}

	public int getNewY()
	{
		return (int) newY;
	}

	public int getRange()
	{
		return range;
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

	public Projectiles getProjectileType()
	{
		return type;
	}

	public Mob getSource()
	{
		return source;
	}
}
