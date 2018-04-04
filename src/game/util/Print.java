package game.util;

import game.chat.Chat;
import game.chat.Message;
import game.settings.Settings;

public class Print
{
	public static void printInfo(String message)
	{
		if(!Settings.debugMode) return;
		System.out.println("INFO: " + message);
	}

	public static void printImportantInfo(String message)
	{
		Chat.addMessage(new Message(message, PrintType.Info));
		System.out.println("INFO: " + message);
	}
	
	public static void printWarning(String message)
	{
		if(!Settings.debugMode) return;
		Chat.addMessage(new Message(message, PrintType.Warning));
		System.out.println("WARNING: " + message);
	}

	public static void printError(String message)
	{
		if(!Settings.debugMode) return;
		Chat.addMessage(new Message(message, PrintType.Error));
		System.out.println("ERROR: " + message);
	}
}
