package game.entity.spawner;

import game.entity.particle.Particle;
import game.graphics.Sprite;
import game.level.Level;

public class ParticleSpawner extends Spawner
{
	public ParticleSpawner(int x, int y, float xSpeed, float ySpeed, int life, int amount, Level level, Sprite[] sprite)
	{
		super(x, y, Type.PARTICLE, amount, level);
		
		for(int i = 0; i < amount; i++)
		{
			level.add(new Particle(x, y, xSpeed, ySpeed, life, sprite));
		}
	}
}
