package sprites;

import java.util.ArrayList;

import graphics.Map;
import sprites.AbstractSpriteSet;

// template for enemies
public class EnemyJet extends AbstractSpriteSet {

	public int HP, maxHP, damage, spawnLocation; // virus stats
	public boolean dead, stun, leftScreen; // if virus is dead or stunned
	public long spawnTime, stunTimer; // same as player - invincible stun time 
	public ArrayList<Bullet> bullet; // virus weapon
	
	public EnemyJet(Map map) {
		super(map);
	}
	
	// getters
	public boolean getDeath() {
		return dead;
	}
	
	public boolean leftScreen() {
		return leftScreen;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public ArrayList<Bullet> getBullet() {
		return bullet;
	}
	
	public long getSpawnTime() {
		return spawnTime;
	}
	
	public int getSpawnLocation() {
		return spawnLocation;
	}
	
	// setters
	public void setDeath(boolean b) {
		dead = b;
	}
	
	public void setFullHealth() {
		HP = maxHP;
	}
	
	// virus being hit method
	public void hit(int damage) {
		if (dead || stun) {
			return;
		}

		HP -= damage;

		if (HP < 0) {
			HP = 0;
		}

		if (HP == 0) {
			dead = true;
		}

		stun = true;
		stunTimer = System.nanoTime();
	}
	
	public void update(double x, double y) {
		
	}

}
