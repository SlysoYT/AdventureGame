package game.entity.item;

import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectiles;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityProjectileBoomerang extends Item
{
	public ItemAbilityProjectileBoomerang(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(5, 6, 5, 5), Sprite.PROJECTILE_BOOMERANG);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}
	
	public Ability getItemAbility(Player player)
	{
		return new AbilityShooting(player, Projectiles.ProjectileBoomerang, 70);
	}
}
