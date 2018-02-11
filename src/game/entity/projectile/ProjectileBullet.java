package game.entity.projectile;

import java.util.UUID;

import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileBullet extends Projectile
{
	public ProjectileBullet(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 2.5D, 100, 10F, 20, source, new Hitbox(-2, -2, 3, 3), Sprite.PROJECTILE_WIZARD, uuid);
	}

	@Override
	protected void tickProjectile()
	{
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
