package game.util;

public class Timer
{
	private static long start;
	private static long now;
	private static float passedTime;

	public static void start()
	{
		start = System.currentTimeMillis();
	}

	public static float getPassedTime(boolean freezeTimer)
	{
		if(start == -1) return -1;
		if(freezeTimer)
		{
			if(now == -1)
			{
				now = System.currentTimeMillis();
				passedTime = now - start;
			}
			return passedTime / 1000;
		}
		now = System.currentTimeMillis();
		passedTime = now - start;
		return passedTime / 1000;
	}

	public static void reset()
	{
		start = -1;
		now = -1;
	}
}
