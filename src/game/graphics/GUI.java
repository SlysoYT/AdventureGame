package game.graphics;

import game.Game;

public abstract class GUI
{
	protected Sprite sprite;

	public GUI(Sprite sprite)
	{
		this.sprite = sprite;
	}
	
	protected final int getXOffset()
	{
		return Game.width / 2 - sprite.getWidth() / 2;
	}
	
	protected final int getYOffset()
	{
		return Game.height / 2 - sprite.getHeight() / 2;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
}
