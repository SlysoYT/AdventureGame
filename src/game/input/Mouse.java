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
package game.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.Game;

public class Mouse implements MouseListener, MouseMotionListener
{

	private static int mouseX = -1;
	private static int mouseY = -1;
	private static int mouseButton = -1;
	private static int onClick = -1;

	public static void tick()
	{
		if(onClick >= 0) onClick--;
	}

	public static int getX()
	{
		return mouseX;
	}

	public static int getY()
	{
		return mouseY;
	}

	public static int getLevelPointingX()
	{
		return mouseX / Game.SCALE + Game.getScreen().getXOffset();
	}

	public static int getLevelPointingY()
	{
		return mouseY / Game.SCALE + Game.getScreen().getYOffset();
	}

	public static int getButton()
	{
		return mouseButton;
	}

	public static boolean onClick()
	{
		return onClick != -1;
	}

	public void mouseDragged(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseClicked(MouseEvent e)
	{

	}

	public void mouseEntered(MouseEvent e)
	{

	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mousePressed(MouseEvent e)
	{
		mouseButton = e.getButton();
		onClick = 1;
	}

	public void mouseReleased(MouseEvent e)
	{
		mouseButton = -1;
	}

}
