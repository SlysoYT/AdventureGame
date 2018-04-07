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
				object.addString(SString.String("typeEn", entity.getClass().getName()));
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
					object.addField(SField.Integer("prType", ((Projectile) entity).getProjectileType().ordinal()));
					object.addString(SString.String("prSrcUUID", ((Projectile) entity).getSource().getUUID().toString()));
				}
			}

			//Tick players
			/*
			 * List<Player> players = Game.getLevel().getPlayers(); for(Player
			 * player : players) { String playerName;
			 * 
			 * if(player != Game.getLevel().getClientPlayer()) { playerName =
			 * ((OnlinePlayer) player).getPlayerName(); } else { playerName =
			 * "hostPlayer"; //TODO }
			 * 
			 * object.addString(SString.String("plUUID",
			 * player.getUUID().toString()));
			 * object.addString(SString.String("plName", playerName));
			 * object.addField(SField.Integer("plXPos", player.getX()));
			 * object.addField(SField.Integer("plYPos", player.getY()));
			 * object.addField(SField.Float("plXVel", player.getXVelocity()));
			 * object.addField(SField.Float("plYVel", player.getYVelocity())); }
			 * 
			 * //Tick all projectiles List<Projectile> projectiles =
			 * Game.getLevel().getProjectiles(); for(Projectile projectile :
			 * projectiles) { object.addString(SString.String("prUUID",
			 * projectile.getUUID().toString()));
			 * object.addField(SField.Integer("prXPos", projectile.getX()));
			 * object.addField(SField.Integer("prYPos", projectile.getY()));
			 * object.addField(SField.Float("prDir", (float)
			 * projectile.getDirection()));
			 * object.addField(SField.Integer("prType",
			 * projectile.getProjectileType().ordinal()));
			 * object.addString(SString.String("prSrcUUID",
			 * projectile.getSource().getUUID().toString())); }
			 */
		}

		byte[] data = new byte[object.getSize()];
		object.getBytes(data, 0);

		return data;
	}
}
