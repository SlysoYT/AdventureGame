package game.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.Game;
import game.graphics.Screen;

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
		return mouseX / Game.SCALE + Screen.getXOffset();
	}

	public static int getLevelPointingY()
	{
		return mouseY / Game.SCALE + Screen.getYOffset();
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
