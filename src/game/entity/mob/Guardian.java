package game.entity.mob;

import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileGuardian;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class Guardian extends Mob
{
	public Guardian(int x, int y, float speed)
	{
		super(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 10.0F, speed, 10.0F, 60);
	}

	public void tickMob()
	{
		Player target = level.getNearestPlayer(this);
		//double dir = Math.atan(arg0); //TODO
		//level.add(new ProjectileGuardian(x, y, dir, this, null));
	}

	public void render(Screen screen)
	{
		sprite = Sprite.PLAYER_DOWN[0];

		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}
}
