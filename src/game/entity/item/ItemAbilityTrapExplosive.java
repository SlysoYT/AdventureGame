package game.entity.item;

import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityTrap;
import game.entity.mob.player.Player;
import game.entity.trap.Traps;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityTrapExplosive extends Item
{
	public ItemAbilityTrapExplosive(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(3, 7, 8, 3), Sprite.TRAP_EXPLOSIVE_1);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}

	public Ability getItemAbility(Player player)
	{
		return new AbilityTrap(player, Traps.TrapExplosive, 70);
	}
}
