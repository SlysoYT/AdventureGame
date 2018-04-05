package game.entity.mob.ability;

import game.entity.mob.player.Player;
import game.network.ingame.AbilityOnline;

public abstract class Ability
{
	protected int currentCooldown, cooldown;
	protected Player player;
	private AbilityType type;

	protected abstract void onEnable();

	protected Ability(Player player, int cooldown, AbilityType type)
	{
		this.player = player;
		this.type = type;

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
			if(type == AbilityType.Primary) AbilityOnline.primaryAbility = this;
			else if(type == AbilityType.Secondary) AbilityOnline.secondaryAbility = this;
			else if(type == AbilityType.Ultimate) AbilityOnline.ultimateAbility = this;
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
