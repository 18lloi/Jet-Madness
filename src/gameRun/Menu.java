package gameRun;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import graphics.Background;

public class Menu extends AbstractLevelSet {

	// Whether game should be muted or not - used throughout all classes
	public static boolean isNotMuted = true;
	private String username; // sets user name that is given

	private Background bg; // background

	private boolean isSelected = false; // true when user hits enter
	private boolean menuScreen = true; // whether main menu page is open or not
	private boolean directionScreen = false; // whether direction page is open or not
	private boolean rulesScreen = false;// whether rules page is open or not
	private boolean leaderboardScreen = false; // whether leaderboard is open or not
	private int no = 0; // helps creates loading screen
	private ArrayList<String> leaderboardNames; // list of all the names that made it on the leaderboard
	private ArrayList<Integer> leaderboardStats; // parallel list that shows their score

	private int currentChoice = 0; // keeps track of which action
	
	// buttons, options, directions, and rules
	private String[] menuButtons = { "Start", "Leaderboard", "Directions", "Rules", "Options", "Quit" };
	private String[] directionButtons = {
			"Your country has been invaded by a neighboring country under the regime",
			"of Mike. The year is 1982. As a pilot in the air force, it is your duty to protect",
			"your country. You will be operating a jet and fending off enemies.",
			"",
			"Use the WASD keys to navigate. The plane automatically shoots.",
			"Objective: Shoot the enemies and don’t get hit."
			};
	private String[] rulesButtons = {
			"There are three lives, and each life has 100 hit points. Upon losing a life,",
			"you must answer a question. Answering incorrectly awards no points,",
			"answering correctly awards you 50. Answering a question after using a",
			"hint will award 25 points. At the end of each level, you are awarded 100",
			"points per life remaining. The three lives that are given at the beginning",
			"are all that you get. Killing an enemy earns you 25 points.",
			"Best of luck soldier."
			};
	private String[] optionButtons = { "Toggle Mute", "Back", "Quit" };
	private String tip = "*Use arrow keys to navigate and enter key to select";
	
	private int numberOptions; // length of current button list

	private Color titleColor;
	private final Color BRIGHTRED = new Color(255, 100, 100);
	private final Color BRIGHTYELLOW = new Color(255, 185, 81);

	private Font titleFont;
	private Font font;
	
	// create menu music
	private Clip clip;

