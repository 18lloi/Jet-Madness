package graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import mainclasses.Panel;

public class Map {

	// position of map
	private double x, y;
	
	private int map[][]; // double array of world
	private int blockSize; // each block is blockSize x blockSize pixels
	private int rows, columns; // rows and columns on the game
	private int mapRows, mapColumns; //  rows and columns taken from reading picture

	private int pictureWidth;
	private int fixColumn, fixRow; // fixes last row and column
	private int xmin, xmax, ymin, ymax; // bounds to draw - limits excess drawing

	// picture image and each individual tile
	private BufferedImage tilespriteSheet;
	private Tile[][] tiles;

	public Map(int bs) {
		blockSize = bs;
		rows = Panel.HEIGHT / bs + 1;
		columns = Panel.WIDTH / bs + 1;
	}
	
	// getters
	public int getSize() {
		return blockSize;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	// returns type - whether play can or cannot pass through a certain tile
	public int getType(int row, int column) {
		int rc = map[row][column];

		int r = rc / pictureWidth;
		int c = rc % pictureWidth;
		return tiles[r][c].getType();
	}
	
	// pleaces object on the given coordinates
	public void setMapPosition(double xMap, double yMap) {
		// centers map on given coordinates - usually player
		x += (xMap - x);
		y += (yMap - y);

		// does not center on object if object is at corner or wall
		if (x < xmin) {
			x = xmin;
		}
		if (y < ymin) {
			y = ymin;
		}
		if (x > xmax) {
			x = xmax;
		}
		if (y > ymax) {
			y = ymax;
		}
		
		// accounts for 1 block not rendering glitch
		fixColumn = (int) -x / blockSize;
		fixRow = (int) -y / blockSize;
	}
	
	// reads tilesheet by splitting it into several subimages of size blockSize - assigns integer valeus to each block
	public void loadTiles(String s) {
		try {
			tilespriteSheet = ImageIO.read(getClass().getResourceAsStream(s));
			pictureWidth = tilespriteSheet.getWidth() / blockSize;
			tiles = new Tile[2][pictureWidth];

			BufferedImage subimage;
			// first row in picture is passable, second row is blocked ie walls
			for (int i = 0; i < pictureWidth; i++) {
				subimage = tilespriteSheet.getSubimage(i * blockSize, 0, blockSize, blockSize);
				tiles[0][i] = new Tile(subimage, Tile.PASSABLE);
				subimage = tilespriteSheet.getSubimage(i * blockSize, blockSize, blockSize, blockSize);
				tiles[1][i] = new Tile(subimage, Tile.NONPASSABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// read map file and generates world from that - each interger is assigned tile number which is used for graphics
	// first number is number of rows, second number is number of columns
	// the rest of the numbers are separated by spaces and represent what each row and column is - 0 is nothing
	public void loadMap(String s) {

		try {

			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			mapColumns = Integer.parseInt(br.readLine());
			mapRows = Integer.parseInt(br.readLine());
			map = new int[mapRows][mapColumns];

			xmin = Panel.WIDTH - mapColumns * blockSize;
			xmax = 0;
			ymin = Panel.HEIGHT - mapRows * blockSize;
			ymax = 0;

			for (int row = 0; row < mapRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split("\\s+");
				for (int col = 0; col < mapColumns; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g) {

		// draws map and accounts for possible glitches
		for (int i = fixRow; i < rows + fixRow; i++) {
			if (i >= mapRows) {
				break;
			}

			for (int j = fixColumn; j < columns + fixColumn; j++) {
				if (j >= mapColumns) {
					break;
				}

				if (map[i][j] == 0) {
					continue;
				}

				// coordinates taken from map to display which image from picture
				int px = map[i][j] / pictureWidth;
				int py = map[i][j] % pictureWidth;

				g.drawImage(tiles[px][py].getImage(), (int) x + j * blockSize, (int) y + i * blockSize, null);
			}
		}
	}

}
