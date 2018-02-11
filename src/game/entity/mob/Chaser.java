package game.entity.mob;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.util.Hitbox;
import game.level.tile.Tile;

public class Chaser extends Mob
{
	public Chaser(int x, int y, float speed)
	{
		initMob(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 10.0F, speed, 10.0F, 60);
	}

	public void tick()
	{
		/*
		 * Player player = level.getClientPlayer(); float xChange = 0; float
		 * yChange = 0; if(Math.sqrt((player.getX() - x) * (player.getX() - x) +
		 * (player.getY() - y) * (player.getY() - y)) > 10) { if(player.getX() <
		 * x) xChange -= getSpeed(); else if(player.getX() > x) xChange +=
		 * getSpeed(); if(player.getY() < y) yChange -= getSpeed(); else
		 * if(player.getY() > y) yChange += getSpeed(); move(xChange, yChange,
		 * hitbox); }
		 */
		tickMob();
	}

	public void render(Screen screen)
	{
		sprite = Sprite.PLAYER_DOWN[0];

		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}
}
