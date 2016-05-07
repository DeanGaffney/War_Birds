package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class AbstractGameObject {

	public Level 		level;
	
	public Vector2		position;
	public Vector2		dimension;
	public Vector2		origin;
	public Vector2		scale;
	public float		rotation;
	public Vector2		velocity;
	public Vector2		terminalVelocity;
	public Vector2		friction;
	public Vector2		acceleration;

	public float		stateTime;
	public Animation	animation;
	
	public float 		timeToDie;

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
	}

	public void update(float deltaTime) {
		
		if (state == State.ASLEEP) return; 
		
		stateTime += deltaTime;
		
		updateMotionX(deltaTime);
		updateMotionY(deltaTime);

		// Move to new position
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;
		
		if (state == State.DYING) {
			timeToDie -= deltaTime;
			if (timeToDie<0) state = State.DEAD;
		}
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
		stateTime = 0;
	}

	public abstract void render(SpriteBatch batch);

	protected void updateMotionX(float deltaTime) {}

	protected void updateMotionY(float deltaTime) {}
	
	public boolean isInScreen()  {
		return ((position.x>-Constants.VIEWPORT_WIDTH/2 && position.x<Constants.VIEWPORT_WIDTH/2) && 
				(position.y>level.start && position.y<level.end));
		
	}
}
