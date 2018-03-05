package game.entity.mob;

import java.util.List;

import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;
import game.util.Node;
import game.util.Vector2i;

public class StarChaser extends Mob
{
	private List<Node> path = null;

	public StarChaser(int x, int y, float speed)
	{
		super(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 10.0F, speed, 10.0F, 60);
	}

	public void tickMob()
	{
		float xChange = 0;
		float yChange = 0;
		int playerPosX = level.getClientPlayer().getX();
		int playerPosY = level.getClientPlayer().getY();

		Vector2i start = new Vector2i(this.getX() >> Screen.TILE_SIZE_SHIFTING, this.getY() >> Screen.TILE_SIZE_SHIFTING);
		Vector2i end = new Vector2i(playerPosX >> Screen.TILE_SIZE_SHIFTING, playerPosY >> Screen.TILE_SIZE_SHIFTING);
		path = level.findPath(start, end);
		if(path == null) return;
		if(path.size() > 0)
		{
			Vector2i pathVector = path.get(path.size() - 1).tile;
			int xGoal = (pathVector.getX() << Screen.TILE_SIZE_SHIFTING) + this.getHitbox().getXOffset() + this.getHitbox().getWidth() + 1;
			int yGoal = (pathVector.getY() << Screen.TILE_SIZE_SHIFTING) + this.getHitbox().getYOffset() + this.getHitbox().getHeight() + 1;

			if(x < xGoal) xChange += getSpeed();
			if(x > xGoal) xChange -= getSpeed();
			if(y < yGoal) yChange += getSpeed();
			if(y > yGoal) yChange -= getSpeed();

			this.motion(xChange, yChange);
		}
	}

	public void render(Screen screen)
	{
		sprite = Sprite.PLAYER_DOWN[0];

		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}
}
