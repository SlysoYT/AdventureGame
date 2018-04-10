package game.network.ingame;

import game.Game;
import game.network.serialization.SField;
import game.network.serialization.SObject;
import game.network.serialization.SString;

public class GetSendDataAsClient
{
	private static boolean disconnect = false;

	public static byte[] getData(SObject object)
	{
		if(disconnect)
		{
			object.addField(SField.Boolean("disconnect", true));
		}
		else
		{
			if(Game.getLevel() == null)
			{
				SString joinPlayer = SString.String("requestJoin", "player123"); //TODO: Custom name
				object.addString(joinPlayer);
				object.addField(SField.Boolean("loadedLevel", false));
			}
			else
			{
				object.addField(SField.Boolean("loadedLevel", true));

				object.addField(SField.Float("xVel", Game.getLevel().getClientPlayer().getXVelocity()));
				object.addField(SField.Float("yVel", Game.getLevel().getClientPlayer().getYVelocity()));

				object = AbilityOnline.tick(object);
			}
		}

		byte[] data = new byte[object.getSize()];
		object.getBytes(data, 0);

		return data;
	}

	public static void disconnect()
	{
		disconnect = true;
	}
}
