package sprites;

import java.awt.image.BufferedImage;

public class Animation {

	private BufferedImage[] frames; // animation images
	private int currentFrame; // current frame as integer

	private long startTime; // when the current frame starts
	private long delay; // time before new image takes place of old one - like flipbook speed

	private boolean playedOnce; // true once animation for certain action plays once ie shooting

	public Animation() {
		playedOnce = false;
	}

	// reset frames back to default
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		currentFrame = 0;
		startTime = System.nanoTime();
		playedOnce = false;
	}

	public void setDelay(long d) {
		delay = d;
	}

	public void update() {
		
		// stop animation if there's only one picture to draw
		if (delay == -1) {
			return;
		}
		
		// change frames according to delay and elapsed time
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		if (elapsed > delay) {
			currentFrame++;
			startTime = System.nanoTime();
		}
		
		// reset current frame back to first after all animation frames have played
		if (currentFrame == frames.length) {
			currentFrame = 0;
			playedOnce = true;
		}
	}

	// getter for image
	public BufferedImage getImage() {
		return frames[currentFrame];
	}

	// true once animation for certain action plays once ie shooting
	public boolean hasPlayedOnce() {
		return playedOnce;
	}
}
