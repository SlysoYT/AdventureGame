package game.entity.mob.abilities;

import game.entity.mob.Mob;

public class MovementSpeed extends AbilityDuration
{
	private float factor;

	public MovementSpeed(Mob mob, int cooldown, int duration, float factor)
	{
		super(mob, cooldown, duration);
		this.factor = factor;
	}

	@Override
	protected void onEnable()
	{
		mob.setSpeed(mob.getSpeed() * factor);
	}

	@Override
	protected void onDisable()
	{
		mob.setSpeed(mob.getSpeed() / factor);
	}

	@Override
	protected void tickAbility()
	{
		if(isEnabled())
		{
		}
	}
}
