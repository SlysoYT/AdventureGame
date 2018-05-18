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
		super(x, y, 8.5F, source, new Hitbox(-5, -2, 8, 3), Sprite.TRAP_EXPLOSIVE_1, uuid);
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
		if(System.nanoTime() % 1_000_000_000 <= 500_000_000) sprite = Sprite.TRAP_EXPLOSIVE_1;
		else sprite = Sprite.TRAP_EXPLOSIVE_2;

		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
