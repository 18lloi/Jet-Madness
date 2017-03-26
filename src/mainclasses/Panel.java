package mainclasses;

import gameRun.LevelManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;

	// dimensions
	public static final int WIDTH = 720;
	public static final int HEIGHT = 480;
	public static final int SCALE = 1;

	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	// image
	private BufferedImage image;
	private Graphics2D g;

	// level manager
	private LevelManager lm;

	// constructor
	public Panel() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}

	// create and start thread
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}

	// initialize running and graphics and levels
	private void init() {

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();

		running = true;

		lm = new LevelManager();
		
	}

	// create continuous running
	public void run() {

		init();

		long start;
		long elapsed;
		long wait;

		// game loop
		while (running) {

			start = System.nanoTime();

			update();
			draw();
			refreshDrawScreen();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;
			if (wait < 0) {
				wait = 5;
			}

			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// continuous running loop - every non-graphical update goes here
	private void update() {
		lm.update();
	}

	// graphical update
	private void draw() {
		lm.draw(g);
	}

	// creates drawable frame
	private void refreshDrawScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}

	// not used
	public void keyTyped(KeyEvent key) {
		
	}

	// user input
	public void keyPressed(KeyEvent key) {
		lm.keyPressed(key.getKeyCode());
	}

	// stops user input
	public void keyReleased(KeyEvent key) {
		lm.keyReleased(key.getKeyCode());
	}

}
