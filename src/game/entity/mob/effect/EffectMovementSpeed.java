package game.entity.mob.effect;

import game.entity.mob.Mob;

public class EffectMovementSpeed extends Effect
{
	public EffectMovementSpeed(int duration, int amplifier, Mob mob)
	{
		super(duration, amplifier, mob);
	}

	@Override
	protected void tickEffect()
	{
	}

	@Override
	protected void onEnable()
	{
		if(amplifier >= 0) mob.setSpeed((mob.getSpeed() * (1 + 0.2F * this.amplifier)));
		else mob.setSpeed(mob.getSpeed() / (this.amplifier * -1.5F));
	}

	@Override
	protected void onDisable()
	{
		if(amplifier >= 0) mob.setSpeed(mob.getSpeed() / (1 + 0.2F * this.amplifier));
		else mob.setSpeed(mob.getSpeed() * (this.amplifier * -1.5F));
	}
}
