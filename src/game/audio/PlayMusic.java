package game.audio;

public class PlayMusic
{
	private static final Sound musicChiptune1 = new Sound("/audio/music/Chiptune1.wav");

	private static boolean playingMusic = false;
	private static long musicPlayingSince;
	private static long soundLengthInMicroSeconds;

	public static void tick()
	{
		if(playingMusic)
		{
			if(System.nanoTime() / 1000 >= musicPlayingSince + soundLengthInMicroSeconds) playingMusic = false;
			return;
		}

		startMusic();
	}

	private static void startMusic()
	{
		PlaySound.playSound(musicChiptune1);
		musicPlayingSince = System.nanoTime() / 1000;
		soundLengthInMicroSeconds = PlaySound.getSoundLengthInMicroseconds(musicChiptune1);
		playingMusic = true;
	}
}
