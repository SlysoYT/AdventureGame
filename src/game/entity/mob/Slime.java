package game.entity.mob;

import java.util.List;
import java.util.UUID;

import game.Game;
import game.entity.mob.player.Player;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.level.tile.Tile;
import game.util.Hitbox;
import game.util.Node;
import game.util.Vector2i;

public class Slime extends Mob
{
	private float xChange = 0, yChange = 0;
	private int xGoal, yGoal;

	private float jumpHeight = 3.5F;
	private float jumpHeightOffset = 0.0F;
	private float yVelocity = jumpHeight;
	private float gravity = 0.17F + rand.nextFloat() / 6;

	private boolean shouldIncreaseY = false;
	private boolean shouldDecreaseY = false;
	private byte jumpDelay = 0;

	private int xOffsetRandom = rand.nextInt(40) - 20;

	private List<Node> path = null;

	public Slime(int x, int y, UUID uuid)
	{
		super(x, y, new Hitbox(-7, -1, 13, 7), Sprite.SLIME_DOWN, 20.0F, 0.75F, 10.0F, 30, uuid);
		xGoal = x;
		yGoal = y;
	}

	public void tickMob()
	{
		xChange = 0;
		yChange = 0;

		Player target = level.getNearestPlayer(this);

		if(target != null)
		{
			yGoal = target.getY();

			int distanceToPlayer = (int) Math.sqrt(Math.pow(target.getX() - this.getX(), 2) + Math.pow(target.getY() - this.getY(), 2));

			/*
			 * if(distanceToPlayer > 50) xGoal = target.getX() + xOffsetRandom;
			 * else xGoal = target.getX();
			 */

			if(Game.getGameStateTicksPassed() % 20 == rand.nextInt(20)) //Randomness to not make all slimes tick at the same time
			{
				Vector2i start = new Vector2i(this.getX() >> Screen.TILE_SIZE_SHIFTING, this.getY() >> Screen.TILE_SIZE_SHIFTING);
				Vector2i end = new Vector2i(target.getX() >> Screen.TILE_SIZE_SHIFTING, target.getY() >> Screen.TILE_SIZE_SHIFTING);
				path = level.findPath(start, end);
				if(path == null) return;
				if(path.size() > 0)
				{
					Vector2i pathVector = path.get(path.size() - 1).tile;
					xGoal = (pathVector.getX() << Screen.TILE_SIZE_SHIFTING) + this.getHitbox().getXOffset() + this.getHitbox().getWidth() + 1;
					yGoal = (pathVector.getY() << Screen.TILE_SIZE_SHIFTING) + this.getHitbox().getYOffset() + this.getHitbox().getHeight() + 1;
				}
			}
		}
		else
		{
			if(Math.sqrt(Math.pow(xGoal - this.getX(), 2) + Math.pow(yGoal - this.getY(), 2)) < 10)
			{
				int xPos = 0, yPos = 0;
				while(true)
				{
					xPos = rand.nextInt(Tile.DEFAULT_TILE_SIZE * Game.getLevel().getLevelWidth());
					yPos = rand.nextInt(Tile.DEFAULT_TILE_SIZE * Game.getLevel().getLevelHeight());
					if(!Game.getLevel().hitboxCollidesWithSolidTile(xPos, yPos, hitbox)) break;
				}
				xGoal = xPos;
				yGoal = yPos;
			}
		}

		if(jumpDelay > 0)
		{
			jumpDelay--;
			return;
		}

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
			jumpDelay = (byte) (5 + rand.nextInt(40));
			level.add(new ParticleSpawner((int) (x), (int) (y + 15), 0.01F, 0.01F, 20, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner((int) (x + 4), (int) (y + 15), 0.5F, 0.15F, 20, 3, level, Sprite.PARTICLE_SLIME));
			level.add(new ParticleSpawner((int) (x - 4), (int) (y + 15), 0.5F, 0.15F, 20, 3, level, Sprite.PARTICLE_SLIME));
		}
		if(xGoal <= x) xChange -= getSpeed();
		else xChange += getSpeed();
		this.motion(xChange, yChange);

		//Attacking
		List<Player> collidedPlayers = level.playersCollidedWithHitbox(x, y, hitbox);
		if(!collidedPlayers.isEmpty())
		{
			this.attack(collidedPlayers.get(0));
		}
	}

	public void render(Screen screen)
	{
		sprite = Sprite.SLIME_DOWN;
		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}

	private void resetJump()
	{
		yVelocity = jumpHeight;
	}
}
