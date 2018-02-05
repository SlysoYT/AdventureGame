package game.entity.mob.ability;

import game.entity.mob.effect.EffectAbilityCooldown;
import game.entity.mob.effect.EffectMovementSpeed;
import game.entity.mob.player.Player;

public class AbilitySpeed extends Ability
{
	private int amplifier;
	private int duration;

	public AbilitySpeed(Player player, int cooldown, int duration, int amplifier)
	{
		super(player, cooldown);
		this.amplifier = amplifier;
		this.duration = duration;
	}

	@Override
	protected void onEnable()
	{
		player.applyEffect(new EffectMovementSpeed(duration, amplifier, player));
		player.applyEffect(new EffectAbilityCooldown(duration, amplifier, player));
	}
}
