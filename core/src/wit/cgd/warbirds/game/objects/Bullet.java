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
	public boolean enemyBullet;	//used to make bullets enemy or player bullets.True if enemy fired bullet.
	public float angle;
	Bullet(Level level) {
		super(level);
		init();
	}
	
	public void init() {
		dimension.set(0.5f, 0.5f);
				
		region = Assets.instance.doubleBullet.region;

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		bounds.set(0, 0, dimension.x, dimension.y);
		hitBox.set(position.x,position.y,bounds.width,bounds.height);
		enemyBullet = false;
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
		enemyBullet = false;
		state = State.ACTIVE;
		
	}
}
