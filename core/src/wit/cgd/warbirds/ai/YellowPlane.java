package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.math.MathUtils;
/*
 * The plane goes back and forth shooting and once the enemy is detected within
 * a range (calculated in nearPlayer() method of abstract enemy class)
 * the enemy dive bombs at the players position acting like a kamikaze.
 */
public class YellowPlane extends AbstractEnemy{
	private float xDistance;		//x distance from players position
	private float yDistance;		//y distance from players position
	private float magnitude;		//used to store magnitude of vector
	private float easingSpeed;		//used to move smoothly to the player position.
	public YellowPlane(Level level) {
		super(level);
		init();
	}

	public void init(){
		dimension.set(1, 1);
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.yellowEnemy.region;
		this.normalAnimation = Assets.instance.yellowEnemy.animationNormal;
		this.explosionAnimation = Assets.instance.yellowEnemy.animationExplosionBig;
		setAnimation(normalAnimation);
		velocity.x = 2.0f;
		velocity.y = 0.f;
		easingSpeed = .1f;
		score = 300;
		isBoss = false;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		//if we are not near the player do this normal behaviour(back and forth shooting)
		if(!nearPlayer()){
			//change offset and velocity depending on direction
			if (position.x<-3) {
				velocity.x = 2.0f;
			} else if (position.x>3) {
				velocity.x = -velocity.x;
			}
		}else{
			//enemy is near player so I want to kamikaze into the player.
			xDistance = level.player.position.x - position.x;
			yDistance = level.player.position.y - position.y;
			magnitude = (float) Math.sqrt(xDistance * xDistance + yDistance * yDistance);
			xDistance /= magnitude;
			yDistance /= magnitude;

			//move to calculated position.
			velocity.x += xDistance * easingSpeed;
			velocity.y += yDistance * easingSpeed;
		}
	}
}
