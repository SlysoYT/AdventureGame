package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class WaterTile extends Tile
{
	private final int TILE_SIZE_SHIFTING = Screen.TILE_SIZE_SHIFTING;

	public WaterTile(Sprite sprite)
	{
		super(sprite);
	}

	public void render(int x, int y, Screen screen)
	{
		boolean xEven = x % 2 == 0;
		boolean yEven = y % 2 == 0;

		if(xEven && yEven) sprite = Sprite.SPRITE_WATER_0;
		else if(!xEven && yEven) sprite = Sprite.SPRITE_WATER_1;
		else if(xEven && !yEven) sprite = Sprite.SPRITE_WATER_2;
		else sprite = Sprite.SPRITE_WATER_3;

		screen.renderTile(x << TILE_SIZE_SHIFTING, y << TILE_SIZE_SHIFTING, this);
	}

	public boolean liquid()
	{
		return true;
	}
}
