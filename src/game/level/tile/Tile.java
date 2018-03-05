package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public class Tile
{
	public Sprite sprite;

	public static final byte DEFAULT_TILE_SIZE = 16;

	public static final Tile TILE_ERROR = new ErrorTile(Sprite.SPRITE_ERROR);
	public static final Tile TILE_VOID = new VoidTile(Sprite.SPRITE_VOID);

	public static final Tile TILE_DIRT = new DirtTile(Sprite.SPRITE_DIRT);
	public static final Tile TILE_GRASS = new GrassTile(Sprite.SPRITE_GRASS);
	public static final Tile TILE_FLOWER_0 = new GrassTile(Sprite.SPRITE_FLOWER_0);
	public static final Tile TILE_FLOWER_1 = new GrassTile(Sprite.SPRITE_FLOWER_1);
	public static final Tile TILE_FLOWER_2 = new GrassTile(Sprite.SPRITE_FLOWER_2);
	public static final Tile TILE_FLOWER_3 = new GrassTile(Sprite.SPRITE_FLOWER_3);
	public static final Tile TILE_ROCK_GRASS = new RockTile(Sprite.SPRITE_ROCK_GRASS);
	public static final Tile TILE_ROCK_SAND = new RockTile(Sprite.SPRITE_ROCK_SAND);
	public static final Tile TILE_SAND = new SandTile(Sprite.SPRITE_SAND);
	public static final Tile TILE_WATER = new WaterTile(Sprite.SPRITE_WATER_0);

	public static final Tile TILE_BOOSTER = new BoosterTile(Sprite.SPRITE_BOOSTER);
	public static final Tile TILE_CHECKPOINT = new CheckpointTile(Sprite.SPRITE_CHECKPOINT);
	public static final Tile TILE_ICE = new IceTile(Sprite.SPRITE_ICE);
	public static final Tile TILE_KILLER = new KillerTile(Sprite.SPRITE_KILLER);
	public static final Tile TILE_QUARTZ = new QuartzTile(Sprite.SPRITE_QUARTZ);
	public static final Tile TILE_QUARTZ_WALL = new QuartzWallTile(Sprite.SPRITE_QUARTZ_WALL);

	//Colors of tiles in level files and level tiles array
	public static final int COL_TILE_VOID = 0xFF0000;

	public static final int COL_TILE_DIRT = 0xFF825C37;
	public static final int COL_TILE_GRASS = 0xFF479F49;
	public static final int COL_TILE_FLOWER_0 = 0xFF479F59;
	public static final int COL_TILE_FLOWER_1 = 0xFF479F69;
	public static final int COL_TILE_FLOWER_2 = 0xFF479F79;
	public static final int COL_TILE_FLOWER_3 = 0xFF479F89;
	public static final int COL_TILE_ROCK_GRASS = 0xFF9C9C9C;
	public static final int COL_TILE_ROCK_SAND = 0xFF9D9D9D;
	public static final int COL_TILE_SAND = 0xFFFFFAC9;
	public static final int COL_TILE_WATER = 0xFF1385C6;

	public static final int COL_TILE_BOOSTER = 0xFF00FF66;
	public static final int COL_TILE_CHECKPOINT = 0xFFCC00FF;
	public static final int COL_TILE_ICE = 0xFFA0CFFF;
	public static final int COL_TILE_KILLER = 0xFFFF0000;
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

	public Sprite getSprite()
	{
		return null;
	}

	public Hitbox getHitbox()
	{
		return null;
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
