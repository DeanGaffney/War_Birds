package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

import wit.cgd.warbirds.ai.AbstractEnemy;
import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.LevelDecoration.Shield;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GameStats;

public class Player extends AbstractGameObject {
	//Shooting is now handled in abstract game object to avoid duplicate code.
	public static final String TAG = Player.class.getName();

	private Animation normalAnimation;
	private Animation explosionAnimation;
	private TextureRegion region;
	public int score;
	public float damageDelay;		//if  a player has been hit by a bullet they cant take damage again until this timer is up.
	public float invincibleTime;
	public boolean hasShield;
	public int nextScoreForHealth;	//the next score that needs to be achieved for a health increase.

	//read handleInvincibilty() method below for more details.
	public enum Invincible {NORMAL,POWER_UP,DAMAGED};  //if player is currently invincible due to power up,bullet damage,or normal state(i.e can be damaged)..

	public Invincible invincible;

	public Player (Level level) {
		super(level);
		init();
	}

	public void init() {
		dimension.set(1, 1);

		//set up animations
		normalAnimation = Assets.instance.player.animationNormal;
		explosionAnimation = Assets.instance.player.animationExplosionBig;
		setAnimation(normalAnimation);

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);
		timeShootDelay = 0;
		state = State.ACTIVE;
		invincible = Invincible.NORMAL;
		invincibleTime = 0;
		damageDelay = Constants.DAMAGE_DELAY;
		hasShield = true;			//start player off with a shield.
		nextScoreForHealth = 2000;
	}

	@Override
	public void update (float deltaTime) {
		super.update(deltaTime);

		//if in screen and not dead enemy must be active.
		if(isInScreen() && !isDead()) state = State.ACTIVE;

		//clamp players movement
		position.x = MathUtils.clamp(position.x,-Constants.VIEWPORT_WIDTH/2+0.5f,Constants.VIEWPORT_WIDTH/2-0.5f);
		position.y = MathUtils.clamp(position.y,level.start+2, level.end-2);

		//keep updating hitbox position
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);

		//handle all collisions for the player
		handleCollisions();

		//if player is in a state of invinicibilty then handle it depending on their form of invincibility.
		if(isInvincible())handleInvincibility(deltaTime);
		
		//see if player has earned more health
		if(hasEarnedHealth() && health != 100){
			nextScoreForHealth += 2000;
			health += 10;
		}

	}

	//return if player is invincible or not
	public boolean isInvincible(){
		return invincible == Invincible.DAMAGED || invincible == Invincible.POWER_UP;
	}

	/*
	 * This method of handling levels of invincibilty deals with if a player is in a current state of 
	 * invincibility from being damaged. The reason I allow for a small recovery time is to avoid any
	 * double damage taken by a single bullet and it decreases the players health very smoothly.
	 * Previously the player was being killed too quick and I susupect it was from double damage being taken
	 * by single entities. This recovery timer has solved the problem.
	 * 
	 * The second part of the function deals with the player picking up a power up and
	 * being in a state of invincibilty from picking up the power up.
	 */
	public void handleInvincibility(float deltaTime){
		//see if player has been hit with bullet and avoid any double damage issues with timer.
		if(invincible == Invincible.DAMAGED){
			damageDelay -= deltaTime;
			if(damageDelay < 0){
				invincible = Invincible.NORMAL;
				damageDelay = Constants.DAMAGE_DELAY;
			}
		}

		//see if player has power up and deal with the timer
		if(invincible == Invincible.POWER_UP){
			invincibleTime -= deltaTime;
			if(invincibleTime < 0){
				invincible = Invincible.NORMAL;
				invincibleTime = 0;			//reset upon collision with shield.
			}
		}
	}

	//handles all collisions with regards to the player
	public void handleCollisions(){

		//handle enemy bullet collisions
		for(Bullet bullet : level.bullets){
			if(!bullet.enemyBullet)continue;
			if(Intersector.overlaps(hitBox,bullet.hitBox)){
				bullet.state = Bullet.State.DEAD;
				onCollisionWithBullet();
				break;
			}
		}

		//shield collisions
		for(Shield shield : level.levelDecoration.shields){
			if(shield.collected || invincible == Invincible.POWER_UP)continue;
			if(Intersector.overlaps(hitBox, shield.hitBox)){
				shield.collected = true;
				shield.state = Shield.State.DEAD;
				onCollisionWithShield();
				break;
			}
		}
	}

	//collision with enemy bullet
	public void onCollisionWithBullet(){
		//don't proceed if already dead and dont do damage if they are in asleep state.
		if(isDead() || state == State.ASLEEP || invincible == Invincible.DAMAGED || invincible == Invincible.POWER_UP)return;

		if(health > 0){
			health -= 10;
			invincible = Invincible.DAMAGED;			//make them invincible for a short time to avoid double damage issues.
			AudioManager.instance.play(Assets.instance.sounds.playerHit);
			if(isDead()){
				setAnimation(explosionAnimation);
				AudioManager.instance.play(Assets.instance.sounds.playerExplosion);	
				timeToDie = Constants.DIE_DELAY;
				state = State.DYING;
			}
		}
	}

	//collision with enemy (called within the enemy class)
	public void onCollisionWithEnemy(AbstractEnemy enemy){
		//don't proceed if already dead and dont do damage if they are in asleep state.
		if(isDead() || state == State.ASLEEP || invincible == Invincible.DAMAGED || invincible == Invincible.POWER_UP)return;

		enemy.setAnimation(explosionAnimation); // blow up the enemy if it collides with you
		AudioManager.instance.play(Assets.instance.sounds.enemyExplosion);
		enemy.state = State.DYING;
		enemy.timeToDie = Constants.DIE_DELAY;
		GameStats.instance.kill();						//update stats.	(only countsif player actually killed enemy)
		if(health > 0){
			health -= 10;
			invincible = Invincible.DAMAGED;			//make them invincible for a short time to avoid double damage issues.
			AudioManager.instance.play(Assets.instance.sounds.playerHit);
			if(isDead()){
				setAnimation(explosionAnimation);
				AudioManager.instance.play(Assets.instance.sounds.playerExplosion);
				timeToDie = Constants.DIE_DELAY;
				state = State.DYING;
			}
		}
	}

	//collision with shield.
	public void onCollisionWithShield(){
		//dont allow double power ups.
		if(invincible == Invincible.POWER_UP)return;

		hasShield = true;			//say player has shield (activation handled in worldcontroller on key press 'R').
	}

	//method called from world controller when shield activation buttoned called.
	public void activateShield(){
		//dont allow double power ups.
		if(invincible == Invincible.POWER_UP)return;
		
		hasShield = false;							//use up shield .
		invincible = Invincible.POWER_UP;			//set invincibility.
		invincibleTime = Constants.INVINCIBLE_TIME; //set the timer for power up.
	}
	
	//increases players health by 10 every 2000 points.
	public boolean hasEarnedHealth(){
		return score >= nextScoreForHealth;
	}

	//sees if player is dead or not.
	public boolean isDead(){
		return health <= 0;
	}

	public void render (SpriteBatch batch) {
		region = animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
				dimension.x, dimension.y, scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);
	}

}
