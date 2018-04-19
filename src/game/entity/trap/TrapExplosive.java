package game.entity.trap;

import java.util.UUID;

import game.entity.explosion.Explosion;
import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class TrapExplosive extends Trap
{
	public TrapExplosive(int x, int y, Mob source, UUID uuid)
	{
		super(x, y, 8.5F, source, new Hitbox(-3, -3, 6, 6), Sprite.SPRITE_CHECKPOINT, uuid);
	}

	@Override
	public void tickTrap()
	{
	}

	@Override
	protected void onTrigger(Mob trigger)
	{
		level.add(new Explosion(x, y, null, damage));
		this.remove();
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}