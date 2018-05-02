package game.entity.lighting;

import java.util.UUID;

import game.entity.Entity;

public class LightSource extends Entity
{
	private int radius;
	private int lifeTime = 0, maxLifeTime = -1;

	public LightSource(int x, int y, int radius, UUID uuid)
	{
		setUUID(uuid);
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	public LightSource(int x, int y, int radius, int lifeTime, UUID uuid)
	{
		setUUID(uuid);
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.maxLifeTime = lifeTime;
	}

	public void tick()
	{
		if(maxLifeTime == -1) return;

		if(lifeTime++ >= maxLifeTime) this.remove();
	}

	public int getRadius()
	{
		return radius;
	}

	public boolean affectsPixelAt(int xPixel, int yPixel)
	{
		return Math.sqrt((xPixel - this.getX()) * (xPixel - this.getX()) + (yPixel - this.getY()) * (yPixel - this.getY())) <= radius;
	}

	public float getGammaAtPixel(int x, int y)
	{
		return (float) (1F / ((Math.sqrt((x - this.getX()) * (x - this.getX()) + (y - this.getY()) * (y - this.getY()))) / radius));
	}
}
