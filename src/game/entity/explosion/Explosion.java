package game.entity.explosion;

import java.util.List;
import java.util.UUID;

import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.Entity;
import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public class Explosion extends Entity
{
	private Sprite sprite;
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
			List<Mob> collidedMobs = level.mobsCollidedWithHitbox(x, y, hitbox);

			for(Mob mob : collidedMobs)
			{
				mob.damage(damage);
			}

			canMakesDamage = false;
		}

		if(System.currentTimeMillis() - start >= 280) this.remove();
	}

	public void render(Screen screen)
	{
		sprite = Sprite.EXPLOSION[5 - (int) ((System.currentTimeMillis() - start) % 300) / 50];
		screen.renderSprite(x - 8, y - 8, sprite, true);
	}
}
