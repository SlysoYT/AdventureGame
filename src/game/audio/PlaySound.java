package game.audio;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import game.util.Print;

public class PlaySound
{
	private static Clip clip;

	public static void playSound(Sound sound)
	{
		try
		{
			PlaySound.clip = AudioSystem.getClip();
			PlaySound.clip.open(AudioSystem.getAudioInputStream(PlaySound.class.getResource(sound.getPath())));
			PlaySound.clip.start();
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e)
		{
			Print.printError(e.getMessage());
		}
	}

	public static void initAudioSystem()
	{
		try
		{
			PlaySound.clip = AudioSystem.getClip();
			PlaySound.clip.open(AudioSystem.getAudioInputStream(PlaySound.class.getResource(Sounds.hit.getPath())));
		}
		catch(LineUnavailableException | IOException | UnsupportedAudioFileException e)
		{
			Print.printError(e.getMessage());
		}
	}
}
