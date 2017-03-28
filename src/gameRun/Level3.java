package gameRun;

import sprites.*;
import graphics.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

import mainclasses.Panel;

import sprites.Player;

import graphics.Map;

// 3rd level of the game
public class Level3 extends AbstractLevelSet {

	private Background bg; // background
	private Map map; // layout of the level
	private int killCount, totalEnemyCount, enemyCount; // keeps score of kills
														// - need to kill
														// all enemies to pass
														// level

	private Clip clip; // Music

	public Player p; // player object
	public LifeBar lb; // draws life bar and lives
	public Pause pause; // pause screen if player opens
	ArrayList<EnemyJet> js; // list of enemy jets to spawn in level
	ArrayList<EnemyJet> j; // list of enemy jets in level
	double[][] jetMovementData; // data of enemies and how they move after their spawn { dx, dy }
	int[][] jetSpawnData; // data of where and when enemies spawn { time of spawn, x position of where they spawn }
	int[] lifeAppearanceData; // data of hidden life appearing { time of spawn, time of disappearance, x location, y location }
	ArrayList<NewLife> nl; // list of hidden lives to obtain in level

	private boolean isQuestioning, questionTimerPause; // whether the player is
														// currently answering a question
	// a question
	private boolean isSelected; // true if player has answered
	private boolean isHint, isSolutions; // if player should see the hint or the solution respectively
	private boolean endLevelQuestion; // whether or not the game should ask the question before switching levels
	private int currentChoice; // current answer choice (1, 2, 3, 4, or 5 for hint)
	ArrayList<String> questions, q1, q2, q3, q4; // stores questions and answer choices collected from outside database
	ArrayList<String> hints, solutions; // stores hints and solutions collected from outside database
	ArrayList<Integer> answers; // stores answers collected from outside database

	// timer of the level - also used for when enemy jets appear
	private long startTime;
	private long currentTime;
	private long pausedTime;

	private int rand; // selects random question from database

	// constructor
	public Level3(LevelManager lm) {
		this.lm = lm;
		init();
	}

