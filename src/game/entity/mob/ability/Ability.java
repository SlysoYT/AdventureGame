package game.entity.mob.ability;

import game.entity.mob.player.Player;

public abstract class Ability
{
	protected int currentCooldown, cooldown;
	protected Player player;

	protected abstract void onEnable();

	public Ability(Player player, int cooldown)
	{
		this.player = player;

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

	public void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}
}
