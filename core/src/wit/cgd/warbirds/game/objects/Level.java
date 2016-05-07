package wit.cgd.warbirds.game.objects;

import java.util.ArrayList;

import wit.cgd.warbirds.ai.AbstractEnemy;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;

public class Level extends AbstractGameObject {

	public static final String	TAG		= Level.class.getName();

	public Player				player	= null;
	public LevelDecoration		levelDecoration;
	public float				start;
	public float				end;

	public final Array<AbstractEnemy> enemies = new Array<AbstractEnemy>();
	public final Array<Bullet> bullets = new Array<Bullet>();

	public final Pool<Bullet> bulletPool = new Pool<Bullet>() {
    	@Override
    	protected Bullet newObject() {
    		return new Bullet(level);
    	}
    };
    
    
	/**
	 * Simple class to store generic object in level.
	 */
	public static class LevelObject {
		String	name;
		int		x;
		int		y;
		float	rotation;
		int		state;
	}

	/**
	 * Collection of all objects in level
	 */
	public static class LevelMap {
		ArrayList<LevelObject>	islands;
		ArrayList<LevelObject>	enemies;
		String					name;
		float					length;
	}

	public Level() {
		super(null);
		init();

	}

	private void init() {

		// player
		player = new Player(this);
		player.position.set(0, 0);
		

		levelDecoration = new LevelDecoration(this);

		// read and parse level map (form a json file)
		String map = Gdx.files.internal("levels/level-01.json").readString();

		Json json = new Json();
		json.setElementType(LevelMap.class, "enemies", LevelObject.class);
		LevelMap data = new LevelMap();
		data = json.fromJson(LevelMap.class, map);
		
		Gdx.app.log(TAG, "Data name = " + data.name);
		Gdx.app.log(TAG, "islands . . . ");
		for (Object e : data.islands) {
			LevelObject p = (LevelObject) e;
			Gdx.app.log(TAG, "type = " + p.name + "\tx = " + p.x + "\ty =" + p.y);
			levelDecoration.addIsland(p.name, p.x, p.y, p.rotation);
		}
		Gdx.app.log(TAG, "enemies . . . ");
		for (Object e : data.enemies) {
			LevelObject p = (LevelObject) e;
			Gdx.app.log(TAG, "type = " + p.name + "\tx = " + p.x + "\ty =" + p.y);
			levelDecoration.addEnemy(p.name,p.x,p.y,p.rotation);
		}

		position.set(0, 0);
		velocity.y = Constants.SCROLL_SPEED;
		state = State.ACTIVE;
	}

	public void update(float deltaTime) {

		super.update(deltaTime);
		
		// limits for rendering
		start = position.y - scale.y * Constants.VIEWPORT_HEIGHT;
		end = position.y + scale.y * Constants.VIEWPORT_HEIGHT;

		player.update(deltaTime);
		
		for (Bullet bullet: bullets)
			bullet.update(deltaTime);
		
		levelDecoration.update(deltaTime); //this calls update on my enemies
	}

	public void render(SpriteBatch batch) {

		levelDecoration.render(batch);
		player.render(batch);
		for (Bullet bullet: bullets)
			bullet.render(batch);
		for (AbstractEnemy enemy: enemies)
			enemy.render(batch);
	
		//System.out.println("Bullets " + bullets.size);
	}

}