	// initializes variables and collects from databases
	public void init() {

		// begins timer
		startTime = System.currentTimeMillis();
		pausedTime = 0; // time that has spent pausing the game initialized to 0

		// parallel arrays
		// sets enemies { dx, dy } speed
		jetMovementData = new double[][] { { 2.5, 0 }, { 1.2, 0 }, { -1.4, 0 },
				{ -2, 1 }, { 0, 1.2 }, { 2.9, 0 }, { 2, .75 }, { -2, .64 },
				{ -1.36, 1 }, { 2.5, 1 }, { -3, 0 }, { 2, .54 }, { 2, .9 },
				{ .4, 1.1 }, { -3, .8 } };
		// { time of appearance, location of appearance }
		jetSpawnData = new int[][] { { 2000, 500 }, { 3000, 350 },
				{ 4000, 400 }, { 10000, 300 }, { 10500, 450 }, { 11000, 100 },
				{ 12500, 500 }, { 13000, 330 }, { 30000, 200 }, { 31000, 250 },
				{ 32000, 600 }, { 33000, 275 }, { 33500, 325 }, { 34000, 300 },
				{ 34500, 224 } };

		// { hidden life spawn time, disappear time, x position, y position }
		lifeAppearanceData = new int[] { 10000, 12000, 320, 200 };

		// loads layout of level - tiles weren't used though
		int blockSize = 30;
		map = new Map(blockSize);
		map.loadTiles("/tiles/tiles1.1.gif");
		map.setMapPosition(0, 0);

		// loads moving background
		bg = new Background("/background/level1.1bg.gif", 0.1);
		bg.setVector(0, 1);

		// initialize player stats and player spawn position
		killCount = 0;
		enemyCount = 0;
		p = new Player(map);
		p.setLives(lm.getLives());
		p.setPosition(Panel.WIDTH / 2, Panel.HEIGHT - 100);

		// create life bar at top left corner
		lb = new LifeBar(p);

		// initialize arraylists for storing questions from database
		questions = new ArrayList<String>();
		q1 = new ArrayList<String>();
		q2 = new ArrayList<String>();
		q3 = new ArrayList<String>();
		q4 = new ArrayList<String>();
		hints = new ArrayList<String>();
		solutions = new ArrayList<String>();
		answers = new ArrayList<Integer>();

		// default question variables
		isQuestioning = false;
		endLevelQuestion = false;
		currentChoice = -1;

		// ADDS ENEMY
		js = new ArrayList<EnemyJet>();
		j = new ArrayList<EnemyJet>();
		for (int i = 0; i < jetMovementData.length; i++) {
			RedJet rjet = new RedJet(map, jetMovementData[i][0],
					jetMovementData[i][1], (long) jetSpawnData[i][0],
					jetSpawnData[i][1]);
			rjet.setPosition(jetSpawnData[i][1], 60);
			js.add(rjet);
		}
		totalEnemyCount = js.size();

		// loads music for the level
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(getClass()
					.getResource("/music/My_Little_Adventure.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// plays music if the game isn't muted
		if (Menu.isNotMuted) {
			clip.start();
		}

		// accesses questions from the database and reads it
		try {
			int lineNumber = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("res/educationDatabase/q1")));
			String str;
			while ((str = br.readLine()) != null) {
				lineNumber++;
				int temp = lineNumber % 8;
				switch (temp) {
				case 1:
					questions.add(str);
					break;
				case 2:
					q1.add(str);
					break;
				case 3:
					q2.add(str);
					break;
				case 4:
					q3.add(str);
					break;
				case 5:
					q4.add(str);
					break;
				case 6:
					answers.add(Integer.parseInt(str));
					break;
				case 7:
					hints.add(str);
					break;
				case 0:
					solutions.add(str);
					break;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create pause screen - ineffective until player hits P
		pause = new Pause(lm, clip);
		pause.setPause(false);

		// places hidden life in map at designated location (only shows during 2 time segments)
		nl = new ArrayList<NewLife>();
		NewLife nlife = new NewLife(map);
		nlife.setPosition(lifeAppearanceData[2], lifeAppearanceData[3]);
		nl.add(nlife);

	}

	// updates continuous non-graphical Thread
	public void update() {

		// pauses timer when game is paused
		if (pause.getPauseDone()) {
			pause.setPauseDone(false);
			startTime = System.currentTimeMillis();
			pausedTime = currentTime;
			currentTime = System.currentTimeMillis() - startTime + pausedTime;
			pause.setPause(false);
		}

		// pauses game, nothing runs except pause class (and the timer pause) if
		// the game is paused
		if (pause.getPaused()) {
			pause.update();
			return;
		}

		// resets screen after questioning is over
		if (!isQuestioning) {
			isSelected = false;
			currentChoice = -1;
		}

		// pauses timer when being questioned
		if (questionTimerPause) {
			questionTimerPause = false;
			startTime = System.currentTimeMillis();
			pausedTime = currentTime;
			currentTime = System.currentTimeMillis() - startTime + pausedTime;
		}

		// nothing runs except questions and pause if the player pauses - if
		// player
		// is right, then a specific action is performed to reward the player
		if (isQuestioning) {

			if (isSelected) {
				isSelected = false;
				if (currentChoice == answers.get(rand)) {
					currentChoice = -1;
					if (!isSolutions) {
						if (isHint) {
							lm.setScore(25);
						} else {
							lm.setScore(50);
						}
					}
					questionTimerPause = true;
					isQuestioning = false;
				} else if (currentChoice == 5) {
					currentChoice = -1;
					isHint = true;
				} else {
					currentChoice = -1;
					isSolutions = true;
				}
			}
			return;
		}

		// updates the current time
		currentTime = System.currentTimeMillis() - startTime + pausedTime;

		// loads map layout of level
		map.loadMap("/maps/map1.1.map");

		// updates background moving
		bg.update();

		// player updates
		p.update();
		p.checkHit(j);
		if (currentTime > lifeAppearanceData[0]
				&& currentTime < lifeAppearanceData[1]) {
			p.checkGetLife(nl);
		}

		// enemy updates including death and score
		for (int i = 0; i < js.size(); i++) {
			if (currentTime > js.get(i).getSpawnTime()) {
				j.add(js.get(i));
				j.get(j.size() - 1).setPosition(js.get(i).getSpawnLocation(),
						60);
				js.remove(i);
			}
		}
		for (int i = 0; i < j.size(); i++) {
			j.get(i).update(p.x, p.y);
			if (j.get(i).getDeath()) {
				j.remove(i);
				killCount++;
				lm.setScore(25);
				enemyCount++;
			}
			if (j.get(i).leftScreen) {
				j.remove(i);
				enemyCount++;
			}
		}

		// actions after player dies - asks a question for player to earn some
		// points
		if (p.getDeath()) {
			if (p.getLives() == 1) {
				clip.stop();
				lm.setLevel(LevelManager.GAMEOVER);
			} else {
				isQuestioning = true;
				Random r = new Random();
				rand = r.nextInt(answers.size());
				p.setNewLife();
				p.setPosition(Panel.WIDTH / 2, Panel.HEIGHT - 100);
			}
		}

		// goes to transition screen if question is answered correctly
		if (endLevelQuestion) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lm.setScore(p.getLives() * 100);
			lm.setLives(p.getLives());
			clip.stop();
			lm.setLevel(LevelManager.TRANSITION);
		}

		// asks final question if every enemy is dead
		if (enemyCount == totalEnemyCount) {
			Random r = new Random();
			rand = r.nextInt(answers.size());
			isQuestioning = true;
			endLevelQuestion = true;
		}

	}

	// updates continuous graphical Thread
	public void draw(Graphics2D g) {

		// draws pause screen, nothing else draws except pause class if the game
		// is paused
		if (pause.getPaused()) {
			pause.draw(g);
			return;
		}

		// nothing draws except questions and pause if the player pauses
		if (isQuestioning) {
			g.setFont(new Font("Magneto", Font.PLAIN, 72));
			g.setColor(Color.BLACK);
			g.drawString("Question", 200, 140);
			g.fillRect(0, 150, Panel.WIDTH, 300);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Times New Roman", Font.PLAIN, 16));
			g.drawString(
					"Press the number of your answer choice. Press 5 for a hint.",
					50, 180);
			g.drawString(questions.get(rand), 50, 230);
			g.drawString(q1.get(rand), 50, 250);
			g.drawString(q2.get(rand), 50, 270);
			g.drawString(q3.get(rand), 50, 290);
			g.drawString(q4.get(rand), 50, 310);
			if (isHint)
				g.drawString(hints.get(rand), 50, 330);
			if (isSolutions)
				g.drawString(solutions.get(rand), 50, 350);
			return;
		}

		// draws background
		bg.draw(g);

		// draws platforms, walls, etc.
		map.draw(g);

		// draws player and health bar
		p.draw(g);
		lb.draw(g);

		// draws the current kills out of total enemies
		g.drawString("Kill Count: " + killCount + " / " + totalEnemyCount, 500,
				35);

		// draws the current score
		g.drawString("Score: " + lm.getScore(), 350, 35);

		// draws the enemies
		for (int i = 0; i < j.size(); i++) {
			j.get(i).draw(g);
		}

		// draws hidden life
		if (currentTime > lifeAppearanceData[0]
				&& currentTime < lifeAppearanceData[1]) {
			for (int i = 0; i < nl.size(); i++) {
				nl.get(i).draw(g);
			}
		}
	}

	// all the user input options here for level 3
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_SPACE) {
			System.out.println("HI");
		}

		switch (k) {
		case KeyEvent.VK_SPACE:
			p.setShooting(true);
			break;
		case KeyEvent.VK_A:
			p.setLeft(true);
			break;
		case KeyEvent.VK_D:
			p.setRight(true);
			break;
		case KeyEvent.VK_W:
			p.setUp(true);
			break;
		case KeyEvent.VK_S:
			p.setDown(true);
			break;
		case KeyEvent.VK_P:
			pause.setPause(true);
			pausedTime = currentTime;
			break;
		case KeyEvent.VK_1:
			currentChoice = 1;
			isSelected = true;
			break;
		case KeyEvent.VK_2:
			currentChoice = 2;
			isSelected = true;
			break;
		case KeyEvent.VK_3:
			currentChoice = 3;
			isSelected = true;
			break;
		case KeyEvent.VK_4:
			currentChoice = 4;
			isSelected = true;
			break;
		case KeyEvent.VK_5:
			currentChoice = 5;
			isSelected = true;
			break;
		}

		// sends user input to pause class if game is paused
		if (pause.getPaused()) {
			pause.keyPressed(k);
		}

	}

	// stop user input if user releases key
	public void keyReleased(int k) {
		switch (k) {
		case KeyEvent.VK_A:
			p.setLeft(false);
			break;
		case KeyEvent.VK_D:
			p.setRight(false);
			break;
		case KeyEvent.VK_W:
			p.setUp(false);
			break;
		case KeyEvent.VK_S:
			p.setDown(false);
			break;
		}
	}

}
