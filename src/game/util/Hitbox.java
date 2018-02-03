package game.util;

public class Hitbox
{
	private int xOffset, yOffset;
	private int width, height;
	
	/**
	 * Defines the hitbox of a mob.
	 */
	public Hitbox(int xOffset, int yOffset, int width, int height)
	{
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
	}
	
	public int getXOffset()
	{
		return xOffset;
	}
	
	public int getYOffset()
	{
		return yOffset;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
