package game.level.tile;

import game.graphics.Screen;
import game.graphics.Sprite;

public class Tile
{
	public Sprite sprite;

	public static final byte DEFAULT_TILE_SIZE = 16;

	public static Tile boosterTile = new BoosterTile(Sprite.boosterSprite);
	public static Tile checkpointTile = new CheckpointTile(Sprite.checkpointSprite);
	public static Tile errorTile = new ErrorTile(Sprite.errorSprite);
	public static Tile iceTile = new IceTile(Sprite.iceSprite);
	public static Tile killerTile = new KillerTile(Sprite.killerSprite);
	public static Tile voidTile = new VoidTile(Sprite.voidSprite);
	public static Tile quartzTile = new QuartzTile(Sprite.quartzSprite);
	public static Tile quartzWallTile = new QuartzWallTile(Sprite.quartzWallSprite);

	public static Tile blockTile = new BlockTile(Sprite.blockSprite);
	public static Tile dirtTile = new DirtTile(Sprite.dirtSprite);
	public static Tile grassTile = new GrassTile(Sprite.grassSprite);
	public static Tile sandTile = new SandTile(Sprite.sandSprite);
	public static Tile waterTile0 = new WaterTile(Sprite.waterSprite0);
	public static Tile waterTile1 = new WaterTile(Sprite.waterSprite1);
	public static Tile waterTile2 = new WaterTile(Sprite.waterSprite2);
	public static Tile waterTile3 = new WaterTile(Sprite.waterSprite3);

	//Colors of tiles in level files
	public static final int colBoosterTile = 0xFF00FF66;
	public static final int colCheckpointTile = 0xFFCC00FF;
	public static final int colIceTile = 0xFFA0CFFF;
	public static final int colKillerTile = 0xFFFF0000;
	public static final int colVoidTile = 0xFF0000;
	public static final int colQuartzTile = 0xFFFFFFFF;
	public static final int colQuartzWallTile = 0xFFB4B4B4;

	public static final int colBlockTile = 0xFF805030;
	public static final int colDirtTile = 0xFF907040;
	public static final int colGrassTile = 0xFF15FF00;
	public static final int colSandTile = 0xFFF4E4B4;
	public static final int colWaterTile = 0xFF0066FF;

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
