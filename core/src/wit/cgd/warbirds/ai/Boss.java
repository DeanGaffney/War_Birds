package wit.cgd.warbirds.ai;

import com.badlogic.gdx.math.MathUtils;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GamePreferences;

public class Boss extends AbstractEnemy{
	private float xDistance;
	private float yDistance;
	private float bulletSpeed;
	private boolean bossDead;

	public Boss(Level level) {
		super(level);
		dimension.set(1, 1);
		scale.set(2,2);			//make boss bigger than normal enemies.
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.boss.region;
		normalAnimation = Assets.instance.boss.animationNormal;
		explosionAnimation = Assets.instance.boss.animationExplosionBig;
		setAnimation(normalAnimation);
		velocity.x = 2.0f;
		velocity.y = 0.f;
		score = 500;
		health = 4000;
		isBoss = true;
		bulletSpeed = 5f;
		bossDead = false;
	}

	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		
		//plays a boss defeated audio clip
		if(isDead() && !bossDead)bossDefeatedSound();
		
		//call boss shooting method
		shoot(false);

		//clamp bosses movement don't want him to move out of screen and die.
		if(state == State.ACTIVE){
			position.x = MathUtils.clamp(position.x,-Constants.VIEWPORT_WIDTH/2+0.5f,Constants.VIEWPORT_WIDTH/2-0.5f);
			position.y = MathUtils.clamp(position.y,level.start+2, level.end-2);
		}

		//if we aren't near the player move at a slower pace so as to stay in the screen and range,otherwise speed up and back off.
		velocity.y = (nearPlayer()) ? Constants.SCROLL_SPEED : Constants.SCROLL_SPEED/2;

		//move left to right
		if (position.x<-3) {
			velocity.x = 2.0f;
		} else if (position.x>3) {
			velocity.x = -velocity.x;
		}
	}
	
	private void bossDefeatedSound() {
		bossDead = true;
		//stop music and play boss defeated sound.
		AudioManager.instance.playingMusic.pause();
		AudioManager.instance.play(Assets.instance.sounds.bossDefeated);
	}

	//boss handles shooting slightly differently according to rotation etc..
	//could include boolean in original shoot method but it becomes to messy to control then,better to override in my view.
	@Override
	public void shoot(boolean isPlayer){
		if (timeShootDelay>0) return;

		// get bullet
		Bullet bullet = level.bulletPool.obtain();
		bullet.reset();
		bullet.position.set(position);

		//set the bullet direction for enemies rotation and velocity,else set it with the player in mind
		if(!isPlayer){
			//set enemy bullet to true
			bullet.enemyBullet = true;

			//set position to that of the boss
			bullet.position.x = position.x;
			bullet.position.y = position.y;

			//calculate distance between bullet and player
			xDistance = level.player.position.x - bullet.position.x;
			yDistance = level.player.position.y - bullet.position.y;

			//rotate in terms of degrees.
			bullet.rotation = (float) (MathUtils.atan2(yDistance, xDistance) * 180 /Math.PI);

			
			bullet.velocity.x = bulletSpeed * (90 - Math.abs(bullet.rotation)) / 90;
			if (bullet.rotation < 0){
				bullet.velocity.y = -bulletSpeed + Math.abs(velocity.x);//Going upwards.
			}
			else{
				bullet.velocity.y = bulletSpeed - Math.abs(velocity.x);//Going downwards.
			}
		}

		level.bullets.add(bullet);
		AudioManager.instance.play(Assets.instance.sounds.enemyShoot);

		//decide delay time depending on type of player.
		timeShootDelay = GamePreferences.instance.enemyShootSpeed;
	}

}
