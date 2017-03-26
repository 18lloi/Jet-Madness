package gameRun;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import graphics.Background;

public class Pause extends AbstractLevelSet {

	private Clip clipMute; // used to mute current audio clip
	
	private Background bg; // Pause background
	private boolean isPaused; // if pause screen is up
	private boolean isSelected; //  true if player pressed enter
	private int currentChoice = 0; // whatever choice the player is currently on
	private int no = 0; // used to display loading
	private String[] options = { "Resume", "Toggle Mute", "Main Menu", "Quit"}; // list of pause options
	private final Color BRIGHTYELLOW = new Color(255, 255, 102); // color of selected
	private boolean isPauseDone;
	
	public Pause(LevelManager lm, Clip clip) {
		this.lm = lm;
		// used to mute current audio clip
		clipMute = clip;
		init();
	}
	
	// initialize background
	public void init() {
		bg = new Background("/background/pausebg.gif", 0.1);
	}
	
	// pause setter
	public void setPause(boolean a) {
		isPaused = a;
	}
	
	public void setPauseDone(boolean a) {
		isPauseDone = a;
	}
	
	// pause getter
	public boolean getPaused() {
		return isPaused;
	}
	
	public boolean getPauseDone() {
		return isPauseDone;
	}
	
	public void update() {
		
	}
	
	public void draw(Graphics2D g) {
		
		// draw background
		bg.draw(g);
		
		// draw pause options
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoice) {
				g.setColor(BRIGHTYELLOW);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawString(options[i], 300, 200 + i * 30);
		}
		
		// select button and do course of action
		if (isSelected && no == 1) {
			no = 0;
			isSelected = false;
			if (currentChoice == 0) {
				setPauseDone(true);
			} else if (currentChoice == 1) {
				isSelected = false;
				if (Menu.isNotMuted) {
					Menu.isNotMuted = false;
					clipMute.stop();
				} else {
					Menu.isNotMuted = true;
					clipMute.start();
				}
			} else if (currentChoice == 2) {
				clipMute.stop();
				lm.setLevel(LevelManager.MENU);
			} else if (currentChoice == 3) {
				System.exit(0);
			}
		}
		
		// draw loading
		if (isSelected) {
			g.setColor(Color.GREEN);
			g.drawString("LOADING...", 500, 350);
			no++;
		}
	}
	
	public void keyPressed(int k) {
		// up and down to move cursor, enter to select
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