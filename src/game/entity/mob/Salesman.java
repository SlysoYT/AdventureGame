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
package game.entity.mob;

import java.util.UUID;

import game.Game;
import game.entity.item.ItemAbilityRage;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.GUIs.GUIInventoryType;
import game.input.Mouse;
import game.util.Hitbox;

public class Salesman extends Mob
{
	public Salesman(int x, int y, UUID uuid)
	{
		super(x, y, new Hitbox(2, 1, 11, 14), Sprite.SALESMAN, 20.0F, 0, 0, 0, uuid);
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
				Game.getLevel().getClientPlayer().getInventory().addShopOffer(20, new ItemAbilityRage(0, 0));
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
