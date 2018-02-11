package game.network;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileBullet;
import game.level.Level;
import game.network.Serialization.SField;
import game.network.Serialization.SObject;
import game.network.Serialization.SString;
import game.network.Serialization.SerializationReader;
import game.util.GameState;

public class NetworkPackage
{
	public static final short VERSION = 201;

	private boolean isClient;
	private boolean clientIsOnline = false;
	private static Level level;

	private static Projectile projectile = null;

	public NetworkPackage(boolean isClient)
	{
		this.isClient = isClient;
	}

	public void tick(Level level)
	{
		NetworkPackage.level = level;
	}

	public static void shoot(Projectile projectile)
	{
		NetworkPackage.projectile = projectile;
	}

	public byte[] getSendData(String targetIPAddress)
	{
		if(isClient)
		{
			SObject object = new SObject("Debug");
			SField version = SField.Short("version", VERSION);

			object.addField(version);

			if(!clientIsOnline)
			{
				SString joinPlayer = SString.String("requestJoin", "player123"); //TODO: Custom name and request join only in the beginning
				object.addString(joinPlayer);
			}

			if(level != null)
			{
				SField xVelocity = SField.Float("xVel", level.getClientPlayer().getXVelocity());
				SField yVelocity = SField.Float("yVel", level.getClientPlayer().getYVelocity());

				object.addField(xVelocity);
				object.addField(yVelocity);

				if(projectile != null)
				{
					SField projectileDir = SField.Float("prDir", (float) projectile.getDirection());
					object.addField(projectileDir);
					projectile = null;
				}
			}

			byte[] data = new byte[object.getSize()];
			object.getBytes(data, 0);

			return data;
		}
		else
		{
			SObject object = new SObject("Debug");
			SField version = SField.Short("version", VERSION);
			object.addField(version);

			if(!Server.isClientOnline(targetIPAddress))
			{
				SString kickPlayer = SString.String("kickPlayer", "connection refused");
				object.addString(kickPlayer);

				byte[] data = new byte[object.getSize()];
				object.getBytes(data, 0);

				return data;
			}

			if(level != null)
			{
				//Tick players
				List<Player> players = level.getPlayers();
				for(Player player : players)
				{
					String playerName;
					int isYourClientPlayer = 0;

					if(player != level.getClientPlayer())
					{
						playerName = ((OnlinePlayer) player).getPlayerName();
					}
					else
					{
						playerName = "hostPlayer"; //TODO
					}

					if(player == level.getPlayer(targetIPAddress)) isYourClientPlayer = 1;

					SString playerUUID = SString.String("plUUID", player.getUUID().toString());
					SString name = SString.String("plName", playerName);
					SField xPos = SField.Integer("plXPos", player.getX());
					SField yPos = SField.Integer("plYPos", player.getY());
					SField xVel = SField.Float("plXVel", player.getXVelocity());
					SField yVel = SField.Float("plYVel", player.getYVelocity());
					SField isYourPlayer = SField.Integer("plCl", isYourClientPlayer);

					object.addString(playerUUID);
					object.addString(name);
					object.addField(xPos);
					object.addField(yPos);
					object.addField(xVel);
					object.addField(yVel);
					object.addField(isYourPlayer);
				}

				//Tick all projectiles
				List<Projectile> projectiles = level.getProjectiles();
				for(Projectile projectile : projectiles)
				{
					SString projectileUUID = SString.String("prUUID", projectile.getUUID().toString());
					SField xPos = SField.Integer("prXPos", projectile.getX());
					SField yPos = SField.Integer("prYPos", projectile.getY());
					SField angle = SField.Float("prDir", (float) projectile.getDirection());

					object.addString(projectileUUID);
					object.addField(xPos);
					object.addField(yPos);
					object.addField(angle);
				}

			}

			byte[] data = new byte[object.getSize()];
			object.getBytes(data, 0);

			return data;
		}
	}

