package graphics;

import java.awt.image.BufferedImage;

// each individual tile that is put together by the Map class
public class Tile {

	private BufferedImage image;
	private int type;

	// tile types
	public static final int PASSABLE = 0;
	public static final int NONPASSABLE = 1;

	public Tile(BufferedImage image, int type) {
		this.image = image;
		this.type = type;
	}

	// geters
	public BufferedImage getImage() {
		return image;
	}

	public int getType() {
		return type;
	}
}
