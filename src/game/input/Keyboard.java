package game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener
{
	private boolean[] keys = new boolean[600];
	private boolean[] keysToggle = new boolean[600];
	private int[] keysToggleTicks = new int[600];

	public boolean up, down, left, right, enter, escape, shift, backspace;
	public boolean enterToggle;

	public void tick()
	{
		for(int i = 0; i < keysToggleTicks.length; i++)
		{
			if(keysToggleTicks[i] > 0)
			{
				keysToggle[i] = false;
				keysToggleTicks[i] = 0;
			}
			else if(keysToggleTicks[i] == 0) keysToggleTicks[i]++;
		}

		up = keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
		enter = keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_ENTER];
		escape = keys[KeyEvent.VK_ESCAPE];
		shift = keys[KeyEvent.VK_SHIFT];
		backspace = keys[KeyEvent.VK_BACK_SPACE];

		enterToggle = keysToggle[KeyEvent.VK_ENTER];
	}

	public void keyPressed(KeyEvent e)
	{
		keys[e.getKeyCode()] = true;
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) TextInput.backspace();
	}

	public void keyReleased(KeyEvent e)
	{
		keys[e.getKeyCode()] = false;
		keysToggle[e.getKeyCode()] = true;
		keysToggleTicks[e.getKeyCode()] = 0;
	}

	public void keyTyped(KeyEvent e)
	{
		TextInput.addText(e.getKeyChar());
	}

}
