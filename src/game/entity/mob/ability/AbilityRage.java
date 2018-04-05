package game.entity.mob.ability;

import game.entity.mob.effect.EffectMovementSpeed;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectiles;

public class AbilityRage extends AbilityDuration
{
	private Ability abilityShooting;

	public AbilityRage(Player player, int cooldown, int duration)
	{
		super(player, cooldown, duration, AbilityType.Ultimate);
		abilityShooting = new AbilityShooting(player, Projectiles.ProjectileBullet, 10);

	}

	@Override
	protected void onEnable()
	{
		player.applyEffect(new EffectMovementSpeed(duration, -3, player));
	}

	@Override
	protected void onDisable()
	{
	}

	@Override
	protected void tickAbility()
	{
		if(isEnabled())
		{
			abilityShooting.tick();
			abilityShooting.enable();
		}
	}
}
