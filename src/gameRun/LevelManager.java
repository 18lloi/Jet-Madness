package gameRun;

import java.awt.Graphics2D;

// central manager that manages all levels and stages of the game
public class LevelManager {

	// variable to manage levels
	private AbstractLevelSet[] levels;
	private int currentLevel;
	private String username;
	private int score;
	private int lives;
	public static int previousLevel; // used to go to previous level

	// list of levels assigned integer
	public static final int NUMBEROFLEVELS = 8;
	public static final int MENU = 0;
	public static final int LEVEL1 = 1;
	public static final int LEVEL2 = 2;
	public static final int LEVEL3 = 3;
	public static final int LEVEL4 = 4;
	public static final int LEVEL5 = 5;
	public static final int GAMEOVER = 6;
	public static final int CONGRATULATION = 7;

	public LevelManager() {

		levels = new AbstractLevelSet[NUMBEROFLEVELS];

		currentLevel = MENU; // default level to begin
		score = 0; // set score to 0
		loadLevel(currentLevel); // load menu
		
	}
	
	// loads current level by parameter
	private void loadLevel(int state) {
		if (state == MENU) {
			levels[state] = new Menu(this);
		}
		if (state == LEVEL1) {
			levels[state] = new Level1(this);
		}
		if(state == LEVEL2){
			levels[state] = new Level2(this); 
		}
		if (state == LEVEL3) {
			levels[state] = new Level3(this);
		}
		if (state == LEVEL4) {
			levels[state] = new Level4(this);
		}
		if (state == LEVEL5) {
			levels[state] = new Level5(this);
		}
		if(state == GAMEOVER){
			levels[state] = new GameOver(this, username, score); 
		}
		if(state == CONGRATULATION){
			levels[state] = new GameBeat(this, username, score); 
		}
	}

	// stop and delete the current level
	private void unloadState(int state) {
		levels[state] = null;
	}
	
	
	// getter and setter for current level
	public int getCurrentLevel() {
		return currentLevel;
	}

	// holds previous level in case player dies and wants to retry level and sets a new level
	public void setLevel(int level) {
		previousLevel = currentLevel;
		unloadState(currentLevel);
		currentLevel = level;
		loadLevel(currentLevel);
	}
	
	// setter for name
	public void setUsername(String a) {
		username = a;
	}
	
	// getter for score
	public int getScore() {
		return score;
	}
	
	// sets lives
	public void setLives(int l) {
		lives = l;
	}
	
	// gets lives
	public int getLives() {
		return lives;
	}
	
	// sets any addition to the score
	public void setScore(int change) {
		score += change;
	}

	// sends updates to the current level
	public void update() {
		try {
			levels[currentLevel].update();
		} catch (Exception e) {
			
		}
	}

	// send draw method to the current level
	public void draw(Graphics2D g) {
		try {
			levels[currentLevel].draw(g);
		} catch (Exception e) {
			
		}
	}
	
	// sends user input to the current level
	public void keyPressed(int k) {
		try {
			levels[currentLevel].keyPressed(k);
		} catch (NullPointerException e) {
			
		}
	}

	// sends releasing key input to the current level
	public void keyReleased(int k) {
		try {
			levels[currentLevel].keyReleased(k);
		} catch (NullPointerException e) {
			
		}
	}

}
