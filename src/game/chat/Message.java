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

	public String getSender()
	{
		return sender;
	}

	public String getMessage()
	{
		return message;
	}
}
