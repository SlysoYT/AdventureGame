package game.entity.explosion;

import java.util.List;
import java.util.UUID;

import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.Entity;
import game.entity.lighting.LightSource;
import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public class Explosion extends Entity
{
	private Sprite sprite;
	private LightSource lightSource;

	private int lightRadius = 10;
	private final int MAX_LIGHT_RADIUS = 30;
	private boolean lightRadiusIncreasing = true;

	private long start;
	private float damage;
	private boolean canMakesDamage = true;

	public Explosion(int x, int y, UUID uuid, float damage)
	{
		hitbox = new Hitbox(-6, -6, 12, 12);
		this.x = x;
		this.y = y;
		setUUID(uuid);
		this.damage = damage;
		this.lightSource = new LightSource(x, y, 25, null);
	}

	public void tick()
	{
		//Only at start
		if(start == 0)
		{
			start = System.currentTimeMillis();
			PlaySound.playSound(Sounds.explosion);
		}

		if(canMakesDamage && System.currentTimeMillis() - start >= 30)
		{
			level.add(lightSource); //Light effect

			List<Mob> collidedMobs = level.mobsCollidedWithHitbox(x, y, hitbox);
			for(Mob mob : collidedMobs)
			{
				mob.damage(damage);
			}

			canMakesDamage = false;
		}

		if(lightSource != null)
		{
			if(lightRadiusIncreasing)
			{
				lightRadius += 2;
				if(lightRadius >= MAX_LIGHT_RADIUS) lightRadiusIncreasing = false;
			}
			else lightRadius -= 2;

			lightSource.setRadius(lightRadius);
		}

		if(System.currentTimeMillis() - start >= 280)
		{
			lightSource.remove();
			this.remove();
		}
	}

	public void render(Screen screen)
	{
		sprite = Sprite.EXPLOSION[5 - (int) ((System.currentTimeMillis() - start) % 300) / 50];
		screen.renderSprite(x - 8, y - 8, sprite, true);
	}
}
