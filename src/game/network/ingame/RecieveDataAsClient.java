package game.network.ingame;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.Entity;
import game.level.Level;
import game.network.NetworkPackage;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;
import game.network.serialization.SerializationReader;
import game.util.GameState;
import game.util.Print;

public class RecieveDataAsClient
{
	public static void recieve(byte[] data)
	{
		Level level = Game.getLevel();

		SObject object = SObject.deserialize(data, 0);
		if(!NetworkPackage.validPacket(object)) return;

		if(object.findString("kickPlayer") != null)
		{
			Game.setGameState(GameState.TitleScreen);
			Print.printInfo("You got kicked from the server! Reason: " + object.findString("kickPlayer").getString());
			return;
		}

		if(level == null)
		{
			if(object.findField("levelSeed") != null) Game.loadLevel(null, SerializationReader.readLong(object.findField("levelSeed").getData(), 0));
			if(object.findString("yourUUID") != null)
			{
				Game.getLevel().getClientPlayer().setUUID(UUID.fromString(object.findString("yourUUID").getString()));
			}
			return;
		}

		List<SString> UUIDEntities = object.findStrings("UUIDEn");
		List<SField> typeOfEntity = object.findFields("typeEn");
		List<SField> xPosEntity = object.findFields("xPosEn");
		List<SField> yPosEntity = object.findFields("yPosEn");
		List<SField> xVelocityMob = object.findFields("xVelMb");
		List<SField> yVelocityMob = object.findFields("yVelMb");
		List<SString> namePlayer = object.findStrings("namePl");
		
		for(int i = 0; i < UUIDEntities.size(); i++)
		{
			Entity entity = level.getEntity(UUID.fromString(UUIDEntities.get(i).getString()));
			
			if(entity == null)
			{
				System.out.println(typeOfEntity.size());
				System.out.println(xPosEntity.size());
				System.out.println(yPosEntity.size());
				System.out.println(xVelocityMob.size());
				System.out.println(yVelocityMob.size());
				System.out.println(namePlayer.size());
			}
		}
		
		/*//Players
		List<SString> playerUUIDs = object.findStrings("plUUID");
		List<SString> playerNames = object.findStrings("plName");
		List<SField> xPositionPlayers = object.findFields("plXPos");
		List<SField> yPositionPlayers = object.findFields("plYPos");
		List<SField> xVelocityPlayers = object.findFields("plXVel");
		List<SField> yVelocityPlayers = object.findFields("plYVel");

		for(int i = 0; i < playerUUIDs.size(); i++)
		{
			UUID uuid = UUID.fromString(playerUUIDs.get(i).getString());
			Player player = level.getPlayer(uuid);

			int xPos = SerializationReader.readInt(xPositionPlayers.get(i).getData(), 0);
			int yPos = SerializationReader.readInt(yPositionPlayers.get(i).getData(), 0);
			float xVelocity = SerializationReader.readFloat(xVelocityPlayers.get(i).getData(), 0);
			float yVelocity = SerializationReader.readFloat(yVelocityPlayers.get(i).getData(), 0);

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
				Player source = level.getPlayer(UUID.fromString(projectileSources.get(i).getString()));

				if(type == 0) level.add(new ProjectileBoomerang(xPos, yPos, angle, source, uuid));
				else if(type == 1) level.add(new ProjectileBullet(xPos, yPos, angle, source, uuid));
				else level.add(new ProjectileGuardian(xPos, yPos, angle, source, uuid));

			}
			//Tick projectile
			else
			{
				projectile.setPosition(xPos, yPos);
			}
		}*/
	}
}
