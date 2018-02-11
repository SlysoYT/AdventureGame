package game.entity.projectile;

import java.util.UUID;

import game.entity.mob.Mob;
import game.entity.mob.effect.EffectMovementSpeed;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class ProjectileBoomerang extends Projectile
{
	public ProjectileBoomerang(int x, int y, double direction, Mob source, UUID uuid)
	{
		super(x, y, direction, 1.8D, 250, 20F, 100, source, new Hitbox(-3, -3, 5, 5), Sprite.PROJECTILE_BOOMERANG, uuid);
	}

	@Override
	protected void tickProjectile()
	{
		double deltaX = this.getX() - getSource().getX();
		double deltaY = this.getY() - getSource().getY();

		if(distance > 70) speed -= 0.05D;
		if(speed < 0)
		{
			distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
			angle = Math.atan2(deltaY, deltaX);

			//Player catched projectile
			if(distance < 3.0D)
			{
				this.remove();
				getSource().applyEffect(new EffectMovementSpeed(120, 2, getSource()));
			}
		}
	}

	@Override
	protected void onMobHit(Mob mob)
	{
		mob.motion((float) (2 * speed * Math.cos(angle)), (float) (2 * speed * Math.sin(angle)));
		mob.damage(damage);
	}
	
	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
