package sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mainclasses.Panel;

import sprites.Animation;

import graphics.Map;

// 2nd enemy, faster but more damaging, found in level 4
public class BlueJet extends EnemyJet {

	private boolean canShoot; // if jet should shoot
	private int delay; // delay after each bullet shot
	private long startStunTime; // start stun time
	private BufferedImage[] sprites; // array of sprites
	
	// constructor initializes variables
	public BlueJet(Map map, double dx, double dy, long spawnTime, int spawnLocation) {
		super(map);
		
		// set stats - low health, high damage
		maxHP = 30;
		HP = maxHP;
		damage = 50;

		// set size and collision size
		width = 50;
		height = 50;
		cheight = 35;
		cwidth = 35;
		
		// set moving directions
		this.dx = dx;
		this.dy = dy;
		
		// set spawning directions
		this.spawnLocation = spawnLocation;
		this.spawnTime = spawnTime;
		
		stun = false; // default stun
		canShoot = false; // default canThrow

		bullet = new ArrayList<Bullet>(); // create list of bullets to shoot
		startStunTime = System.nanoTime(); // begin time to throw every "delay" miliseconds
		delay = 2500; // delay in throwing pattern
		
		try {
			// load images
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/entities/blueJet.gif"));

			sprites = new BufferedImage[1];

			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// animation
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(200);
	}
	
	public void update(double px, double py) {
		
		// set dead
		if (HP == 0) {
			dead = true;
		}
		
		// bounces off left and right sides of screen
		if (x > Panel.WIDTH - width || x < width) {
			dx *= -1;
		}
		
		// makes sure jet bounces off top 
		if (y < height) {
			dy *= -1;
		}
		
		// disappears if jet reaches bottom
		if (y > Panel.HEIGHT - height) {
			leftScreen = true;
		}
		
		// checks jet position to bounces off walls and if it collides with player
		checkCollision();
		// sets the jet's initial position given from level classes
		setPosition(x, y);
		
		// sets intervals to shoot
		long throwTime = (System.nanoTime() - startStunTime) / 1000000;
		if (throwTime > delay) {
			canShoot = true;
			startStunTime = System.nanoTime();
		}
		
		// shoots if jet can shoot
		if (canShoot) {
			Bullet d = new Bullet(map, 0, 3.8, "/entities/bullet.gif");
			d.setPosition(x, y);
			bullet.add(d);
			canShoot = false;
			animation.setDelay(100);
		}
		
		// stun time
		if (stun) {
			long stunTime = (System.nanoTime() - stunTimer) / 1000000;
			if (stunTime > 400) {
				stun = false;
			}
		}
		
		// remove bullet if it hit something and should be removed
		for (int i = 0; i < bullet.size(); i++) {
			bullet.get(i).update();
			if (bullet.get(i).shouldRemove()) {
				bullet.remove(i);
				i--;
			}
		}
		
		//update animation
		animation.update();
	}
	
	public void draw(Graphics2D g) {

		// place in map
		setMapPosition();

		// draw bullets
		for (int i = 0; i < bullet.size(); i++) {
			bullet.get(i).draw(g);
		}
		
		// draws flashing when blue jet is stunned
		if (stun) {
			long elapsed = (System.nanoTime() - stunTimer) / 1000000;

			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}
		super.draw(g, HP, maxHP, width);
	}

}
