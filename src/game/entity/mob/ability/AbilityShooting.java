package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.entity.projectile.BoomerangProjectile;
import game.entity.projectile.Projectile;
import game.entity.projectile.WizardProjectile;
import game.input.Mouse;
import game.network.NetworkPackage;
import game.util.GameState;

public class AbilityShooting extends Ability
{
	Projectile projectile;

	public AbilityShooting(Player player, Projectile projectile)
	{
		super(player, projectile.getFireCooldown());
		this.projectile = projectile;
	}

	public AbilityShooting(Player player, Projectile projectile, int cooldown)
	{
		super(player, cooldown);
		this.projectile = projectile;
	}

	@Override
	protected void onEnable()
	{
		double deltaX = Mouse.getX() - (Game.width * Game.SCALE) / 2;
		double deltaY = Mouse.getY() - (Game.height * Game.SCALE) / 2;

		double angle = Math.atan2(deltaY, deltaX); //Atan = tan^-1, difference to atan: doesn't crash when dividing by 0, = atan(deltaY / deltaX)

		if(Game.getGameState() == GameState.IngameOffline)
		{
			if(projectile instanceof WizardProjectile) player.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			else if(projectile instanceof BoomerangProjectile)
				player.shoot(new BoomerangProjectile(player.getX(), player.getY(), angle, player, null));
		}
		else
		{
			if(projectile instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			//TODO
		}
	}
}
