package sprites;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class LifeBar {

	private Player p;
	private BufferedImage bar, heart; // images for life bar and heart (lives)
	
	private Font font; // font to write current health
	
	public LifeBar (Player p) {
		// needs player health and life stats
		this.p = p;
		
		try {
			// loads images
			bar = ImageIO.read(getClass().getResourceAsStream("/lifeBar/lifeBar.gif"));
			heart = ImageIO.read(getClass().getResourceAsStream("/lifeBar/heart.gif"));
			
			// sets font
			font = new Font("Magneto", Font.PLAIN, 18);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	

	public void draw(Graphics2D g) {
		// draw lifeBar and hearts
		g.drawImage(bar, 0, 10, null);
		g.drawImage(heart, 180, 16, null);
		
		// set graphics
		g.setFont(font);
		g.setColor(Color.WHITE);
		
		// draw current health / total health
		g.drawString(p.getHP() + "/" + p.getMaxHP(), 35, 35);
		
		// draw number of lives
		g.drawString("X  " + p.getLives(), 220, 35);
	}
	
}