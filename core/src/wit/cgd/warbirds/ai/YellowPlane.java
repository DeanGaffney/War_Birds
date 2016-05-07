package wit.cgd.warbirds.ai;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.math.MathUtils;

public class YellowPlane extends AbstractEnemy{

	public YellowPlane(Level level) {
		super(level);
		init();
	}
	
	public void init(){
		dimension.set(0.6f, 0.6f);
		origin.set(dimension.x / 2, dimension.y / 2);
		region = Assets.instance.yellowEnemy.region;
		//TODO score,health,damage etc...
	}

	

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}

}
