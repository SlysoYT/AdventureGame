package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public class TileRock extends Tile
{
	private final int TILE_SIZE_SHIFTING = Screen.TILE_SIZE_SHIFTING;

	public TileRock(Sprite sprite)
	{
		super(sprite);
	}

	public void render(int x, int y, Screen screen)
	{
		screen.renderTile(x << TILE_SIZE_SHIFTING, y << TILE_SIZE_SHIFTING, this);
	}

	public Hitbox getHitbox()
	{
		return new Hitbox(2, 3, 10, 6);
	}
}
