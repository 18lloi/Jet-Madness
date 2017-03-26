package sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mainclasses.Panel;

import graphics.Tile;
import graphics.Map;

// template for sprites
public abstract class AbstractSpriteSet {

	// tile stuff
	public Map map;
	public int blockSize;
	public double xmap;
	public double ymap;

	// position and vector
	public double x;
	public double y;
	public double dx;
	public double dy;

	// dimensions
	public int width;
	public int height;

	// collision box
	public int cwidth;
	public int cheight;

	// collision
	public int currentx;
	public int currenty;
	public double willbex; // accounts for moving speed x
	public double willbey; // accounts for mobing speed y
	public boolean isTopLeft;
	public boolean isTopRight;
	public boolean isBottomLeft;
	public boolean isBottomRight;

	// animation
	public Animation animation;
	public int currentAction;
	public boolean direction;

	// movement
	public boolean isLeft;
	public boolean isRight;
	public boolean isUp;
	public boolean isDown;

	// movement attributes
	public double moveSpeed;
	public double maxSpeed;
	public double stopSpeed;
	
	public Player player;

	// constructor
	public AbstractSpriteSet(Map map) {
		this.map = map;
		blockSize = map.getSize();
	}
	
	// see if two objects collide
	public boolean intersects(AbstractSpriteSet o) {
		Rectangle r1 = this.getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}

	// getter for rectange of sprite
	public Rectangle getRectangle() {
		return new Rectangle((int) x - cwidth / 2, (int) y - cheight / 2, cwidth, cheight);
	}
	
	// getters for coordinates relative to map
	public double getxMap() {
		return xmap;
	}
	
	public double getyMap() {
		return ymap;
	}

	// calculate if direction the player is going in causes a collision with collision coordinates (cwidth and cheight)
	public void calculateBlockCollision(double x, double y) {
		int leftTile = (int) (x - cwidth / 2) / blockSize;
		int rightTile = (int) (x + cwidth / 2 - 1) / blockSize;
		int topTile = (int) (y - cheight / 2) / blockSize;
		int bottomTile = (int) (y + cheight / 2 - 1) / blockSize;

		int tl = map.getType(topTile, leftTile);
		int tr = map.getType(topTile, rightTile);
		int bl = map.getType(bottomTile, leftTile);
		int br = map.getType(bottomTile, rightTile);

		isTopLeft = tl == Tile.NONPASSABLE;
		isTopRight = tr == Tile.NONPASSABLE;
		isBottomLeft = bl == Tile.NONPASSABLE;
		isBottomRight = br == Tile.NONPASSABLE;
	}

	// complex method - checks every side and corner for collision
	// checks for future collisions to stop "player stuck in wall" glitch
	// accounts for moving
	public void checkCollision() {

		currenty = (int) x / blockSize;
		currentx = (int) y / blockSize;

		willbex = x + dx;
		willbey = y + dy;

		calculateBlockCollision(x, willbey);
		
		if (dy < 0) {
			if (isTopLeft || isTopRight) {
				dy = 0;
				y = currentx * blockSize + cheight / 2;
			} else {
				y += dy;
			}
		}
		
		if (dy > 0) {
			if (isBottomLeft || isBottomRight) {
				dy = 0;
				y = currentx * blockSize + cheight / 2;
			} else {
				y += dy;
			}
		}

		calculateBlockCollision(willbex, y);

		if (dx < 0) {
			if (isTopLeft || isBottomLeft) {
				dx = 0;
				x = currenty * blockSize + cwidth / 2;
			} else {
				x += dx;
			}
		}

		if (dx > 0) {
			if (isTopRight || isBottomRight) {
				dx = 0;
				x = (currenty + 1) * blockSize - cwidth / 2;
			} else {
				x += dx;
			}
		}

	}

	// getters for coordinates, size, and collision size
	public int getx() {
		return (int) x;
	}

	public int gety() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCWidth() {
		return cwidth;
	}

	public int getCHeight() {
		return cheight;
	}

	// setters for position, map position, and movements
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setMapPosition() {
		xmap = map.getX();
		ymap = map.getY();
	}

	public void setLeft(boolean b) {
		isLeft = b;
	}

	public void setRight(boolean b) {
		isRight = b;
	}

	public void setUp(boolean b) {
		isUp = b;
	}

	public void setDown(boolean b) {
		isDown = b;
	}

	// see if object is or isn't on scree - doesn't have to run if not on screen
	public boolean notOnScreen() {
		return x + xmap + width < 0 || x + xmap - width > Panel.WIDTH || y + ymap + height < 0 || y + ymap - height > Panel.HEIGHT;
	}
	
	public void draw(Graphics2D g) {
		
		// draw animation with direction
		if (direction) {
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), null);
		} else {
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2 + width), (int) (y + ymap - height / 2), -width, height, null);
		}
		
	}

	public void draw(Graphics2D g, int objectHP, int objectMaxHP, int width) {
		
		// draw health bar
		int fullBar = (int) Math.round((double) objectHP / (double) objectMaxHP * (width - 5));
		g.setColor(new Color(127, 0, 0));
		g.fillRect((int) (x + xmap - width / 2), (int) (y + ymap - height / 2 - 2), width - 5, 2);
		g.setColor(Color.RED);
		g.fillRect((int) (x + xmap - width / 2), (int) (y + ymap - height / 2 - 2), fullBar, 2);
		g.setColor(Color.BLACK);
		g.drawRect((int) (x + xmap - width / 2), (int) (y + ymap - height / 2 - 2), width - 5, 2);
		
		// draw animation with direction
		if (direction) {
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), null);
		} else {
			g.drawImage(animation.getImage(), (int) (x + xmap - width / 2 + width), (int) (y + ymap - height / 2), -width, height, null);
		}
		
	}

}
