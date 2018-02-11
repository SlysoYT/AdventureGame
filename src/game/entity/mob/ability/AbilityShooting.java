package game.entity.mob.ability;

import game.Game;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileBoomerang;
import game.entity.projectile.ProjectileBullet;
import game.entity.projectile.Projectiles;
import game.input.Mouse;
import game.util.GameState;

public class AbilityShooting extends Ability
{
	Projectiles projectile;

	public AbilityShooting(Player player, Projectiles projectile, int cooldown)
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
			if(projectile.ordinal() == 0) player.shoot(new ProjectileBoomerang(player.getX(), player.getY(), angle, player, null));
			else if(projectile.ordinal() == 1) player.shoot(new ProjectileBullet(player.getX(), player.getY(), angle, player, null));
		}
		else
		{
			//if(projectile instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(player.getX(), player.getY(), angle, player, null));
			//TODO
		}
	}
}
