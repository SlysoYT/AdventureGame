package game.entity.mob.player;

import java.util.UUID;

import game.Game;
import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.Mob;
import game.entity.mob.abilities.Ability;
import game.entity.mob.abilities.MovementSpeed;
import game.entity.mob.abilities.Shooting;
import game.entity.projectile.BoomerangProjectile;
import game.entity.projectile.WizardProjectile;
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
		initMob(x, y, hitbox, Sprite.playerDown[0], 50.0F, 1.0F, 1.2F, 10);
		this.input = input;

		primaryAbility = new Shooting(this, new BoomerangProjectile(0, 0, 0, null, null));
		secondaryAbility = new Shooting(this, new WizardProjectile(0, 0, 0, null, null));
		passiveAbility = new MovementSpeed(this, 600, 180, 2.0F);
	}

	public Player(int x, int y, String IPAddress)
	{
		isClient = false;
		this.IPAddress = IPAddress;

		hitbox = new Hitbox(-5, -8, 9, 15);
		initMob(x, y, hitbox, Sprite.playerDown[0], 50.0F, 1.0F, 1.2F, 10);
	}

	public Player(int x, int y, UUID uuid)
	{
		isClient = false;
		setUUID(uuid);

		hitbox = new Hitbox(-5, -8, 9, 15);
		initMob(x, y, hitbox, Sprite.playerDown[0], 50.0F, 1.0F, 1.2F, 10);
	}

	public void tick()
	{
		tickMob();

		if(respawning) respawn();

		if(isMoving())
		{
			if(walkParticleTicks % 5 == 0)
				new ParticleSpawner(x + (random.nextInt(6) - 3), y + (4 + random.nextInt(4)), 0.2F, 0.05F, 30, 1, level, Sprite.particleQuartz);
			walkParticleTicks++;
			anim++;
		}
		else anim = 0;

		if(!isClient) return;

		if(input.enterToggle && !typingMessage)
		{
			typingMessage = true;
			TextInput.clearTextInput();
		}
		if(typingMessage)
		{
			chatLine = TextInput.getTextInput();
			Chat.typingMessage(chatLine);

			if(input.enterToggle && !chatLine.isEmpty())
			{
				Chat.addMessage(new Message(chatLine, this.getClass().getSimpleName().toString()));
				chatLine = "";
				typingMessage = false;
			}
		}

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

		screen.renderMob(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, this);
	}

	protected void getWalkingSprite()
	{
		if(dir == 0)
		{
			sprite = Sprite.playerUp[0];
			if(isMoving())
			{
				if(anim % 34 < 11)
				{
					sprite = Sprite.playerUp[1];
				}
				else if(anim % 34 < 17)
				{
					sprite = Sprite.playerUp[0];
				}
				else if(anim % 34 < 26)
				{
					sprite = Sprite.playerUp[2];
				}
				else
				{
					sprite = Sprite.playerUp[0];
				}
			}
		}

		if(dir == 1)
		{
			sprite = Sprite.playerRight[0];
			if(isMoving())
			{
				if(anim % 25 < 8)
				{
					sprite = Sprite.playerRight[1];
				}
				else if(anim % 25 < 17)
				{
					sprite = Sprite.playerRight[2];
				}
				else
				{
					sprite = Sprite.playerRight[0];
				}
			}
		}

		if(dir == 2)
		{
			sprite = Sprite.playerDown[0];
			if(isMoving())
			{
				if(anim % 34 < 11)
				{
					sprite = Sprite.playerDown[1];
				}
				else if(anim % 34 < 17)
				{
					sprite = Sprite.playerDown[0];
				}
				else if(anim % 34 < 26)
				{
					sprite = Sprite.playerDown[2];
				}
				else
				{
					sprite = Sprite.playerDown[0];
				}
			}
		}

		if(dir == 3)
		{
			sprite = Sprite.playerLeft[0];
			if(isMoving())
			{
				if(anim % 25 < 8)
				{
					sprite = Sprite.playerLeft[1];
				}
				else if(anim % 25 < 17)
				{
					sprite = Sprite.playerLeft[2];
				}
				else
				{
					sprite = Sprite.playerLeft[0];
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

	public float getPrimaryAbilityCooldownProgress()
	{
		return (float) primaryAbility.getCurrentCooldown() / primaryAbility.getCooldown();
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
