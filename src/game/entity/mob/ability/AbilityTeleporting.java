package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.graphics.HUD;
import game.graphics.Screen;
import game.input.Mouse;
import game.level.tile.Tile;

public class AbilityTeleporting extends Ability
{
	private int range;

	public AbilityTeleporting(Player player, int cooldown, int teleportationRange)
	{
		super(player, cooldown);
		this.range = teleportationRange;
	}

	@Override
	protected void onEnable()
	{
		int deltaX = Mouse.getX() / Game.SCALE - Game.getLevel().getClientPlayer().getX() + Screen.getXOffset();
		int deltaY = Mouse.getY() / Game.SCALE - Game.getLevel().getClientPlayer().getY() + Screen.getYOffset();

		//Out of range
		if(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) > range)
		{
			HUD.setTitleText("Target out of range!", 120);
			currentCooldown = 0;
			return;
		}
		//Out of map
		if(player.getX() + deltaX < 0 || player.getY() + deltaY < 0
				|| player.getX() + deltaX > Game.getLevel().getLevelWidth() * Tile.DEFAULT_TILE_SIZE
				|| player.getY() + deltaY > Game.getLevel().getLevelHeight() * Tile.DEFAULT_TILE_SIZE)
		{
			currentCooldown = 0;
			return;
		}

		if(!player.collision(deltaX, deltaY)) player.setPosition(player.getX() + deltaX, player.getY() + deltaY);
		else currentCooldown = 0;
	}
}
