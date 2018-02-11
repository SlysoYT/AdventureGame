package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class Tile
{
	public Sprite sprite;

	public static final byte DEFAULT_TILE_SIZE = 16;

	public static final Tile TILE_ERROR = new ErrorTile(Sprite.SPRITE_ERROR);
	public static final Tile TILE_VOID = new VoidTile(Sprite.SPRITE_VOID);

	public static final Tile TILE_BOOSTER = new BoosterTile(Sprite.SPRITE_BOOSTER);
	public static final Tile TILE_CHECKPOINT = new CheckpointTile(Sprite.SPRITE_CHECKPOINT);
	public static final Tile TILE_ICE = new IceTile(Sprite.SPRITE_ICE);
	public static final Tile TILE_KILLER = new KillerTile(Sprite.SPRITE_KILLER);
	public static final Tile TILE_QUARTZ = new QuartzTile(Sprite.SPRITE_QUARTZ);
	public static final Tile TILE_QUARTZ_WALL = new QuartzWallTile(Sprite.SPRITE_QUARTZ_WALL);

	//Colors of tiles in level files
	public static final int COL_TILE_BOOSTER = 0xFF00FF66;
	public static final int COL_TILE_CHECKPOINT = 0xFFCC00FF;
	public static final int COL_TILE_ICE = 0xFFA0CFFF;
	public static final int COL_TILE_KILLER = 0xFFFF0000;
	public static final int COL_TILE_VOID = 0xFF0000;
	public static final int COL_TILE_QUARTZ = 0xFFFFFFFF;
	public static final int COL_TILE_QUARTZ_WALL = 0xFFB4B4B4;

	public Tile(Sprite sprite)
	{
		this.sprite = sprite;
	}

	//Tiles render itself
	public void render(int x, int y, Screen screen)
	{

	}

	//Some tiles tick itself (e.g CanonTile)
	public void tick(Tile tile, Sprite sprite, int x, int y, int id)
	{

	}

	public Sprite getSprite()
	{
		return null;
	}

	public boolean isTickable()
	{
		return false;
	}

	public boolean solid()
	{
		return false;
	}

	public boolean liquid()
	{
		return false;
	}

	public boolean booster()
	{
		return false;
	}

	public boolean sliding()
	{
		return false;
	}

	public boolean deadly()
	{
		return false;
	}

	public boolean checkpoint()
	{
		return false;
	}
}