	// constructer calls init, creates moving background, and creates and starts music
	public Menu(LevelManager lm) {

		this.lm = lm;
		init();

		try {
			
			// load and set background
			bg = new Background("/background/menubg.gif", 1);
			bg.setVector(-.5, 0);

			// word color and font
			titleColor = new Color(255, 168, 38);
			titleFont = new Font("Magneto", Font.PLAIN, 72);
			font = new Font("Arial", Font.BOLD, 18);
			
			// music init
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/music/Arcade_Music_Menu.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			
			// leaderboard init
			leaderboardNames = new ArrayList<String>();
			leaderboardStats = new ArrayList<Integer>();
			int lineNumber = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res/educationDatabase/leaderboard")));
			String str;
			while ((str = br.readLine()) != null) {
				lineNumber++;
				switch (lineNumber % 2) {
					case 1: leaderboardNames.add(str); break;
					case 0: leaderboardStats.add(Integer.parseInt(str)); break;
				}
			}
			br.close();

			// selection sorts leaderboard from highest score to lowest score
			for(int i = 0; i < leaderboardStats.size() - 1; i++){
				int maxIndex = i;
				for (int j = i + 1; j < leaderboardStats.size(); j++) {
					if (leaderboardStats.get(j) > leaderboardStats.get(maxIndex)) {
						maxIndex = j;
					}
				}
				if(maxIndex != i){
					int temp = leaderboardStats.get(i);
					String temp2 = leaderboardNames.get(i);
					leaderboardStats.set(i, leaderboardStats.get(maxIndex));
					leaderboardNames.set(i, leaderboardNames.get(maxIndex));
					leaderboardStats.set(maxIndex, temp);
					leaderboardNames.set(maxIndex, temp2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// starts menu music
		clip.start();
	}
	
	// initializes numberOptions to the number of buttons in the MENU screen
	public void init() {
		numberOptions = menuButtons.length;
	}

	// only thing to update is background which moves
	public void update() {
		bg.update();
	}
	
	// draws menu options
	private void menuScreen(Graphics2D g) {
		for (int i = 0; i < menuButtons.length; i++) {
			if (i == currentChoice) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(BRIGHTYELLOW);
			}
			g.drawString(menuButtons[i], 120, 250 + i * 30);
		}
	}
	
	// draws direction
	private void directionScreen(Graphics2D g) {
		g.setColor(Color.WHITE);
		for(int i = 0; i < directionButtons.length; i++) {
			g.drawString(directionButtons[i], 40, 200 + i * 30);
		}
		g.setColor(BRIGHTRED);
		g.drawString("Back", 40, 420);
	}
	
	// draws rules
	private void rulesScreen(Graphics2D g) {
		g.setColor(Color.WHITE);
		for(int i = 0; i < rulesButtons.length; i++) {
			g.drawString(rulesButtons[i], 40, 180 + i * 30);
		}
		g.setColor(BRIGHTRED);
		g.drawString("Back", 40, 420);
	}
	
	// draws leaderboard
		private void leaderboardScreen(Graphics2D g) {
			g.setColor(Color.WHITE);
			for(int i = 0; i < 7; i++) {
				g.drawString(i + 1 + ". " + leaderboardNames.get(i), 40, 200 + i * 30);
			}
			for(int i = 0; i < 7; i++) {
				g.drawString(Integer.toString(leaderboardStats.get(i)), 480, 200 + i * 30);
			}
			g.setColor(BRIGHTRED);
			g.drawString("Back", 40, 420);
		}
	
	// draws options options
	private void optionScreen(Graphics2D g) {
		for (int i = 0; i < optionButtons.length; i++) {
			g.setColor(Color.RED);
			if (i == currentChoice) {
				g.setColor(BRIGHTRED);
			} else {
				g.setColor(Color.RED);
			}
			g.drawString(optionButtons[i], 40, 360 + i * 30);
		}
	}

	// updates continuous graphical Thread
	public void draw(Graphics2D g) {

		// draw background
		bg.draw(g);

		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Jet Madness", 100, 140);
		
		// draw tip
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font("Arial", Font.ITALIC, 13));
		g.drawString(tip, 100, 440);
		g.setFont(font);
		
		// draws which menu screen and the options and directions
		if (menuScreen) {
			menuScreen(g);
		} else {
			if (directionScreen) directionScreen(g);
			else if (rulesScreen) rulesScreen(g);
			else if (leaderboardScreen) leaderboardScreen(g);
			else optionScreen(g);
		}
		
		// select something
		if (isSelected && no == 1) {
			no = 0;
			if (menuScreen) {
				selectMenu();
			} else {
				if (directionScreen) {
					isSelected = false;
					numberOptions = menuButtons.length;
					currentChoice = 0;
					directionScreen = false;
					menuScreen = true;
				} else if (leaderboardScreen) {
					isSelected = false;
					numberOptions = menuButtons.length;
					currentChoice = 0;
					leaderboardScreen = false;
					menuScreen = true;
				} else if (rulesScreen) {
					isSelected = false;
					numberOptions = menuButtons.length;
					currentChoice = 0;
					rulesScreen = false;
					menuScreen = true;
				} else {
					selectOptions();
				}
			}
		}
		
		// draw loading
		if (isSelected) {
			g.setColor(Color.GREEN);
			g.drawString("LOADING...", 400, 450);
			no++;
		}
		
	}

	// each action after selected on main menu screen - start, directions, options, quit
	private void selectMenu() {
		isSelected = false;
		if (currentChoice == 0) { // asks for name and starts name
			clip.stop();
			username = JOptionPane.showInputDialog("Choose your jet name:");
			if (username != null && username.length() > 0) {
				lm.setUsername(username);
				lm.setLevel(LevelManager.LEVEL1);
			}
		} else if (currentChoice == 1) {
			numberOptions = 1;
			leaderboardScreen = true;
			menuScreen = false;
		} else if (currentChoice == 2) {
			numberOptions = 1;
			directionScreen = true;
			menuScreen = false;
		} else if (currentChoice == 3) {
			numberOptions = 1;
			rulesScreen = true;
			menuScreen = false;
		} else if (currentChoice == 4) {
			numberOptions = optionButtons.length;
			currentChoice = 0;
			menuScreen = false;
		} else if (currentChoice == 5) {
			System.exit(0);
		}
	}
	
	// action after selected on options screen - toggle mute, back to main menu screen, quit
	private void selectOptions() {
		isSelected = false;
		if (currentChoice == 0) {
			if (isNotMuted) {
				isNotMuted = false;
				clip.stop();
			} else {
				isNotMuted = true;
				clip.start();
			}
		} else if (currentChoice == 1) {
			numberOptions = menuButtons.length;
			currentChoice = 0;
			menuScreen = true;
		} else if (currentChoice == 2) {
			System.exit(0);
		}
	}
	
	// buttons to move and select cursor
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			isSelected = true;
		} else if (k == KeyEvent.VK_UP) {
			currentChoice--;
			if (currentChoice == -1) {
				currentChoice = numberOptions - 1;
			}
		} else if (k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if (currentChoice == numberOptions) {
				currentChoice = 0;
			}
		}
	}

	public void keyReleased(int k) {

	}

}
