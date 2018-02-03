package game.entity.mob.abilities;

import game.entity.mob.Mob;

public abstract class Ability
{
	protected int currentCooldown, cooldown;

	protected Mob mob;

	protected abstract void onEnable();

	public Ability(Mob mob, int cooldown)
	{
		this.mob = mob;

		if(cooldown > 0) this.cooldown = cooldown;
		else this.cooldown = 1;

		this.currentCooldown = this.cooldown;
	}

	public void enable()
	{
		if(currentCooldown <= 0)
		{
			currentCooldown = cooldown;
			onEnable();
		}
	}

	public void tick()
	{
		if(currentCooldown > 0) currentCooldown--;
	}

	public int getCurrentCooldown()
	{
		return currentCooldown;
	}

	public int getCooldown()
	{
		return cooldown;
	}
}
