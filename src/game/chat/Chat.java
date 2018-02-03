package game.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.input.Keyboard;

public class Chat
{
	private static List<Message> messages = new ArrayList<Message>();
	private static Font font = new Font("Verdana", Font.BOLD, 18);

	private static String inputField = "";

	public static void addMessage(Message message)
	{
		messages.add(message);
	}

	public static void tick(Keyboard key)
	{
		//If more than 200 messages, delete old ones
		while(messages.size() > 200)
			messages.remove(0);

		for(int i = 0; i < messages.size(); i++)
			messages.get(i).tick();
	}

	public static void render(Graphics g)
	{
		renderVisibleChatMessages(g);
		renderInputField(g);
	}

	private static void renderVisibleChatMessages(Graphics g)
	{
		List<Message> visibleMessages = new ArrayList<Message>();

		for(int i = messages.size() - 1; i >= 0; i--)
		{
			if(!messages.get(i).visible()) break;
			visibleMessages.add(messages.get(i));
		}

		int y = Game.height * Game.SCALE - font.getSize() - 5;
		g.setFont(font);
		g.setColor(Color.GREEN);

		for(int i = 0; i < visibleMessages.size(); i++)
		{
			g.drawString(visibleMessages.get(i).getChatMessage(), 0, y);
			y -= font.getSize();
			if(y < Game.height * Game.SCALE / 2) break; //-> The chat won't fill the whole screen
		}
	}

	private static void renderInputField(Graphics g)
	{
		if(inputField == null) return;

		g.setFont(font);
		g.setColor(Color.GREEN);
		g.drawString(inputField, 0, Game.height * Game.SCALE - 5);

		//Blinking cursor
		if(Game.getGameStateTicksPassed() % 20 >= 10)
		{
			int x = g.getFontMetrics().stringWidth(inputField);
			int y = Game.height * Game.SCALE - 5;
			g.fillRect(x + 1, y, font.getSize() - 1, 3);
		}

		inputField = null;
	}

	public static void typingMessage(String message)
	{
		inputField = message;
	}
}
