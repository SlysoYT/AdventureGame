package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.entity.trap.TrapBounce;
import game.entity.trap.Traps;
import game.util.GameState;

public class AbilityTrap extends Ability
{
	Traps trap;

	public AbilityTrap(Player player, Traps trap, int cooldown)
	{
		super(player, cooldown, AbilityType.Secondary);
		this.trap = trap;
	}

	@Override
	protected void onEnable()
	{
		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(trap.ordinal() == 0) player.setTrap(new TrapBounce(player.getX(), player.getY(), player, null));
		}
		else
		{
			//if(trap instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			//TODO
		}
	}
}
