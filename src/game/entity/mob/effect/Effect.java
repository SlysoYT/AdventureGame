package game.entity.mob.effect;

import game.entity.mob.Mob;
import game.entity.mob.player.Player;

public abstract class Effect
{
	private int duration;
	private int passedTime;
	private boolean active;
	protected int amplifier;
	protected Mob mob;
	protected Player player;

	protected abstract void tickEffect();

	protected abstract void onEnable();

	protected abstract void onDisable();

	protected Effect(int duration, int amplifier, Mob mob)
	{
		this.duration = duration;
		this.amplifier = amplifier;
		this.mob = mob;

		onEnable();
		active = true;
	}

	protected Effect(int duration, int amplifier, Player player)
	{
		this.duration = duration;
		this.amplifier = amplifier;
		this.player = player;

		onEnable();
		active = true;
	}

	public void tick()
	{
		if(passedTime >= duration)
		{
			onDisable();
			active = false;
		}
		else tickEffect();

		passedTime++;
	}

	public boolean isActive()
	{
		return active;
	}
}
