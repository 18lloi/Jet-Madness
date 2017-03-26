package gameRun;

import graphics.Background;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.*;

public class GameOver extends AbstractLevelSet {
	
	// game over background
	private Background bg;
	
	// music clip
	private Clip clip;
	
	private boolean isSelected = false; // true if player pressed ENTER
	private String[] options = { "Submit Score", "Main Menu", "Quit" }; // array of options
	private int currentChoice = 0; // player's current cursor choice
	private int no = 0; // used in displaying loading screen
	private String username;
	private int score;
	
	// Color
	private final Color BRIGHTYELLOW = new Color(255, 255, 102);
	
	// constructor
	public GameOver(LevelManager lm, String name, int score) {
		this.lm = lm;
		username = name;
		this.score = score;
		init();
	}
	
	// initializes variables
	public void init() {
		
		// loads background
		bg = new Background("/background/gameOverbg.gif", 0.1);
		
		try {
			// loads music
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/music/musicgameover1.3.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// starts music if it isn't muted
		if (Menu.isNotMuted) {
			clip.start();
		}
		
	}
	
	// updates continuous non-graphical Thread
	public void update() {
		
		// update selected and perform action
		if (isSelected && no == 1) {
			no = 0;
			isSelected = false;
			if (currentChoice == 0) {
				try {
					Writer output = null;
					File file = new File("res/educationDatabase/leaderboard");
					output = new BufferedWriter(new FileWriter(file, true));
					output.write("\n" + username);
					output.write("\n" + score);
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				clip.stop(); // stop current music
				lm.setLevel(LevelManager.MENU);
			} else if (currentChoice == 1) {
				clip.stop(); // stop current music
				lm.setLevel(LevelManager.MENU);
			} else if (currentChoice == 2) {
				System.exit(0);
			}
		}
		
	}

	// updates continuous graphical Thread
	public void draw(Graphics2D g) {
		
		// draw background
		bg.draw(g);
		
		// draw options
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoice) {
				g.setColor(BRIGHTYELLOW);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawString(options[i], 120, 360 + i * 30);
		}
		
		// helpful hint
		g.drawString("Make sure to look for the extra life in each level!", 95, 300);
		
		// draw loading
		if (isSelected) {
			g.setColor(Color.GREEN);
			g.drawString("LOADING...", 400, 450);
			no++;
		}
		
	}

	// user input for selecting options
	public void keyPressed(int k) {
		
		// user input to move cursor and select option
		switch (k) {
			case KeyEvent.VK_UP: currentChoice--;
			if (currentChoice == -1) currentChoice = options.length - 1;
			break;
			case KeyEvent.VK_DOWN: currentChoice++;
			if (currentChoice == options.length) currentChoice = 0;
			break;
			case KeyEvent.VK_ENTER: isSelected = true;
			break;
		}
		
	}

	public void keyReleased(int k) {
		
	}

}
