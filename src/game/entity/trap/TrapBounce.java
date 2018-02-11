package game.entity.trap;

import java.util.UUID;

import game.entity.mob.Mob;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class TrapBounce extends Trap
{
	public TrapBounce(int x, int y, Mob source, UUID uuid)
	{
		super(x, y, 0F, source, new Hitbox(-3, -3, 6, 6), Sprite.SPRITE_CHECKPOINT, uuid);
	}

	@Override
	public void tickTrap()
	{
	}

	@Override
	protected void onTrigger(Mob trigger)
	{
		double deltaX = trigger.getX() - x;
		double deltaY = trigger.getY() - y;

		if(deltaX < 0) trigger.motion(-4.5F, 0F);
		else trigger.motion(4.5F, 0F);

		if(deltaY < 0) trigger.motion(0F, -4.5F);
		else trigger.motion(0F, 4.5F);
	}

	public void render(Screen screen)
	{
		screen.renderSprite(getX() - Tile.DEFAULT_TILE_SIZE / 2, getY() - Tile.DEFAULT_TILE_SIZE / 2, getSprite(), true);
	}
}
