package game.entity;

import java.util.Random;
import java.util.UUID;

import game.graphics.Screen;
import game.level.Level;

public abstract class Entity
{
	private boolean removed = false;
	private UUID uuid = UUID.randomUUID();

	protected int x, y;
	protected Level level;
	protected final Random rand = new Random();

	public void tick()
	{
	}

	public void render(Screen screen)
	{
	}

	//Remove from level
	public void remove()
	{
		removed = true;
	}

	public boolean isRemoved()
	{
		return removed;
	}

	public void init(Level level)
	{
		this.level = level;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		if(uuid == null) return;
		this.uuid = uuid;
	}
}
