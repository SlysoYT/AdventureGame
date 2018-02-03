package game.entity.mob.abilities;

import game.Game;
import game.entity.mob.Mob;
import game.graphics.HUD;
import game.input.Mouse;
import game.level.tile.Tile;

public class Teleporting extends Ability
{
	private int range = 250;

	public Teleporting(Mob mob, int cooldown, int teleportRange)
	{
		super(mob, cooldown);
		this.range = teleportRange;
	}

	@Override
	protected void onEnable()
	{
		int deltaX = Mouse.getX() - (Game.width / 2) * Game.SCALE;
		int deltaY = Mouse.getY() - (Game.height / 2) * Game.SCALE;

		//Out of range
		if(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) > range)
		{
			HUD.setTitleText("Target out of range!", 120);
			currentCooldown = 0;
			return;
		}
		//Out of map
		if(mob.getX() + deltaX / Game.SCALE < 0 || mob.getY() + deltaY / Game.SCALE < 0
				|| mob.getX() + deltaX / Game.SCALE > Game.getLevel().getLevelWidth() * Tile.DEFAULT_TILE_SIZE
				|| mob.getY() + deltaY / Game.SCALE > Game.getLevel().getLevelHeight() * Tile.DEFAULT_TILE_SIZE)
		{
			currentCooldown = 0;
			return;
		}

		if(!mob.collision(deltaX / Game.SCALE, deltaY / Game.SCALE))
			mob.setPosition(mob.getX() + deltaX / Game.SCALE, mob.getY() + deltaY / Game.SCALE);
		else currentCooldown = 0;
	}
}
