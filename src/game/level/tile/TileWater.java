package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class TileWater extends Tile
{
	private final int TILE_SIZE_SHIFTING = Screen.TILE_SIZE_SHIFTING;

	public TileWater(Sprite sprite)
	{
		super(sprite);
	}

	public void render(int x, int y, Screen screen)
	{
		boolean xEven = x % 2 == 0;
		boolean yEven = y % 2 == 0;

		if(xEven && yEven) setSprite(Sprite.SPRITE_WATER_0);
		else if(!xEven && yEven) setSprite(Sprite.SPRITE_WATER_1);
		else if(xEven && !yEven) setSprite(Sprite.SPRITE_WATER_2);
		else setSprite(Sprite.SPRITE_WATER_3);

		screen.renderTile(x << TILE_SIZE_SHIFTING, y << TILE_SIZE_SHIFTING, this);
	}

	public boolean liquid()
	{
		return true;
	}
}
