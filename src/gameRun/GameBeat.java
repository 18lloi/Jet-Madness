package gameRun;

import graphics.Background;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameBeat extends AbstractLevelSet {
	
	private Background bg; // background
	
	Clip clip; // Audio clip for victory
	
	private boolean isSelected = false; // true if player pressed ENTER
	private String[] options = { "Continue to the offense", "Submit Score and return to main menu", "Main Menu", "Quit" }; // array of options
	private int currentChoice = 0; // player's current cursor choice
	private int no = 0; // used in displaying loading screen
	private String username; // name that user gives in beginning of game
	private int score; // score that user has acheived
	
	// Color
	private final Color BRIGHTYELLOW = new Color(255, 255, 102);
	
	// constructor sets name, score, connects to central manager, and basically initializes some variables
	public GameBeat(LevelManager lm, String name, int score) {
		username = name;
		this.lm = lm;
		this.score = score;
		init();
	}
	
	// initializes variables
	public void init() {
		
		// sets background
		bg = new Background("/background/congratulationsbg.gif", 0.1);
		
		// winning music
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/music/Triumph_Health_Sound.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// does not play if mute is on
		if (Menu.isNotMuted) {
			clip.start();
		}
		
	}
	
	// updates non-graphical continuous Thread
	public void update() {
		
		// update option choices and perform action
		if (isSelected && no == 1) {
			no = 0;
			isSelected = false;
			if (currentChoice == 0) { // continue to attacking enemy
				clip.stop();
				lm.setLevel(LevelManager.LEVEL4);
			} else if (currentChoice == 1) { // submit score to leaderboard and bring back to menu
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
			} else if (currentChoice == 2) { // go back to menu
				clip.stop(); // stop current music
				lm.setLevel(LevelManager.MENU);
			} else if (currentChoice == 3) { // close game
				System.exit(0);
			}
		}
		
	}

	// updates grahical continous Thread
	public void draw(Graphics2D g) {
		
		// draws background
		bg.draw(g);
		
		//draws Options
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoice) {
				g.setColor(BRIGHTYELLOW);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawString(options[i], 120, 360 + i * 30);
		}
		
		// draws loading screen
		if (isSelected) {
			g.setColor(Color.GREEN);
			g.drawString("LOADING...", 400, 450);
			no++;
		}
		
	}
	
	// user input to select option
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			isSelected = true;
		} else if (k == KeyEvent.VK_UP) {
			currentChoice--;
			if (currentChoice == -1) {
				currentChoice = options.length - 1;
			}
		} else if (k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if (currentChoice == options.length) {
				currentChoice = 0;
			}
		}
	}

	public void keyReleased(int k) {
		
	}

}
