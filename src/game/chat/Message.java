package game.chat;

import game.util.PrintType;

public class Message
{
	private String message;
	private String sender;
	private int displayedTime = 0;

	public Message(String message, String sender)
	{
		this.message = message;
		this.sender = sender;
	}

	public Message(String message, PrintType type)
	{
		this.message = message;
		this.sender = type.toString();
	}

	public void tick()
	{
		displayedTime++;
	}

	public boolean visible()
	{
		return displayedTime < 300;
	}

	public String getChatMessage()
	{
		return "<" + this.sender + "> " + this.message;
	}

	public String getMessage()
	{
		return message;
	}
}
