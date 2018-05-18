package game.entity.projectile;

import java.util.UUID;

import game.entity.explosion.Explosion;
import game.entity.mob.Mob;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileGranade extends Projectile
{
	private int timeUntilDetonation = 250;

	public ProjectileGranade(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 2.5D, 250, 0.0F, source, new Hitbox(-4, -2, 6, 6), Sprite.PROJECTILE_GRANADE, Projectiles.ProjectileGranade, uuid);
	}

	@Override
	protected void tickProjectile()
	{
		if(timeUntilDetonation-- <= 0)
		{
			onMobHit(null);
			this.remove();
		}

		if(speed > 0.2) speed *= 0.97;
		else speed = 0;

		//Spark particles
		level.add(new ParticleSpawner((int) (x), (int) (y - 5), rand.nextFloat() - 0.5F, -rand.nextFloat(), 10, 1, level, Sprite.PARTICLE_SPARK));
	}

	@Override
	protected void onTileCollisioin()
	{
		level.add(new Explosion((int) this.x, (int) this.y, null, 10.0F));
	}

	@Override
	protected void onMobHit(Mob mob)
	{
		level.add(new Explosion((int) this.x, (int) this.y, null, 10.0F));
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
