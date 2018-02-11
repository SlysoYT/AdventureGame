package game.entity.mob;

import game.entity.mob.player.Player;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;

public class Slime extends Mob
{
	private float jumpHeight = 3.5F;
	private float jumpHeightOffset = 0.0F;
	private float yVelocity = jumpHeight;
	private float gravity = 0.25F;

	private boolean shouldIncreaseY = false;
	private boolean shouldDecreaseY = false;
	private byte delay = 0;

	private int xOffsetRandom = rand.nextInt(40) - 20;

	public Slime(int x, int y, float speed)
	{
		initMob(x, y, new Hitbox(-7, -1, 13, 7), Sprite.slimeDown, 20.0F, speed, 10.0F, 30);
	}

	public void tick()
	{
		tickMob();

		//Movement
		if(delay > 0)
		{
			delay--;
			return;
		}

		Player player = level.getClientPlayer();
		int distanceToPlayer = (int) Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2));

		float xChange = 0, yChange = 0;

		int xGoal;
		int yGoal = player.getY();

		if(distanceToPlayer > 50) xGoal = player.getX() + xOffsetRandom;
		else xGoal = player.getX();

		if(yGoal > y) shouldDecreaseY = true;
		else shouldDecreaseY = false;
		if(yGoal < y) shouldIncreaseY = true;
		else shouldIncreaseY = false;

		if(shouldIncreaseY) jumpHeightOffset = -1.5F;
		else if(shouldDecreaseY) jumpHeightOffset = 1.0F;
		else jumpHeightOffset = 0.0F;

		yChange -= yVelocity;
		if(yVelocity > -(jumpHeight + jumpHeightOffset + (rand.nextFloat() - 0.5F))) yVelocity -= gravity;
		else
		{
			resetJump();
			delay = (byte) (5 + rand.nextInt(40));
			level.add(new ParticleSpawner((int) (x), (int) (y + 11), 0.01F, 0.01F, 30, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner((int) (x + 4), (int) (y + 11), 0.5F, 0.15F, 30, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner((int) (x - 4), (int) (y + 11), 0.5F, 0.15F, 30, 3, level, Sprite.PARTICLE_SLIME));
		}
		if(xGoal < x) xChange -= getSpeed();
		else if(xGoal > x) xChange += getSpeed();
		this.motion(xChange, yChange);

		//Attacking
		Player collidedPlayer = level.playerCollidedWithMob(this);
		if(collidedPlayer != null)
		{
			this.attack(collidedPlayer);
		}
	}

	public void render(Screen screen)
	{
		sprite = Sprite.slimeDown;

		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}

	private void resetJump()
	{
		yVelocity = jumpHeight;
	}
}
