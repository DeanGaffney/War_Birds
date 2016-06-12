package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GamePreferences;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {
	GamePreferences prefs = GamePreferences.instance;
	public Level 		level;

	public Vector2		position;
	public Vector2		dimension;
	public Vector2		origin;
	public Vector2		scale;
	public float		rotation;
	public float		direction;
	public Vector2		velocity;
	public Vector2		terminalVelocity;
	public Vector2		friction;
	public Vector2		acceleration;

	public float		stateTime;
	public Animation	animation;

	public float 		timeToDie;
	public float 		timeShootDelay;
	public float 		offset;		//required for planes who may need to rotate.
	public Rectangle    bounds;		//for collision detection.
	public Rectangle	hitBox;
	public boolean		destroyed;
	public float 		health;		//health for game objects (player and enemies)
	public ShapeRenderer box;


	public enum State {
		ASLEEP, // not yet in screen area 
		ACTIVE, // in screen area 
		DYING,	// outside screen area but has short time to enter it 
		DEAD	// to be removed from game
	}
	public State state;



	public AbstractGameObject(Level level) {
		this.level = level;
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		state = State.ASLEEP;
		bounds = new Rectangle();
		hitBox = new Rectangle();
		destroyed = false;
		health = 100;
		box = new ShapeRenderer();
		animation = null;
	}

	public void update(float deltaTime) {

		if (state == State.ASLEEP) return; 

		stateTime += deltaTime;

		// Move to new position
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;

		//shooting delay
		timeShootDelay -= deltaTime;

		//keep updating hitbox position
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);

		//check if object is dead.
		if (state == State.DYING) {
			timeToDie -= deltaTime;
			if (timeToDie <= 0) state = State.DEAD;
		}
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
		stateTime = 0;
	}

	public abstract void render(SpriteBatch batch);

	public boolean isInScreen()  {
		return ((position.x>-Constants.VIEWPORT_WIDTH/2 && position.x<Constants.VIEWPORT_WIDTH/2) && 
				(position.y>level.start && position.y<level.end));
	}

	/*
	 * Im including the shoot method and time shoot delay in here because both player and enemies shoot,
	 * this means I can avoid writing shoot methods for each one.
	 * The new boolean argument simply tells me if it's the player shooting or the enemy.
	 * From there I decide their bullet delay times and appropriate positions.
	 */
	public void shoot(boolean isPlayer){
		if (timeShootDelay>0) return;

		// get bullet
		Bullet bullet = level.bulletPool.obtain();
		bullet.reset();
		bullet.position.set(position);

		//set the bullet direction for enemies rotation and velocity,else set it with the player in mind
		if(!isPlayer){
			bullet.velocity.y = velocity.y * 2;
			bullet.velocity.x = velocity.x * 2;
			bullet.rotation = rotation;
			bullet.enemyBullet = true;
		}else{
			bullet.velocity.y = Constants.BULLET_SPEED;
			bullet.velocity.x = velocity.x;
			bullet.rotation = rotation;
			bullet.enemyBullet = false;
		}

		level.bullets.add(bullet);
		
		//decide what sound to play.
		Sound soundToPlay = (isPlayer) ? Assets.instance.sounds.playerShoot : Assets.instance.sounds.enemyShoot;
		AudioManager.instance.play(soundToPlay);
		
		//decide delay time depending on type of player.
		timeShootDelay = (isPlayer) ? Constants.PLAYER_SHOOT_DELAY : prefs.enemyShootSpeed;

	}
}
