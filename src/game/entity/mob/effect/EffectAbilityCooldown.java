package game.entity.mob.effect;

import game.entity.mob.player.Player;

public class EffectAbilityCooldown extends Effect
{
	public EffectAbilityCooldown(int duration, int amplifier, Player player)
	{
		super(duration, amplifier, player);
	}

	@Override
	protected void tickEffect()
	{

	}

	@Override
	protected void onEnable()
	{
		player.getPrimaryAbility().setCooldown((int) (player.getPrimaryAbility().getCooldown() * (1 / (1 + 0.3 * this.amplifier))));
	}

	@Override
	protected void onDisable()
	{
		player.getPrimaryAbility().setCooldown((int) (player.getPrimaryAbility().getCooldown() / (1 / (1 + 0.3 * this.amplifier))));
	}
}
