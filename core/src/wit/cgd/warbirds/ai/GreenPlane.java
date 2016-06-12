package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/*
 * Green plane is the weakest or most basic of planes.Uses all the base vairables from abstract enemy.
 * His basic movement is to move down the screen going from side to side and rotating in the correct 
 * direction it's going in.
 */
public class GreenPlane extends AbstractEnemy{

	public GreenPlane(Level level) {
		super(level);
		init();
	}

	public void init(){
		dimension.set(1, 1);
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.greenEnemy.region;
		normalAnimation = Assets.instance.greenEnemy.animationNormal;
		explosionAnimation = Assets.instance.player.animationExplosionBig;
		setAnimation(normalAnimation);
		velocity.x = 1.0f;
		velocity.y = -1.0f;
		animation = Assets.instance.greenEnemy.animationNormal;
		offset = 10.f;
		score = 100;
		isBoss = false;
	}



	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		//movement
		
		//change offset and velocity depending on direction
		if (position.x<-3) {
			velocity.x = 2.0f;
		} else if (position.x>3) {
			velocity.x = -velocity.x;
		}
	}
}
