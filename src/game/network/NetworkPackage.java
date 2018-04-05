package game.network;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileBoomerang;
import game.entity.projectile.ProjectileBullet;
import game.entity.projectile.ProjectileGuardian;
import game.level.Level;
import game.network.ingame.AbilityOnline;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;
import game.network.serialization.SerializationReader;
import game.util.GameState;
import game.util.Print;

public class NetworkPackage
{
	public static final short VERSION = 201;

	private boolean isClient;

	public NetworkPackage(boolean isClient)
	{
		this.isClient = isClient;
	}

	public byte[] getSendData(String targetIPAddress)
	{
		Level level = Game.getLevel();

		SObject object = new SObject("Debug");
		SField version = SField.Short("version", VERSION);
		object.addField(version);

		if(isClient)
		{
			if(level == null)
			{
				SString joinPlayer = SString.String("requestJoin", "player123"); //TODO: Custom name and request join only in the beginning
				object.addString(joinPlayer);
			}
			else
			{
				SField xVelocity = SField.Float("xVel", level.getClientPlayer().getXVelocity());
				SField yVelocity = SField.Float("yVel", level.getClientPlayer().getYVelocity());

				object.addField(xVelocity);
				object.addField(yVelocity);

				object = AbilityOnline.tick(object);
			}

			byte[] data = new byte[object.getSize()];
			object.getBytes(data, 0);

			return data;
		}
		else
		{
			if(Server.isClientBanned(targetIPAddress) || !Server.isClientOnline(targetIPAddress))
			{
				SString kickPlayer;

				if(Server.isClientBanned(targetIPAddress)) kickPlayer = SString.String("kickPlayer", "you are banned from this server");
				else kickPlayer = SString.String("kickPlayer", "you got kicked from this server");

				object.addString(kickPlayer);

				byte[] data = new byte[object.getSize()];
				object.getBytes(data, 0);

				return data;
			}

			if(level != null)
			{
				object.addField(SField.Long("levelSeed", Game.getLevel().getGameLevel().getSeed()));

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

					if(player == level.getPlayerByIP(targetIPAddress)) isYourClientPlayer = 1;

					object.addString(SString.String("plUUID", player.getUUID().toString()));
					object.addString(SString.String("plName", playerName));
					object.addField(SField.Integer("plXPos", player.getX()));
					object.addField(SField.Integer("plYPos", player.getY()));
					object.addField(SField.Float("plXVel", player.getXVelocity()));
					object.addField(SField.Float("plYVel", player.getYVelocity()));
					object.addField(SField.Integer("plCl", isYourClientPlayer));
				}

				//Tick all projectiles
				List<Projectile> projectiles = level.getProjectiles();
				for(Projectile projectile : projectiles)
				{
					object.addString(SString.String("prUUID", projectile.getUUID().toString()));
					object.addField(SField.Integer("prXPos", projectile.getX()));
					object.addField(SField.Integer("prYPos", projectile.getY()));
					object.addField(SField.Float("prDir", (float) projectile.getDirection()));
					object.addField(SField.Integer("prType", projectile.getProjectileType().ordinal()));
					object.addString(SString.String("prSrcUUID", projectile.getSource().getUUID().toString()));
				}
			}

			byte[] data = new byte[object.getSize()];
			object.getBytes(data, 0);

			return data;
		}
	}

	public void recieveDataAsClient(byte[] data)
	{
		Level level = Game.getLevel();

		SObject object = SObject.deserialize(data, 0);
		if(!validPacket(object)) return;

		if(object.findString("kickPlayer") != null)
		{
			Game.setGameState(GameState.TitleScreen);
			Print.printInfo("You got kicked from the server! Reason: " + object.findString("kickPlayer").getString());
			return;
		}

		if(level == null)
		{
			if(object.findField("levelSeed") != null) Game.loadLevel(null, SerializationReader.readLong(object.findField("levelSeed").getData(), 0));
			return;
		}

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
		List<SField> projectileTypes = object.findFields("prType");
		List<SString> projectileSources = object.findStrings("prSrcUUID");

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
				int type = SerializationReader.readInt(projectileTypes.get(i).getData(), 0);
				Player source = level.getPlayer(UUID.fromString(projectileSources.get(i).getString())); //TODO: Entity with according UUID on client not found

				if(type == 0) level.add(new ProjectileBoomerang(xPos, yPos, angle, source, uuid));
				else if(type == 1) level.add(new ProjectileBullet(xPos, yPos, angle, source, uuid));
				else level.add(new ProjectileGuardian(xPos, yPos, angle, source, uuid));

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
		Level level = Game.getLevel();

		if(level == null) return;
		if(Server.isClientBanned(IPAddressSender)) return;

		SObject object = SObject.deserialize(data, 0);
		if(!validPacket(object)) return;

		if(!Server.isClientOnline(IPAddressSender))
		{
			if(object.findString("requestJoin") != null)
			{
				Server.addClient(IPAddressSender);

				level.add(new OnlinePlayer(level.getSpawnLocation().getX(), level.getSpawnLocation().getY(), IPAddressSender,
						object.findString("requestJoin").getString()));
				Print.printImportantInfo(object.findString("requestJoin").getString() + " joined the game!");
			}

			return;
		}

		Player senderPlayer = level.getPlayerByIP(IPAddressSender);

		if(object.findField("xVel") != null && object.findField("yVel") != null)
		{
			float xVelocity = SerializationReader.readFloat(object.findField("xVel").getData(), 0);
			float yVelocity = SerializationReader.readFloat(object.findField("yVel").getData(), 0);

			//TODO: Anti cheat and stuff

			if(Math.abs(xVelocity) <= senderPlayer.getSpeed() && Math.abs(yVelocity) <= senderPlayer.getSpeed())
				senderPlayer.motion(xVelocity, yVelocity);
			else Print.printInfo("Anti cheat detected illegal movement: " + xVelocity + " " + yVelocity);

			AbilityOnline.recieveAsHost(IPAddressSender, object);
		}
	}

	private boolean validPacket(SObject object)
	{
		if(object == null)
		{
			Print.printError("Failed to deserialize!");
			return false;
		}

		if(object.getName().equals("Debug") && object.findField("version") != null)
		{
			if(SerializationReader.readShort(object.findField("version").getData(), 0) == VERSION) return true;

			Print.printError("Client version not matching with server version!");
			return false;
		}

		Print.printError("Invalid packet!");
		return false;
	}
}
