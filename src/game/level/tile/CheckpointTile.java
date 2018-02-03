package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class CheckpointTile extends Tile
{
	private final int TILE_SIZE_SHIFTING = Screen.TILE_SIZE_SHIFTING;

	public CheckpointTile(Sprite sprite)
	{
		super(sprite);
	}

	public void render(int x, int y, Screen screen)
	{
		screen.renderTile(x << TILE_SIZE_SHIFTING, y << TILE_SIZE_SHIFTING, this);
	}
	
	public boolean checkpoint()
	{
		return true;
	}
}
