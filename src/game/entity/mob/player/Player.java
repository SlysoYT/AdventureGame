package game.entity.mob.player;

import java.util.UUID;

import game.Game;
import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.Mob;
import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityRage;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.ability.AbilityTeleporting;
import game.entity.projectile.Projectiles;
import game.entity.spawner.ParticleSpawner;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.input.Keyboard;
import game.input.Mouse;
import game.input.TextInput;
import game.level.tile.Tile;
import game.util.Hitbox;

public class Player extends Mob
{
	private Keyboard input;
	private boolean isClient;
	private String IPAddress;

	private int anim = 0;
	private int walkParticleTicks = 0;

	private boolean typingMessage = false;
	private String chatLine = "";

	public boolean respawning = false;

	private final int RESPAWN_COOLDOWN = 180;
	private int currentRespawnCooldown = RESPAWN_COOLDOWN;

	//Abilities
	private Ability primaryAbility;
	private Ability secondaryAbility;
	private Ability passiveAbility;

	public Player(int x, int y, Keyboard input)
	{
		isClient = true;

		hitbox = new Hitbox(-5, -8, 9, 15);
		initMob(x, y, hitbox, Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10);
		this.input = input;

		//primaryAbility = new AbilityShooting(this, Projectiles.ProjectileBoomerang, 100);
		primaryAbility = new AbilityShooting(this, Projectiles.ProjectileBoomerang, 70);
		secondaryAbility = new AbilityTeleporting(this, 360, 200);
		passiveAbility = new AbilityRage(this, 600, 240);
	}

	public Player(int x, int y, String IPAddress)
	{
		isClient = false;
		this.IPAddress = IPAddress;

		hitbox = new Hitbox(-5, -8, 9, 15);
		initMob(x, y, hitbox, Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10);
	}

	public Player(int x, int y, UUID uuid)
	{
		isClient = false;
		setUUID(uuid);

		hitbox = new Hitbox(-5, -8, 9, 15);
		initMob(x, y, hitbox, Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10);
	}

	public void tick()
	{
		tickMob();

		if(respawning) respawn();

		if(isMoving())
		{
			if(walkParticleTicks % 5 == 0)
				new ParticleSpawner(x + (rand.nextInt(6) - 3), y + (4 + rand.nextInt(4)), 0.2F, 0.05F, 30, 1, level, Sprite.PARTICLE_QUARTZ);
			walkParticleTicks++;
			anim++;
		}
		else anim = 0;

		if(!isClient) return;

		if(input.enterToggle && !typingMessage)
		{
			typingMessage = true;
			TextInput.clearTextInput();
			return;
		}
		if(typingMessage)
		{
			chatLine = TextInput.getTextInput();
			Chat.typingMessage(chatLine);

			if(input.enterToggle)
			{
				if(!chatLine.isEmpty()) Chat.addMessage(new Message(chatLine, this.getClass().getSimpleName().toString()));
				chatLine = "";
				typingMessage = false;
			}
		}
		else Chat.typingMessage(null);

		if(this.isDead()) return;

		if(this.isOnDeadly())
		{
			this.kill();
		}
		else if(this.isOnCheckpoint())
		{
			level.finishedLevel();
		}

		if(!typingMessage)
		{
			if(!(input.left && input.right))
			{
				if(input.left && !(isOnIce())) this.motion(-getSpeed(), 0F);
				if(input.right && !(isOnIce())) this.motion(getSpeed(), 0F);
			}
			if(!(input.up && input.down))
			{
				if(input.up && !(isOnIce())) this.motion(0F, -getSpeed());
				if(input.down && !(isOnIce())) this.motion(0F, getSpeed());
			}
		}

		primaryAbility.tick();
		secondaryAbility.tick();
		passiveAbility.tick();

		if(Mouse.getButton() == 1) primaryAbility.enable();
		if(Mouse.getButton() == 3) secondaryAbility.enable();
		if(Mouse.getButton() == 2) passiveAbility.enable();
	}

	public void render(Screen screen)
	{
		if(this.isDead()) return;

		getWalkingSprite();

		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}

	protected void getWalkingSprite()
	{
		if(dir == 0)
		{
			sprite = Sprite.PLAYER_UP[0];
			if(isMoving())
			{
				if(anim % 34 < 11)
				{
					sprite = Sprite.PLAYER_UP[1];
				}
				else if(anim % 34 < 17)
				{
					sprite = Sprite.PLAYER_UP[0];
				}
				else if(anim % 34 < 26)
				{
					sprite = Sprite.PLAYER_UP[2];
				}
				else
				{
					sprite = Sprite.PLAYER_UP[0];
				}
			}
		}

		if(dir == 1)
		{
			sprite = Sprite.PLAYER_RIGHT[0];
			if(isMoving())
			{
				if(anim % 32 < 8)
				{
					sprite = Sprite.PLAYER_RIGHT[1];
				}
				else if(anim % 32 < 16)
				{
					sprite = Sprite.PLAYER_RIGHT[0];
				}
				else if(anim % 32 < 24)
				{
					sprite = Sprite.PLAYER_RIGHT[2];
				}
				else
				{
					sprite = Sprite.PLAYER_RIGHT[0];
				}
			}
		}

		if(dir == 2)
		{
			sprite = Sprite.PLAYER_DOWN[0];
			if(isMoving())
			{
				if(anim % 34 < 11)
				{
					sprite = Sprite.PLAYER_DOWN[1];
				}
				else if(anim % 34 < 17)
				{
					sprite = Sprite.PLAYER_DOWN[0];
				}
				else if(anim % 34 < 26)
				{
					sprite = Sprite.PLAYER_DOWN[2];
				}
				else
				{
					sprite = Sprite.PLAYER_DOWN[0];
				}
			}
		}

		if(dir == 3)
		{
			sprite = Sprite.PLAYER_LEFT[0];
			if(isMoving())
			{
				if(anim % 32 < 8)
				{
					sprite = Sprite.PLAYER_LEFT[1];
				}
				else if(anim % 32 < 16)
				{
					sprite = Sprite.PLAYER_LEFT[0];
				}
				else if(anim % 32 < 24)
				{
					sprite = Sprite.PLAYER_LEFT[2];
				}
				else
				{
					sprite = Sprite.PLAYER_LEFT[0];
				}
			}
		}
	}

	public void whileDead()
	{
		respawning = true;
	}

	public void respawn()
	{
		if(currentRespawnCooldown > 0)
		{
			currentRespawnCooldown--;
			return;
		}
		respawning = false;
		currentRespawnCooldown = RESPAWN_COOLDOWN;
		x = Game.getLevel().getSpawnLocation().getX();
		y = Game.getLevel().getSpawnLocation().getY();
		heal();
	}

	//Getters

	public Ability getPrimaryAbility()
	{
		return primaryAbility;
	}

	public float getSecondaryAbilityCooldownProgress()
	{
		return (float) secondaryAbility.getCurrentCooldown() / secondaryAbility.getCooldown();
	}

	public float getPassiveAbilityCooldownProgress()
	{
		return (float) passiveAbility.getCurrentCooldown() / passiveAbility.getCooldown();
	}

	public int getCurrentRespawnCooldown()
	{
		return currentRespawnCooldown;
	}

	public int getRespawnCooldown()
	{
		return RESPAWN_COOLDOWN;
	}

	public String getIPAddress()
	{
		return IPAddress;
	}
}
