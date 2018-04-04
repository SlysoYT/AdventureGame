package game.entity.mob;

import game.Game;
import game.entity.item.ItemArmourLeather;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.GUIs.GUIInventoryType;
import game.input.Mouse;
import game.util.Hitbox;

public class Salesman extends Mob
{
	public Salesman(int x, int y)
	{
		super(x, y, new Hitbox(2, 1, 11, 14), Sprite.SALESMAN, 20.0F, 0, 0, 0);
	}

	@Override
	protected void tickMob()
	{
		if(Mouse.getLevelPointingX() >= x + hitbox.getXOffset() && Mouse.getLevelPointingX() <= x + hitbox.getXOffset() + hitbox.getWidth()
				&& Mouse.getLevelPointingY() >= y + hitbox.getYOffset() && Mouse.getLevelPointingY() <= y + hitbox.getYOffset() + hitbox.getHeight())
		{
			if(Mouse.getButton() == 1 && Mouse.onClick())
			{
				Game.getLevel().getClientPlayer().getInventory().resetShopOffers();
				Game.getLevel().getClientPlayer().getInventory().addShopOffer(5, new ItemArmourLeather(0, 0));
				Game.getLevel().getClientPlayer().getInventory().setType(GUIInventoryType.Shop);
				Game.setActiveGUI(Game.getLevel().getClientPlayer().getInventory());
			}
		}
	}

	@Override
	public void render(Screen screen)
	{
		screen.renderSprite(x, y, sprite, true);
	}
}
