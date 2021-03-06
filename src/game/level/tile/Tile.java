/*******************************************************************************
 * Copyright (C) 2018 Thomas Zahner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package game.level.tile;

import game.Game;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;

public class Tile
{
	private Sprite sprite;

	protected final int TILE_SIZE_SHIFTING = Game.getScreen().TILE_SIZE_SHIFTING;
	public static final byte DEFAULT_TILE_SIZE = 16;

	public static final Tile TILE_ERROR = new TileError(Sprite.SPRITE_ERROR);
	public static final Tile TILE_VOID = new TileVoid(Sprite.SPRITE_VOID);

	public static final Tile TILE_DIRT = new TileDirt(Sprite.SPRITE_DIRT);
	public static final Tile TILE_GRASS = new TileGrass(Sprite.SPRITE_GRASS);
	public static final Tile TILE_FLOWER_0 = new TileGrass(Sprite.SPRITE_FLOWER_0);
	public static final Tile TILE_FLOWER_1 = new TileGrass(Sprite.SPRITE_FLOWER_1);
	public static final Tile TILE_FLOWER_2 = new TileGrass(Sprite.SPRITE_FLOWER_2);
	public static final Tile TILE_FLOWER_3 = new TileGrass(Sprite.SPRITE_FLOWER_3);
	public static final Tile TILE_ROCK_GRASS = new TileRock(Sprite.SPRITE_ROCK_GRASS);
	public static final Tile TILE_ROCK_SAND = new TileRock(Sprite.SPRITE_ROCK_SAND);
	public static final Tile TILE_SAND = new TileSand(Sprite.SPRITE_SAND);
	public static final Tile TILE_WATER = new TileWater(null);

	public static final Tile TILE_BOOSTER = new TileBooster(Sprite.SPRITE_BOOSTER);
	public static final Tile TILE_CHECKPOINT = new TileCheckpoint(Sprite.SPRITE_CHECKPOINT);
	public static final Tile TILE_ICE = new TileIce(Sprite.SPRITE_ICE);
	public static final Tile TILE_KILLER = new TileKiller(Sprite.SPRITE_KILLER);
	public static final Tile TILE_QUARTZ = new TileQuartz(Sprite.SPRITE_QUARTZ);
	public static final Tile TILE_QUARTZ_WALL = new TileQuartzWall(Sprite.SPRITE_QUARTZ_WALL);

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
		return sprite;
	}

	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
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
