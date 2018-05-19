package game.entity.item;

import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectiles;
import game.graphics.Sprite;
import game.util.Hitbox;

public class ItemAbilityProjectileBullet extends Item
{
	public ItemAbilityProjectileBullet(int x, int y)
	{
		super(x, y, ItemType.Skill, new Hitbox(6, 7, 3, 3), Sprite.PROJECTILE_BULLET);
	}

	@Override
	protected void onPickup(Player trigger)
	{
	}
	
	public Ability getItemAbility(Player player)
	{
		return new AbilityShooting(player, Projectiles.ProjectileBullet, 70);
	}
}
