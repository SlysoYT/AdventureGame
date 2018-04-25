package game.entity.spawner;

import game.entity.particle.Particle;
import game.graphics.Sprite;
import game.level.Level;

public class ParticleSpawner extends Spawner
{
	/**
	 * Spawn random sprites from the sprites array.
	 * 
	 * @param x
	 * @param y
	 * @param xSpeed
	 * @param ySpeed
	 * @param life
	 * @param amount
	 * @param level
	 * @param sprites
	 */
	public ParticleSpawner(int x, int y, float xSpeed, float ySpeed, int life, int amount, Level level, Sprite[] sprites)
	{
		super(x, y, Type.PARTICLE, level);

		for(int i = 0; i < amount; i++)
		{
			level.add(new Particle(x, y, xSpeed, ySpeed, life, sprites[rand.nextInt(sprites.length)]));
		}
	}

	/**
	 * Spawn all sprites specified in sprites.
	 * 
	 * @param x
	 * @param y
	 * @param xSpeed
	 * @param ySpeed
	 * @param life
	 * @param level
	 * @param sprites
	 */
	public ParticleSpawner(int x, int y, float xSpeed, float ySpeed, int life, Level level, Sprite[] sprites)
	{
		super(x, y, Type.PARTICLE, level);

		for(int i = 0; i < sprites.length; i++)
		{
			level.add(new Particle(x, y, xSpeed, ySpeed, life, sprites[i]));
		}
	}
}
