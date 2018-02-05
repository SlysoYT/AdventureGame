package game.entity.mob.ability;

import game.entity.mob.player.Player;

public abstract class AbilityDuration extends Ability
{
	private boolean enabled = false;
	protected int duration;
	private int durationLeft;

	protected abstract void onDisable();

	protected abstract void tickAbility();

	public AbilityDuration(Player player, int cooldown, int duration)
	{
		super(player, cooldown);

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
