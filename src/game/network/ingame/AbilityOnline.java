package game.network.ingame;

import game.Game;
import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileBoomerang;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SerializationReader;

public class AbilityOnline
{
	public static Ability primaryAbility;
	public static Ability secondaryAbility;
	public static Ability ultimateAbility;

	public static SObject tick(SObject object)
	{
		if(primaryAbility != null)
		{
			if(primaryAbility instanceof AbilityShooting)
			{
				SField ability = SField.Float("abltyShoot", ((AbilityShooting) (primaryAbility)).getAngle());
				object.addField(ability);
				primaryAbility = null;
			}
		}
		if(secondaryAbility != null)
		{
			SField ability = SField.Boolean("abltySec", true);
			object.addField(ability);
			secondaryAbility = null;
		}
		if(ultimateAbility != null)
		{
			SField ability = SField.Boolean("abltyUlt", true);
			object.addField(ability);
			ultimateAbility = null;
		}

		return object;
	}

	public static void recieveAsHost(String IPAddressSender, SObject object)
	{
		Player sender = Game.getLevel().getPlayerByIP(IPAddressSender);

		if(object.findField("abltyShoot") != null)
		{
			Game.getLevel().add(new ProjectileBoomerang(sender.getX(), sender.getY(),
					SerializationReader.readFloat(object.findField("abltyShoot").getData(), 0), sender, null));
		}
	}
}
