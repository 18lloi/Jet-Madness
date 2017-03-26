package graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import mainclasses.Panel;

public class Background {

	private BufferedImage image; // Image

	// background coordinates
	private double x;
	private double y;
	
	// change in background coordinates (if background is moving)
	private double dx;
	private double dy;

	private double moveScale; // how much bg moves

	public Background(String s, double ms) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
			moveScale = ms;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// sets background's curent position
	public void setPosition(double x, double y) {
		this.x = (x * moveScale) % Panel.WIDTH;
//		this.y = (y * moveScale) % Panel.HEIGHT;
	}

	// sets direction in which background is moving
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	// updates background's curent position
	public void update() {
		x += dx;
		y += dy;
	}

	// draws updated image
	public void draw(Graphics2D g) {
		g.drawImage(image, (int) x, (int) y, null);
		if (x < 0) {
			g.drawImage(image, (int) x + Panel.WIDTH, (int) y, null);
			x += Panel.WIDTH; 
		} else if (x > 0) {
			g.drawImage(image, (int) x - Panel.WIDTH, (int) y, null);
			x -= Panel.WIDTH;
		}
		if (y < 0) {
			g.drawImage(image, (int) x, (int) y + Panel.HEIGHT, null);
			y += Panel.HEIGHT; 
		} else if (y > 0) {
			g.drawImage(image, (int) x, (int) y - Panel.HEIGHT, null);
			y -= Panel.HEIGHT;
		}
	}

}
