package game.entity.item;

import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectiles;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityProjectileGranade extends Item
{
	public ItemAbilityProjectileGranade(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(4, 7, 6, 6), Sprite.PROJECTILE_GRANADE);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}
	
	public Ability getItemAbility(Player player)
	{
		return new AbilityShooting(player, Projectiles.ProjectileGranade, 70);
	}
}
