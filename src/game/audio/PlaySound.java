package game.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound
{
	private static Clip clip;

	public static void playSound(Sound sound)
	{
		File file = sound.getFile();

		try
		{
			PlaySound.clip = AudioSystem.getClip();
			PlaySound.clip.open(AudioSystem.getAudioInputStream(file));
			PlaySound.clip.start();
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void initAudioSystem()
	{
		try
		{
			PlaySound.clip = AudioSystem.getClip();
			PlaySound.clip.open(AudioSystem.getAudioInputStream(Sounds.hit.getFile()));
		}
		catch(LineUnavailableException | IOException | UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
	}
}
