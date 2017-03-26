package sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import mainclasses.Panel;

import graphics.Map;;

public class Bullet extends AbstractSpriteSet {

	private boolean isHit; // has hit something
	private boolean isRemove; // has hit and should be removed
	private BufferedImage[] sprites; // image of bullet while shooting
	private BufferedImage[] hitSprites; // image of bullet explosion after hitting something

	
	public Bullet(Map tm, double speedx, double speedy, String pic) {

		super(tm);

		// direction and movement
		dx = speedx;
		dy = speedy;

		// size and collision size
		width = 30;
		height = 30;
		cwidth = 8;
		cheight = 27;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(pic));

			sprites = new BufferedImage[1];
			
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}

			hitSprites = new BufferedImage[4];
			
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet.getSubimage(i * width, height, width, height);
			}

			// animation
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// changes bullet from shooting to exploding after hitting a barrier
	public void setHit() {
		
		// only hit once
		if (isHit) {
			return;
		}

		// set isHit to true;
		isHit = true;
		
		// animation change to hitSprites
		animation.setFrames(hitSprites);
		animation.setDelay(50);
		dx = dy = 0;
	}

	// returns whether the bullet has exploded and should be removed
	public boolean shouldRemove() {
		return isRemove;
	}

	public void update() {

		// collision and movement
		checkCollision();
		setPosition(x, y);

		// if collide, set isHit to true;
		if (dy == 0 && dx == 0 && !isHit) {
			setHit();
		}
		
		// disappear if out of screen
		if (y < 0 || y > Panel.HEIGHT - 30 && dy > 0 || x < 0 || x > Panel.WIDTH - 30 && dx > 0) {
			setHit();
		}
		
		

		// update animation, remove after hitSprites plays once
		animation.update();
		if (isHit && animation.hasPlayedOnce()) {
			isRemove = true;
		}
	}

	public void draw(Graphics2D g) {

		setMapPosition();

		super.draw(g);
	}

}