package game.entity.mob.abilities;

import game.entity.mob.Mob;

public abstract class AbilityDuration extends Ability
{
	private boolean enabled = false;
	private int duration, durationLeft;

	protected abstract void onDisable();

	protected abstract void tickAbility();

	public AbilityDuration(Mob mob, int cooldown, int duration)
	{
		super(mob, cooldown);

		if(duration > 0) this.duration = duration;
		else this.duration = 1;

		this.durationLeft = this.duration;
	}

	public void enable()
	{
		if(currentCooldown <= 0)
		{
			enabled = true;
			onEnable();
			currentCooldown = cooldown;
			durationLeft = duration;
		}
	}

	public void tick()
	{
		tickAbility();

		if(!enabled && currentCooldown > 0) currentCooldown--;
		if(enabled)
		{
			if(durationLeft > 0) durationLeft--;
			else
			{
				enabled = false;
				onDisable();
				currentCooldown = cooldown;
			}
		}
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
