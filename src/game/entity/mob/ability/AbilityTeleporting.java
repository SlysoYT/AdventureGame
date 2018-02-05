package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.graphics.HUD;
import game.input.Mouse;
import game.level.tile.Tile;

public class AbilityTeleporting extends Ability
{
	private int range = 250;

	public AbilityTeleporting(Player player, int cooldown, int teleportationRange)
	{
		super(player, cooldown);
		this.range = teleportationRange;
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
		if(player.getX() + deltaX / Game.SCALE < 0 || player.getY() + deltaY / Game.SCALE < 0
				|| player.getX() + deltaX / Game.SCALE > Game.getLevel().getLevelWidth() * Tile.DEFAULT_TILE_SIZE
				|| player.getY() + deltaY / Game.SCALE > Game.getLevel().getLevelHeight() * Tile.DEFAULT_TILE_SIZE)
		{
			currentCooldown = 0;
			return;
		}

		if(!player.collision(deltaX / Game.SCALE, deltaY / Game.SCALE))
			player.setPosition(player.getX() + deltaX / Game.SCALE, player.getY() + deltaY / Game.SCALE);
		else currentCooldown = 0;
	}
}
