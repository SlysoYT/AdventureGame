package game.entity.spawner;

import game.entity.Entity;
import game.level.Level;
import game.util.TileCoordinate;

public class Spawner extends Entity
{
	public enum Type
	{
		PARTICLE, MOB;
	}
	
	@SuppressWarnings("unused")
	private Type type;
	
	public Spawner(TileCoordinate tileCoordinate, Type type, Level level)
	{
		init(level);
		this.x = tileCoordinate.getX();
		this.y = tileCoordinate.getY();
		this.type = type;
	}
	
	public Spawner(int x, int y, Type type, Level level)
	{
		init(level);
		this.x = x;
		this.y = y;
		this.type = type;
	}
}
