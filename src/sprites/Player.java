package sprites;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import mainclasses.Panel;

import sprites.Bullet;

import sprites.Animation;
import graphics.Map;

public class Player extends AbstractSpriteSet {
	
	private int maxHP, HP, lives; // health stats
	
	// images of player
	private ArrayList<BufferedImage[]> playerEntities;
	// number of frames for each player action animation
	private final int[] numFrames = { 4, 4, 7 };
	
	// sets flashing and invincible for short time after being hit
	private boolean isStun; // should flash or not
	private long stunTimer; // duration

	private int gunSpeed; // how fast the gun can shoot bullets
	private boolean isShoot; // booleans of player's actions
	private int bulletDamage; // damage stat
	private double bulletSpeed;
	private ArrayList<Bullet> bullet; // list of all bullets shot by player

	private boolean dying; // boolean of whether player still needs to go through dying animation
	private boolean dead; // player is dead and finished dying animation
	
	// integer values for each specific action that player does
	private static final int IDLE = 0;
	private static final int SHOOT = 1;
	private static final int DIE = 2;
	
	Clip clipShoot;
	Clip clipHealth;
	Clip clipCrash;
	private HashMap<String, Clip> sfx;
	
	public Player(Map map) {
		super(map);
		
		// initializes playing stats
		maxHP = 100;
		HP = maxHP;
//		lives = 3;
		bulletSpeed = 8.8;
		bulletDamage = 15;

		bulletDamage = 150;

		gunSpeed = 100;
		
		// initializes movement and collision stats
		cwidth = 30;
		cheight = 30;
		moveSpeed = 0.8;
		maxSpeed = 5.6;
		stopSpeed = 0.8;
		direction = true;
		width = 50;
		height = 50;

		// initialize list of bullets
		bullet = new ArrayList<Bullet>();
		
		try {

			// read spritesheet and set each picture as a subimage
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/entities/playersprites.gif"));

			playerEntities = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < numFrames.length; i++) {

				BufferedImage[] image = new BufferedImage[numFrames[i]];
				
				for (int j = 0; j < numFrames[i]; j++) {
					image[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}

				playerEntities.add(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// default animation
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(playerEntities.get(IDLE));
		animation.setDelay(50);
		
		try {
			// sound effects
			AudioInputStream ais1 = AudioSystem.getAudioInputStream(getClass()
					.getResource("/music/bulletshot.wav"));
			clipShoot = AudioSystem.getClip();
			clipShoot.open(ais1);
			AudioInputStream ais2 = AudioSystem.getAudioInputStream(getClass()
					.getResource("/music/Triumph_Health_Sound.wav"));
			clipHealth = AudioSystem.getClip();
			clipHealth.open(ais2);
			AudioInputStream ais3 = AudioSystem.getAudioInputStream(getClass()
					.getResource("/music/Crash_Sound.wav"));
			clipCrash = AudioSystem.getClip();
			clipCrash.open(ais3);
		} catch (Exception e) {
			e.printStackTrace();
		}

		sfx = new HashMap<String, Clip>();
		sfx.put("shoot", clipShoot);
		sfx.put("health", clipHealth);
		sfx.put("crash", clipCrash);
	}
	
	// setter for throwing - used for user input
	public void setShooting(boolean b) {
		isShoot = b;
	}
	
	public void moving() {
		
		// no moving as jet is crashing
		if (dying) {
			dx = dy = 0;
			return;
		}
		
		// dx and dy is rate at which player speeds up in that direction - maxes at maxSpeed
		// horizontal movement
		if (isLeft) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (isRight) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		
		// vertical movement
		if (isUp) {
			dy -= moveSpeed;
			if (dy < -maxSpeed) {
				dy = -maxSpeed;
			}
		} else if (isDown) {
			dy += moveSpeed;
			if (dy > maxSpeed) {
				dy = maxSpeed;
			}
		}
		
		// stopping horizontal movement
		if (!isLeft && !isRight) {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}

		// stopping vertical movement
		if (!isUp && !isDown) {
			if (dy > 0) {
				dy -= stopSpeed;
				if (dy < 0) {
					dy = 0;
				}
			} else if (dy < 0) {
				dy += stopSpeed;
				if (dy > 0) {
					dy = 0;
				}
			}
		}
		
		// Mouse control
//		x = MouseInfo.getPointerInfo().getLocation().x - 320;
//		y = MouseInfo.getPointerInfo().getLocation().y - 130;
		
		// borders of screen bound
		if (y > Panel.HEIGHT - 20) {
			y = Panel.HEIGHT - 20;
		}
		
		if (y < 20) {
			y = 20;
		}
		
		if (x > Panel.WIDTH - 20) {
			x = Panel.WIDTH - 20;
		}
		
		if (x < 5) {
			x = 5;
		}

	}
	
	public void checkHit(ArrayList<EnemyJet> viruses) {

		// loop through enemies
		for (int i = 0; i < viruses.size(); i++) {

			EnemyJet v = viruses.get(i);
			
			// check player shooting
			for (int j = 0; j < bullet.size(); j++) {
				if (bullet.get(j).intersects(v)) {
					v.hit(bulletDamage);
					bullet.get(j).setHit();
					break;
				}
			}
			
			// check virus blob attack
			if (intersects(v)) {
				hit(v.getDamage());
			}
			for (int j = 0; j < v.getBullet().size(); j++) {
				if (intersects(v.getBullet().get(j))) {
					hit(v.getDamage());
					v.getBullet().get(j).setHit();
					break;
				}
			}

		}
	}
	
	// adds life if player found and touches hidden life
	public void checkGetLife(ArrayList<NewLife> nl) {
		for (int i = 0; i < nl.size(); i++) {
			if (intersects(nl.get(i))) {
				sfx.get("health").start();
				nl.remove(i);
				lives++;
			}
		}
	}
	
	public void hit(int damage) {
		
		// stun invincible period
		if (isStun) {
			return;
		}

		// lose health
		HP -= damage;
		if (HP < 0) {
			HP = 0;
		}
		
		// dead
		if (HP == 0) {
			dying = true;
		}
		isStun = true;

		// time which player first gets stunned - used for duration of invincible stun
		stunTimer = System.nanoTime();
	}
	
	// set animation of current player action and duration before pose switches
	public void setPose(int pose, int delay) {
		currentAction = pose;
		animation.setFrames(playerEntities.get(pose));
		animation.setDelay(delay);
	}
	
	// reset player health at the expense of losing player life
	public void setNewLife() {
		HP = maxHP;
		dead = false;
		direction = true;
		lives--;
	}
	
	// getters for other classes
	public int getHP() {
		return HP;
	}
	
	public int getMaxHP() {
		return maxHP;
	}
	
	public int getLives() {
		return lives;
	}
	
	public void setLives(int l) {
		lives = l;
	}
	
	public boolean getDeath() {
		return dead;
	}
	
	public void update() {
		
		// animation of dying happens once before restarting
		if (currentAction == DIE) {
			sfx.get("crash").start();
			if (animation.hasPlayedOnce()) {
				dying = false;
				dead = true;
			}
			isShoot = false;
		} else {
			isShoot = true;
		}

		// only shoots 1 bullet per animation of shooting
		if (currentAction == SHOOT) {
			if (animation.hasPlayedOnce()) {
				isShoot = false;
			}
		}
		
		// add bullet if player is shooting
		if (isShoot) {
			if (currentAction != SHOOT) {
				sfx.get("shoot").start();
				sfx.get("shoot").loop(Clip.LOOP_CONTINUOUSLY);
				Bullet b = new Bullet(map, 0, -bulletSpeed, "/entities/bullet.gif");
				b.setPosition(x, y);
				bullet.add(b);
			}
		}
		
		// remove bullet if it hits something
		for (int i = 0; i < bullet.size(); i++) {
			bullet.get(i).update();
			if (bullet.get(i).shouldRemove()) {
				bullet.remove(i);
				i--;
			}
		}
		
		// calls methods dealing with player movement and collision
		moving();
		checkCollision();
		setPosition(x, y);
		
		// sets each animation pose

		if (dying) {
			if (currentAction != DIE) {
				setPose(DIE, 250);
			}
		} else {
			if (isShoot) {
				if (currentAction != SHOOT) {
					setPose(SHOOT, gunSpeed);
				}
			} else {
				if (currentAction != IDLE) {
					setPose(IDLE, 50);
				}
			}
		}

		// sets invincible period
		if (isStun) {
			long elapsed = (System.nanoTime() - stunTimer) / 1000000;
			if (elapsed > 800) {
				isStun = false;
			}
		}
		
		// updates animation
		animation.update();
		
	}
	
	public void draw(Graphics2D g) {
		
		// draws player in map layout
		setMapPosition();
		
		// draws bullets
		for (int i = 0; i < bullet.size(); i++) {
			bullet.get(i).draw(g);
		}
		
		// draws flashing when player is stunned and alive
		if (isStun && !dying) {
			long elapsed = (System.nanoTime() - stunTimer) / 1000000;

			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}

		super.draw(g, HP, maxHP, width);
	}
	
}
