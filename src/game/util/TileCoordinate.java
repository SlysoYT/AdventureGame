package game.util;

import game.level.tile.Tile;

public class TileCoordinate
{
	private int x, y;
	
	public TileCoordinate(int x, int y)
	{
		this.x = x * Tile.DEFAULT_TILE_SIZE;
		this.y = y * Tile.DEFAULT_TILE_SIZE;
	}
	
	public TileCoordinate(int x, int y, Hitbox hitbox)
	{
		this.x = x * Tile.DEFAULT_TILE_SIZE + Math.abs(hitbox.getXOffset());
		this.y = y * Tile.DEFAULT_TILE_SIZE + Math.abs(hitbox.getYOffset());
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
