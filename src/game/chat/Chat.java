package game.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.chat.commands.Command;
import game.chat.commands.CommandHelp;
import game.chat.commands.CommandKill;
import game.chat.commands.CommandTeleport;
import game.input.Keyboard;
import game.level.Level;
import game.util.GameState;

public class Chat
{
	private static List<Message> messages = new ArrayList<Message>();
	private static List<Command> commands = new ArrayList<Command>();
	private static Font font = new Font("Verdana", Font.BOLD, 18);

	private static String inputField = "";

	public Chat(Level level)
	{
		commands.add(new CommandHelp());
		commands.add(new CommandKill(level));
		commands.add(new CommandTeleport());
	}

	public static void addMessage(Message message)
	{
		//TODO: Send to server or if server recieve messages and send to all clients
		messages.add(message);
		if(message.getSender().equals("Server")) return;
		if(Game.getGameState() == GameState.IngameOffline) handleCommands(message);
	}

	private static void handleCommands(Message message)
	{
		if(!message.getMessage().startsWith("!")) return;

		int counter = 0;
		String string = message.getMessage().substring(1, message.getMessage().length());
		String command = "";
		List<String> args = new ArrayList<String>();

		for(int i = 0; i < string.length() + 1; i++)
		{
			if(i >= string.length())
			{
				if(counter == 0) command = string.substring(0, i).toLowerCase();
				else args.add(string.substring(0, i).toLowerCase());
				break;
			}
			if(string.charAt(i) == ' ')
			{
				if(counter == 0) command = string.substring(0, i).toLowerCase();
				else args.add(string.substring(0, i).toLowerCase());
				string = string.substring(i + 1, string.length());
				counter++;
				i = 0;
			}
		}

		for(Command cmd : commands)
		{
			if(cmd.getName().equals(command))
			{
				cmd.enableCommand(args, Game.getLevel().getClientPlayer());
				return;
			}
		}

		addMessage(new Message("Unknown command. Type !help to see a list of commands.", "Server"));
	}

	public static void tick(Keyboard key)
	{
		//If more than 100 messages, delete old ones
		while(messages.size() > 100)
			messages.remove(0);

		for(int i = 0; i < messages.size(); i++)
			messages.get(i).tick();
	}

	public static void render(Graphics g, boolean playerIsChatting)
	{
		if(!playerIsChatting) renderVisibleChatMessages(g);
		else renderNewestChatMessages(g);
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

		renderMessages(g, visibleMessages);
	}

	private static void renderNewestChatMessages(Graphics g)
	{
		List<Message> visibleMessages = new ArrayList<Message>();

		for(int i = messages.size() - 1; i >= 0; i--)
		{
			visibleMessages.add(messages.get(i));
			if(visibleMessages.size() > 15) break;
		}

		renderMessages(g, visibleMessages);
	}

	private static void renderMessages(Graphics g, List<Message> messages)
	{
		int y = Game.height * Game.SCALE - font.getSize() - 5;
		g.setFont(font);
		g.setColor(Color.GREEN);

		for(int i = 0; i < messages.size(); i++)
		{
			g.drawString(messages.get(i).getChatMessage(), 0, y);
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

	public static List<Command> getCommands()
	{
		return commands;
	}
}
