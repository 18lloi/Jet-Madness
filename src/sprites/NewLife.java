package sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import sprites.Animation;

import graphics.Map;

public class NewLife extends AbstractSpriteSet {

	// needs to be array because animation needs image array input
	private BufferedImage[] heart = new BufferedImage[1];
	
	public NewLife(Map map) {
		super(map);
		
		// sets size and collision size
		width = 50;
		height = 50;
		cheight = 30;
		cwidth = 30;

		try {
			// read picture
			heart[0] = ImageIO.read(getClass().getResourceAsStream("/lifeBar/heart.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// animation set
		animation = new Animation();
		animation.setFrames(heart);

	}
	
	// draws and calls super class draw method
	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g);
	}

}
