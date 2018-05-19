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
package game.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import game.Game;

public class PlaySound
{
	private static Clip clip;

	public static void playSound(Sound sound)
	{
		try
		{
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(PlaySound.class.getResource(sound.getPath()));
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(inputStream);
			clip.start();
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e)
		{
			Game.getPrinter().printError(e.getMessage());
		}
	}

	public static long getSoundLengthInMicroseconds(Sound sound)
	{
		try
		{
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(PlaySound.class.getResource(sound.getPath()));
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(inputStream);
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e)
		{
			Game.getPrinter().printError(e.getMessage());
		}

		return clip.getMicrosecondLength();
	}

	public static void initAudioSystem()
	{
		//Just open a sound, without playing it
		try
		{
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(PlaySound.class.getResource(Sounds.hit.getPath()));
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(inputStream);
		}
		catch(LineUnavailableException | IOException | UnsupportedAudioFileException e)
		{
			Game.getPrinter().printError(e.getMessage());
		}
	}
}
