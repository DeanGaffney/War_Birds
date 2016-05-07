package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;


public class Bullet extends AbstractGameObject implements Poolable {

	public static final String TAG = Player.class.getName();
	
	private TextureRegion region;
	
	Bullet(Level level) {
		super(level);
		init();
	}
	
	public void init() {
		dimension.set(0.5f, 0.5f);
				
		region = Assets.instance.doubleBullet.region;

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		
		velocity.y = Constants.BULLET_SPEED;
	}
	

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
				dimension.x, dimension.y, scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);		
	}

	@Override
	public void reset() {
		System.out.println("sdfsd");
		state = State.ACTIVE;
	}
}
