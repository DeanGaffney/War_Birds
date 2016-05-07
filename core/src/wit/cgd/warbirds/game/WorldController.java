package wit.cgd.warbirds.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.util.CameraHelper;
import wit.cgd.warbirds.game.util.Constants;

public class WorldController extends InputAdapter {

	private static final String	TAG	= WorldController.class.getName();

	private Game				game;
	public CameraHelper			cameraHelper;
	public Level				level;

	public WorldController(Game game) {
		this.game = game;
		init();
	}

	private void init() {
		Gdx.input.setInputProcessor(this);
		level = new Level();
		cameraHelper = new CameraHelper();
		cameraHelper.setTarget(level);
	}


	public void update(float deltaTime) {
		handleDebugInput(deltaTime);
		handleGameInput(deltaTime);
		cameraHelper.update(deltaTime);
		level.update(deltaTime);
		cullObjects();
		checkBulletEnemyCollision();
		checkEnemyBulletPlayerCollision();
		checkEnemyPlayerCollision();
	}

	/**
	 * Remove object because they are out of screen bounds or because they have died
	 */
	public void cullObjects() {
		
		// cull bullets 
		for (int k=level.bullets.size; --k>=0; ) { 	// traverse array backwards !!!
			Bullet it = level.bullets.get(k);
			if (it.state == Bullet.State.DEAD) {
				level.bullets.removeIndex(k);
				level.bulletPool.free(it);
			} else if (it.state==Bullet.State.ACTIVE && !isInScreen(it)) {
				it.state = Bullet.State.DYING;
				it.timeToDie = Constants.BULLET_DIE_DELAY;
			}
		}
		
		// TODO cull enemies
	}

	// Collision detection methods
	public void checkBulletEnemyCollision() {}
	public void checkEnemyBulletPlayerCollision() {}
	public void checkEnemyPlayerCollision() {}
	

	public boolean isInScreen(AbstractGameObject obj) {
		return ((obj.position.x>=-Constants.VIEWPORT_WIDTH/2 && obj.position.x<=Constants.VIEWPORT_WIDTH/2)
				&&
				(obj.position.y>=level.start && obj.position.y<=level.end));
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			Gdx.app.exit();
		}
		return false;
	}

	private void handleGameInput(float deltaTime) {

		if (Gdx.input.isKeyPressed(Keys.A)) {
			level.player.velocity.x = -Constants.PLANE_H_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.D)) {
			level.player.velocity.x = Constants.PLANE_H_SPEED;
		} else {
			level.player.velocity.x = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			level.player.velocity.y = Constants.PLANE_MAX_V_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.S)) {
			level.player.velocity.y = Constants.PLANE_MIN_V_SPEED;
		} else {
			level.player.velocity.y = Constants.SCROLL_SPEED;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			level.player.shoot();
		}
	}

	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		if (Gdx.input.isKeyPressed(Keys.ENTER)) {
			cameraHelper.setTarget(!cameraHelper.hasTarget() ? level : null);
		}

		if (!cameraHelper.hasTarget()) {
			// Camera Controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.reset();
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		// TODO - implement touch pad type controls
		return true;
	}

}
