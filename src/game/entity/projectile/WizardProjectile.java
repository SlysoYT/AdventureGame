package game.entity.projectile;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.audio.PlaySound;
import game.audio.Sounds;
import game.entity.mob.Mob;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.GameState;
import game.util.Hitbox;

public class WizardProjectile extends Projectile
{
	public WizardProjectile(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, source, uuid);
		speed = 2.5D;
		range = 100;
		damage = 10.0F;
		fireCooldown = 20;
		sprite = Sprite.wizardProjectile;
		hitbox = new Hitbox(-2, -2, 3, 3);
	}

	public void tick()
	{
		if(!level.tileCollision((int) (x + newX), (int) (y + newY), 6, 3, 3)) move();
		else
		{
			level.add(new ParticleSpawner((int) x, (int) y, 1.0F, 1.0F, 80, 20, level, Sprite.particleQuartz));
			PlaySound.playSound(Sounds.hit);
			this.remove();
			return;
		}

		if(Game.getGameState() == GameState.IngameOnline && !Game.isHostingGame) return;

		//Knock mobs away if hit
		List<Mob> mobs = level.getMobs();
		for(Mob mob : mobs)
		{
			if(mob == this.source) continue;

			for(int corner = 0; corner < 4; corner++)
			{
				int projectileX = this.getX() + this.getHitbox().getXOffset() + this.getHitbox().getWidth() * (corner % 2);
				int projectileY = this.getY() + this.getHitbox().getYOffset() + this.getHitbox().getHeight() * (corner / 2);

				if(projectileX >= mob.getX() + mob.getHitbox().getXOffset()
						&& projectileX <= mob.getX() + mob.getHitbox().getXOffset() + mob.getHitbox().getWidth()
						&& projectileY >= mob.getY() + mob.getHitbox().getYOffset()
						&& projectileY <= mob.getY() + mob.getHitbox().getYOffset() + mob.getHitbox().getHeight())
				{
					mob.motion((float) (2 * speed * Math.cos(angle)), (float) (2 * speed * Math.sin(angle)));
					mob.damage(damage);
					this.remove();
					return;
				}
			}
		}
	}

	public void render(Screen screen)
	{
		screen.renderProjectile((int) (x) - Tile.DEFAULT_TILE_SIZE / 2, (int) (y) - Tile.DEFAULT_TILE_SIZE / 2, this);
	}
}
