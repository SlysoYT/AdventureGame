/*******************************************************************************
 * Copyright (C) 2018 Thomas Zahner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package game.network.ingame;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.Entity;
import game.entity.mob.Guardian;
import game.entity.mob.Mob;
import game.entity.mob.Salesman;
import game.entity.mob.Slime;
import game.entity.mob.player.OnlinePlayer;
import game.entity.mob.player.Player;
import game.entity.projectile.ProjectileBoomerang;
import game.entity.projectile.ProjectileBullet;
import game.entity.projectile.ProjectileGuardian;
import game.level.Level;
import game.network.NetworkPackage;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;
import game.network.serialization.SerializationReader;
import game.util.GameState;

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
			Game.getPrinter().printInfo("You got kicked from the server! Reason: " + object.findString("kickPlayer").getString());
			return;
		}

		if(level == null)
		{
			if(object.findField("levelSeed") != null) Game.loadLevel(null, SerializationReader.readLong(object.findField("levelSeed").getData(), 0));
			if(object.findString("yourUUID") != null)
				Game.getLevel().getClientPlayer().setUUID(UUID.fromString(object.findString("yourUUID").getString()));
			return;
		}

		List<SString> UUIDEntities = object.findStrings("UUIDEn");
		List<SString> typeOfEntities = object.findStrings("typeEn");
		List<SField> xPosEntities = object.findFields("xPosEn");
		List<SField> yPosEntities = object.findFields("yPosEn");
		List<SField> xVelocityMobs = object.findFields("xVelMb");
		List<SField> yVelocityMobs = object.findFields("yVelMb");
		List<SString> namePlayers = object.findStrings("namePl");

		for(int i = 0; i < UUIDEntities.size(); i++)
		{
			UUID uuid = UUID.fromString(UUIDEntities.get(i).getString());
			String typeOfEntity = typeOfEntities.get(i).getString();

			int xPosEntity = SerializationReader.readInt(xPosEntities.get(i).getData(), 0);
			int yPosEntity = SerializationReader.readInt(yPosEntities.get(i).getData(), 0);

			Entity entity = level.getEntity(uuid);

			if(entity == null)
			{
				if(typeOfEntity.equals(OnlinePlayer.class.getSimpleName()))
					level.add(new OnlinePlayer(xPosEntity, yPosEntity, uuid, namePlayers.get(i).getString()));
				else if(typeOfEntity.equals(Player.class.getSimpleName()))
					level.add(new OnlinePlayer(xPosEntity, yPosEntity, uuid, namePlayers.get(i).getString()));
				else if(typeOfEntity.equals(Salesman.class.getSimpleName())) level.add(new Salesman(xPosEntity, yPosEntity, uuid));
				else if(typeOfEntity.equals(Guardian.class.getSimpleName())) level.add(new Guardian(xPosEntity, yPosEntity, uuid));
				else if(typeOfEntity.equals(Slime.class.getSimpleName())) level.add(new Slime(xPosEntity, yPosEntity, uuid));

				//Projectiles
				if(typeOfEntity.equals(ProjectileBoomerang.class.getSimpleName()) || typeOfEntity.equals(ProjectileGuardian.class.getSimpleName())
						|| typeOfEntity.equals(ProjectileBullet.class.getSimpleName()))
				{
					double dir = SerializationReader.readFloat(object.findField("prDir").getData(), 0);
					Mob source = (Mob) level.getEntity(UUID.fromString(object.findString("prSrcUUID").getString()));

					if(typeOfEntity.equals(ProjectileBoomerang.class.getSimpleName()))
					{
						level.add(new ProjectileBoomerang(xPosEntity, yPosEntity, dir, source, uuid));
					}
					else if(typeOfEntity.equals(ProjectileBullet.class.getSimpleName()))
					{
						level.add(new ProjectileBullet(xPosEntity, yPosEntity, dir, source, uuid));
					}
					else if(typeOfEntity.equals(ProjectileGuardian.class.getSimpleName()))
					{
						level.add(new ProjectileGuardian(xPosEntity, yPosEntity, dir, source, uuid));
					}
				}
				return;
			}

			entity.setPosition(xPosEntity, yPosEntity);
			if(entity instanceof Mob)
			{
				((Mob) entity).motion(SerializationReader.readFloat(xVelocityMobs.get(i).getData(), 0),
						SerializationReader.readFloat(yVelocityMobs.get(i).getData(), 0));
			}
		}
	}
}
