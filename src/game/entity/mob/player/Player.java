package game.entity.mob.player;

import java.util.UUID;

import game.Game;
import game.chat.Chat;
import game.chat.Message;
import game.entity.mob.Mob;
import game.entity.mob.ability.Ability;
import game.entity.mob.ability.AbilityRage;
import game.entity.mob.ability.AbilityShooting;
import game.entity.mob.ability.AbilityTrap;
import game.entity.mob.effect.EffectMovementSpeed;
import game.entity.projectile.Projectiles;
import game.entity.spawner.ParticleSpawner;
import game.entity.trap.Traps;
import game.graphics.Screen;
import game.graphics.Sprite;
import game.graphics.GUIs.GUIInventory;
import game.graphics.GUIs.GUIInventoryType;
import game.input.Keyboard;
import game.input.Mouse;
import game.input.TextInput;
import game.level.tile.Tile;
import game.util.Hitbox;

public class Player extends Mob
{
	private Keyboard input;
	private GUIInventory inventory;

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
		super(x, y, new Hitbox(-5, -3, 9, 10), Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10, null);

		isClient = true;
		this.input = input;
		this.inventory = new GUIInventory();

		primaryAbility = new AbilityShooting(this, Projectiles.ProjectileBoomerang, 70);
		secondaryAbility = new AbilityTrap(this, Traps.TrapExplosive, 100);
		passiveAbility = new AbilityRage(this, 600, 240);
	}

	public Player(int x, int y, String IPAddress)
	{
		super(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10, null);

		isClient = false;
		this.IPAddress = IPAddress;
	}

	public Player(int x, int y, UUID uuid)
	{
		super(x, y, new Hitbox(-5, -8, 9, 15), Sprite.PLAYER_DOWN[0], 50.0F, 1.0F, 1.2F, 10, uuid);
		isClient = false;
	}

	public void tickMob()
	{
		if(respawning)
		{
			respawn();
			return;
		}

		if(isMoving())
		{
			Sprite[] particleSprites = Sprite.getParticleSpritesFromPosition(this.getX(), this.getY(), 1);
			if(walkParticleTicks % 5 == 0)
				new ParticleSpawner(x + (rand.nextInt(6) - 3), y + (4 + rand.nextInt(4)), 0.2F, 0.05F, 30, level, particleSprites);
			walkParticleTicks++;
			anim++;
		}
		else anim = 0;

		if(!isClient) return;

		primaryAbility.tick();
		secondaryAbility.tick();
		passiveAbility.tick();

		if(inventory.armourEquipped()) this.applyEffect(new EffectMovementSpeed(1, 5, this));

		if(input.inventoryToggle && !typingMessage)
		{
			if(Game.getActiveGUI() instanceof GUIInventory) Game.setActiveGUI(null);
			else
			{
				inventory.setType(GUIInventoryType.PlayerInv);
				Game.setActiveGUI(inventory);
			}
		}
		if(Game.getActiveGUI() != null) return;

		if(Mouse.getButton() == 1) primaryAbility.enable();
		if(Mouse.getButton() == 3) secondaryAbility.enable();
		if(Mouse.getButton() == 2) passiveAbility.enable();

		handleChat();

		if(typingMessage) return;

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

	private void handleChat()
	{
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

			if(input.enterToggle || input.escapeToggle)
			{
				if(!chatLine.isEmpty() && input.enterToggle) Chat.addMessage(new Message(chatLine, this.getClass().getSimpleName().toString()));
				chatLine = "";
				typingMessage = false;
			}
		}
		else Chat.typingMessage(null);
	}

	public void render(Screen screen)
	{
		if(this.isDead()) return;

		getWalkingSprite();
		screen.renderSprite(x - Tile.DEFAULT_TILE_SIZE / 2, y - Tile.DEFAULT_TILE_SIZE / 2, sprite, true);
	}

	protected void getWalkingSprite()
	{
		if(getDirectionFacing() == 0)
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

		if(getDirectionFacing() == 1)
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

		if(getDirectionFacing() == 2)
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

		if(getDirectionFacing() == 3)
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

	public GUIInventory getInventory()
	{
		return inventory;
	}

	public boolean isTypingMessage()
	{
		return typingMessage;
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
