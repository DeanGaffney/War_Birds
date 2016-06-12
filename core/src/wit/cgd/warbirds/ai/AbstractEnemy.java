package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractEnemy extends AbstractGameObject{

	public TextureRegion region;
	public int health;			//health for each enemy
	public int damage;			//the damage the enemy can deal to the player.(multiply this for other enemies)
	public int score;			//the score given to player after defeating the enemy.(multiply this depending on the enemy type.)
	public Animation normalAnimation;
	public Animation explosionAnimation;
	public boolean isBoss;


	public AbstractEnemy (Level level){
		super(level);
		timeShootDelay = 0;
		health = 100;
		damage = 10;		//10 shots to the player seems reasonable for weakest enemy
		score = 100;
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);
	}

	public void update(float deltaTime){
		super.update(deltaTime);

		//if in screen and not dead enemy must be active.
		if(isInScreen() && !isDead()) state = State.ACTIVE;

		//keep updating hitbox position
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);

		//every normal enemy should be at the right rotation according to their movement (boss rotation handled in boss class)
		if(!isBoss){
			rotation = MathUtils.atan2(velocity.y, velocity.x) * MathUtils.radiansToDegrees + 90f;
		}

		//shoot and let game know its not the player shooting
		shoot(false);

		//check for collisions and respond appropriately.
		handleCollisions();
	}

	//handles all collisions with regards to the enemy
	public void handleCollisions(){
		//handle enemy bullet collisions
		for(Bullet bullet : level.bullets){
			if(bullet.enemyBullet)continue;
			if(Intersector.overlaps(hitBox,bullet.hitBox)){
				bullet.state = Bullet.State.DYING;
				bullet.timeToDie = Constants.BULLET_DIE_DELAY;
				onCollisionWithBullet();
				break;
			}
		}

		//handle enemy player collision
		if(Intersector.overlaps(hitBox, level.player.hitBox))onCollisionWithPlayer();
	}

	//enemy hits player
	public void onCollisionWithPlayer(){
		level.player.onCollisionWithEnemy(this);
	}

	//player bullet hits enemy
	public void onCollisionWithBullet(){
		//don't proceed if already dead and dont do damage if they are in asleep state.
		if(isDead() || state == State.ASLEEP)return;

		if(health > 0){
			health -= 40;
			if(isDead()){	//check if dead after health deduction
				level.player.score += getScore();
				setAnimation(explosionAnimation);
				AudioManager.instance.play(Assets.instance.sounds.enemyExplosion);
				timeToDie = Constants.DIE_DELAY;
				state = State.DYING;
			}
		}
	}

	//checks to see if near player. (used in yellow plane for kamikaze calculation)
	protected boolean nearPlayer(){
		return(Vector2.dst(level.player.position.x,level.player.position.y, position.x, position.y) < 5);
	}

	//returns the score to give to player
	public int getScore(){
		return score;
	}

	//checks if enemy is dead (no health left)
	public boolean isDead(){
		return health <= 0;
	}

	//this is called for all abstract objects so should run.
	public void render(SpriteBatch batch){
		region = animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
				dimension.x, dimension.y, scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);
	}
}
