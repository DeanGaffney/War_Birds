package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractEnemy extends AbstractGameObject{
		
		public Animation animation;
		public TextureRegion region;
		public float timeShootDelay;
		
		public AbstractEnemy (Level level){
			super(level);
			timeShootDelay = 0;
		}
		
		public void update(float deltaTime){
			super.update(deltaTime);
			if(super.isInScreen())state = State.ACTIVE;
			
			//shoot
			shoot();
			timeShootDelay -= deltaTime;
			
			// Move to new position
			position.x += velocity.x * deltaTime;
			position.y += velocity.y * deltaTime;
			/*if(nearPlayer()){
				//kamikaze
			}*/
		}
		
		public void setAnimation(Animation animation) {
			this.animation = animation;
			stateTime = 0;
		}

		protected boolean nearPlayer(){
			return(level.player.position.x - position.x) < 5 &&
					level.player.position.y - position.y < 5;
		}
		
		public void render(SpriteBatch batch){
			region = animation.getKeyFrame(stateTime, true);

			batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
				dimension.x, dimension.y, scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);
		}
		
		public void shoot() {
			if (timeShootDelay>0) return;
			System.out.println("Shooting is being called");
			
			// get bullet
			Bullet bullet = level.bulletPool.obtain();
			bullet.reset();
			bullet.position.set(position);
			
			//reverse the bullet direction for enemies
			bullet.velocity.y = -Constants.BULLET_SPEED;
			bullet.velocity.x = velocity.x;
			bullet.rotation = rotation;
			
			level.bullets.add(bullet);
			timeShootDelay = Constants.ENEMY_SHOOT_DELAY;
		}
}
