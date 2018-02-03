package game.entity.mob.abilities;

import game.Game;
import game.entity.mob.Mob;
import game.entity.projectile.BoomerangProjectile;
import game.entity.projectile.Projectile;
import game.entity.projectile.WizardProjectile;
import game.input.Mouse;
import game.network.NetworkPackage;
import game.util.GameState;

public class Shooting extends Ability
{
	Projectile projectile;

	public Shooting(Mob mob, Projectile projectile)
	{
		super(mob, projectile.getFireCooldown());
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
			if(projectile instanceof WizardProjectile) mob.shoot(new WizardProjectile(mob.getX(), mob.getY(), angle, mob, null));
			else if(projectile instanceof BoomerangProjectile) mob.shoot(new BoomerangProjectile(mob.getX(), mob.getY(), angle, mob, null));
		}
		else
		{
			if(projectile instanceof WizardProjectile) NetworkPackage.shoot(new WizardProjectile(mob.getX(), mob.getY(), angle, mob, null));
			//TODO
		}
	}
}
