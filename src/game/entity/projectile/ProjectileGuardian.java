package game.entity.projectile;

import java.util.UUID;

import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileGuardian extends Projectile
{
	public ProjectileGuardian(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 0.75D, 100, 7.0F, source, new Hitbox(-2, -2, 3, 3), Sprite.PROJECTILE_GUARDIAN, uuid);
	}

	@Override
	protected void tickProjectile()
	{
		//y -= 0.4;
	}

	@Override
	protected void onMobHit(Mob mob)
	{
		mob.damage(damage);
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
