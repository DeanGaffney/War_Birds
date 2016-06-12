package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class WhitePlane extends AbstractEnemy{

	private float radius;			//this is the radius of the circle i want for the movement
	private float theta;			//rate in steps the circle is to be complete.
	
	public WhitePlane(Level level) {
		super(level);
		init();
	}

	public void init(){
		dimension.set(1, 1);
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.whiteEnemy.region;
		normalAnimation = Assets.instance.whiteEnemy.animationNormal;
		explosionAnimation = Assets.instance.whiteEnemy.animationExplosionBig;
		setAnimation(normalAnimation);
		velocity.x = 2.0f;
		velocity.y = -2.0f;
		radius = 2;
		theta = 0.02f;
		score = 200;
		isBoss = false;
	}

	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		//circular motion
		theta = theta + 0.02f;													//update rate at which you want circle done
		velocity.x = (float) (origin.x + (radius*Math.cos(Math.PI/5 * theta)));	//update x
		velocity.y = (float) (origin.y + (radius*Math.sin(Math.PI/5 * theta)));	//update y
	}

	
}
