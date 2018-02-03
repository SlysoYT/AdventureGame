package game.audio;

import java.io.File;

public class Sound
{
	File file;

	public Sound(String path)
	{
		this.file = new File(path);
	}

	public File getFile()
	{
		return file;
	}
}
