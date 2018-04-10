package game.network.ingame;

import java.util.List;

import game.Game;
import game.entity.Entity;
import game.entity.mob.Mob;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectile;
import game.entity.trap.Trap;
import game.network.Server;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;

public class GetSendDataAsHost
{
	public static byte[] getData(SObject object, String IPAddress)
	{
		if(Server.isClientBanned(IPAddress) || !Server.isClientOnline(IPAddress))
		{
			SString kickPlayer;

			if(Server.isClientBanned(IPAddress)) kickPlayer = SString.String("kickPlayer", "you are banned from this server");
			else kickPlayer = SString.String("kickPlayer", "you got kicked from this server");

			object.addString(kickPlayer);

			byte[] data = new byte[object.getSize()];
			object.getBytes(data, 0);

			return data;
		}

		if(Game.getLevel() != null)
		{
			if(Server.clientShouldLoadLevel(IPAddress))
			{
				object.addField(SField.Long("levelSeed", Game.getLevel().getGameLevel().getSeed()));
				object.addString(SString.String("yourUUID", Game.getLevel().getPlayerByIP(IPAddress).getUUID().toString()));

				byte[] data = new byte[object.getSize()];
				object.getBytes(data, 0);
				return data;
				//TODO: Give the client player a UUID and tell it the client
			}

			List<Entity> entities = Game.getLevel().getAllEntities();
			for(Entity entity : entities)
			{
				if(!(entity instanceof Mob) && !(entity instanceof Projectile) && !(entity instanceof Trap)) continue;

				object.addString(SString.String("UUIDEn", entity.getUUID().toString()));
				object.addString(SString.String("typeEn", entity.getClass().getSimpleName()));
				object.addField(SField.Integer("xPosEn", entity.getX()));
				object.addField(SField.Integer("yPosEn", entity.getY()));

				if(entity instanceof Mob)
				{
					object.addField(SField.Float("xVelMb", ((Mob) entity).getXVelocity()));
					object.addField(SField.Float("yVelMb", ((Mob) entity).getYVelocity()));

					if(entity instanceof Player)
					{
						String playerName;
						if(entity != Game.getLevel().getClientPlayer()) playerName = ((OnlinePlayer) entity).getPlayerName();
						else playerName = "hostPlayer"; //TODO

						object.addString(SString.String("namePl", playerName));
					}
				}
				if(entity instanceof Projectile)
				{
					object.addField(SField.Float("prDir", (float) ((Projectile) entity).getDirection()));
					object.addString(SString.String("prSrcUUID", ((Projectile) entity).getSource().getUUID().toString()));
				}
			}
		}

		byte[] data = new byte[object.getSize()];
		object.getBytes(data, 0);

		return data;
	}
}