	public void recieveDataAsClient(byte[] data)
	{
		if(level == null) return;

		SObject object = SObject.deserialize(data, 0);
		if(!validPacket(object)) return;

		if(object.findString("kickPlayer") != null)
		{
			Game.setGameState(GameState.TitleScreen);
			System.out.println("You got kicked from the server! Reason: " + object.findString("kickPlayer").getString());
			return;
		}

		clientIsOnline = true;

		//Players
		List<SString> playerUUIDs = object.findStrings("plUUID");
		List<SString> playerNames = object.findStrings("plName");
		List<SField> xPositionPlayers = object.findFields("plXPos");
		List<SField> yPositionPlayers = object.findFields("plYPos");
		List<SField> xVelocityPlayers = object.findFields("plXVel");
		List<SField> yVelocityPlayers = object.findFields("plYVel");
		List<SField> plCls = object.findFields("plCl");

		for(int i = 0; i < playerUUIDs.size(); i++)
		{
			UUID uuid = UUID.fromString(playerUUIDs.get(i).getString());
			Player player = level.getPlayer(uuid);

			int xPos = SerializationReader.readInt(xPositionPlayers.get(i).getData(), 0);
			int yPos = SerializationReader.readInt(yPositionPlayers.get(i).getData(), 0);
			float xVelocity = SerializationReader.readFloat(xVelocityPlayers.get(i).getData(), 0);
			float yVelocity = SerializationReader.readFloat(yVelocityPlayers.get(i).getData(), 0);

			//Client player
			if(SerializationReader.readInt(plCls.get(i).getData(), 0) == 1)
			{
				level.getClientPlayer().setPosition(xPos, yPos);
				continue;
			}
			//Add player to level
			if(player == null)
			{
				level.add(new OnlinePlayer(xPos, yPos, uuid, playerNames.get(i).getString()));
				return;
			}
			//Tick player
			player.setPosition(xPos, yPos);
			player.motion(xVelocity, yVelocity);
		}

		//Projectiles
		List<SString> projectileUUIDs = object.findStrings("prUUID");
		List<SField> xPositionProjectiles = object.findFields("prXPos");
		List<SField> yPositionProjectiles = object.findFields("prYPos");
		List<SField> angleProjectiles = object.findFields("prDir");

		for(int i = 0; i < projectileUUIDs.size(); i++)
		{
			UUID uuid = UUID.fromString(projectileUUIDs.get(i).getString());
			Projectile projectile = level.getProjectile(uuid);

			int xPos = SerializationReader.readInt(xPositionProjectiles.get(i).getData(), 0);
			int yPos = SerializationReader.readInt(yPositionProjectiles.get(i).getData(), 0);
			float angle = SerializationReader.readFloat(angleProjectiles.get(i).getData(), 0);

			//Add projectile to level
			if(projectile == null)
			{
				level.add(new ProjectileBullet(xPos, yPos, angle, null, uuid));
			}
			//Tick projectile
			else
			{
				projectile.setPosition(xPos, yPos);
			}

		}
	}

	public void recieveDataAsHost(byte[] data, String IPAddressSender)
	{
		if(level == null) return;

		SObject object = SObject.deserialize(data, 0);
		if(!validPacket(object)) return;

		if(!Server.isClientOnline(IPAddressSender))
		{
			if(object.findString("requestJoin") != null)
			{
				Server.addClient(IPAddressSender);
				if(!Server.isClientOnline(IPAddressSender)) return;

				level.add(new OnlinePlayer(level.getSpawnLocation().getX(), level.getSpawnLocation().getY(), IPAddressSender,
						object.findString("requestJoin").getString()));
				System.out.println(object.findString("requestJoin").getString() + " joined the game!");
			}

			return;
		}

		Player senderPlayer = level.getPlayer(IPAddressSender);

		if(object.findField("xVel") != null && object.findField("yVel") != null)
		{
			float xVelocity = SerializationReader.readFloat(object.findField("xVel").getData(), 0);
			float yVelocity = SerializationReader.readFloat(object.findField("yVel").getData(), 0);

			//TODO: Anti cheat and stuff

			if(xVelocity <= senderPlayer.getSpeed() && yVelocity <= senderPlayer.getSpeed()) senderPlayer.motion(xVelocity, yVelocity);
			else System.out.println("Anti cheat detected illegal movement: " + xVelocity + " " + yVelocity);

			if(object.findField("prDir") != null)
			{
				float angle = SerializationReader.readFloat(object.findField("prDir").getData(), 0);
				level.add(new ProjectileBullet(senderPlayer.getX(), senderPlayer.getY(), angle, senderPlayer, null));
			}
		}

		if(projectile != null)
		{
			level.add(new ProjectileBullet(level.getClientPlayer().getX(), level.getClientPlayer().getY(), projectile.getDirection(),
					level.getClientPlayer(), null));
			projectile = null;
		}
	}

	private boolean validPacket(SObject object)
	{
		if(object == null)
		{
			System.out.println("Failed to deserialize!");
			return false;
		}

		if(object.getName().equals("Debug") && object.findField("version") != null)
		{
			if(SerializationReader.readShort(object.findField("version").getData(), 0) != VERSION)
			{
				System.out.println("Invalid packet!");
				return false;
			}
			return true;
		}

		System.out.println("Invalid packet!");
		return false;
	}
}
