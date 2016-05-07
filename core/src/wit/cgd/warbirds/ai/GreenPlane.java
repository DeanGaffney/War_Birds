package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class GreenPlane extends AbstractEnemy{

	public float offset;
	public GreenPlane(Level level) {
		super(level);
		init();
	}

	public void init(){
		dimension.set(0.6f, 0.6f);
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.greenEnemy.region;
		velocity.x = 1.0f;
		animation = Assets.instance.greenEnemy.animationNormal;
		offset = 10.f;
		timeShootDelay = 0;
		//TODO score,health,damage etc...
	}
	
	

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		//calculate correct rotation
		rotation = MathUtils.atan2(velocity.y, velocity.x) - offset;
		
		//movement
		System.out.println("Green plane Movement: " + position);
		if(state != State.ACTIVE) return;

		velocity.y = -.5f;
		if (position.x<-3) {
			velocity.x = .5f;
			offset = -10.f;
		} else if (position.x>3) {
			velocity.x = -.5f;
			offset = 10.f;
		}
	}
	
}
