package game.entity;

import game.graphics.Screen;
import game.graphics.Sprite;

public class DamageValue extends Entity
{
	private float damageValue;
	private float xVelocity = 1 - rand.nextInt(3);
	private float yVelocity = -2.5F;

	public DamageValue(float damage, int x, int y)
	{
		this.damageValue = damage;
		this.x = x;
		this.y = y;
	}

	public void tick()
	{
		x += xVelocity;
		y += yVelocity;
		yVelocity += 0.2F;

		if(yVelocity > 3.5F) this.remove();
	}

	public void render(Screen screen)
	{
		Sprite.writeValues(String.valueOf((int) (damageValue)), screen, x - Screen.getXOffset(), y - Screen.getYOffset(), 0x000000);
	}
}
