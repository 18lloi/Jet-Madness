package gameRun;

import java.awt.Graphics2D;

// Abstract Template for all levels
public abstract class AbstractLevelSet {

	protected LevelManager lm; // connects all levels to the central manager

	public abstract void init(); // initialize variables

	public abstract void update(); // continuous loop that updates non-grahical elements

	public abstract void draw(Graphics2D g); // continuous loop that updates grahical elements

	public abstract void keyPressed(int k); // user input

	public abstract void keyReleased(int k); // stops user input

}
