package wit.cgd.warbirds.game;


import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import wit.cgd.warbirds.ai.AbstractEnemy;
import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.LevelDecoration.Shield;
import wit.cgd.warbirds.game.screens.MenuScreen;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.CameraHelper;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GameStats;

public class WorldController extends InputAdapter {

	private static final String	TAG	= WorldController.class.getName();

	private Game				game;
	public CameraHelper			cameraHelper;
	public Level				level;
	public int					currentLevel = 1;
	public float 				timeLeftGameOverDelay;

	public WorldController(Game game) {
		this.game = game;
		init();
	}

	private void init() {
		Gdx.input.setInputProcessor(this);

		cameraHelper = new CameraHelper();
		initLevel(currentLevel);	//placed here to centralize the camera on level load.
		cameraHelper.setTarget(level);
		timeLeftGameOverDelay = 5;
	}

	//all collisions handled in their respective classes(AbstractEnemy class and Player class)
	public void update(float deltaTime) {
		handleDebugInput(deltaTime);	//handle debug
		if (isGameOver()) {				//check game over
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0) init();	//if it is restart game
		} else {
			handleGameInput(deltaTime);				//if its not handle the input to game.
		}
		level.update(deltaTime);					//update level
		cameraHelper.update(deltaTime);				//update camera
		if (!isGameOver() && isLevelWon() && currentLevel  + 1 <= Constants.MAX_LEVEL) {		//if not over but the level is,go to new level
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0){
				currentLevel++;	
				initLevel(currentLevel);
				if(!AudioManager.instance.playingMusic.isPlaying()) AudioManager.instance.playingMusic.play();
			}
		}

		if(isGameOver()) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0) {
				currentLevel = 1;
				GameStats.instance.gamePlayed();
				backToMenu();
			}
		}

		cullObjects();
	}

	//loads a level according to the current level number.
	private void initLevel(int currentLevel) {
		switch(currentLevel){
		case 1:
			level = new Level(Constants.LEVEL_01);
			break;
		case 2:
			level = new Level(Constants.LEVEL_02);
			break;
		case 3:
			level = new Level(Constants.LEVEL_03);
			break;
		case 4:
			level = new Level(Constants.LEVEL_04);
			break;
		case 5:
			level = new Level(Constants.LEVEL_05);
			break;
		}
		cameraHelper.setTarget(level);
		timeLeftGameOverDelay = 5;
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

		//cull enemies
		for(int i = 0; i < level.levelDecoration.enemies.size; i++){
			AbstractEnemy it = level.levelDecoration.enemies.get(i);
			if(it.state == AbstractEnemy.State.DEAD){
				level.levelDecoration.enemies.removeIndex(i);
			}else if(it.state == AbstractEnemy.State.ACTIVE && !isInScreen(it)){
				it.state = AbstractEnemy.State.DYING;
				it.timeToDie = Constants.DIE_DELAY;
			}
		}
		
		//cull shields
		for(int i = 0; i < level.levelDecoration.shields.size; i++){
			Shield it = level.levelDecoration.shields.get(i);
			if(it.state == Shield.State.DEAD){
				level.levelDecoration.shields.removeIndex(i);
			}else if(it.state == Shield.State.ACTIVE && !isInScreen(it)){
				it.state = Shield.State.DYING;
				it.timeToDie = Constants.DIE_DELAY;
			}
		}
	}

	public boolean isInScreen(AbstractGameObject obj) {
		return ((obj.position.x>=-Constants.VIEWPORT_WIDTH/2 && obj.position.x<=Constants.VIEWPORT_WIDTH/2)
				&&
				(obj.position.y>=level.start && obj.position.y<=level.end));
	}



	//allows you to reset the game world,toggle camera follow on/off and switch levels according to numberpad.
	@Override
	public boolean keyUp(int keycode) {

		switch (keycode) { // Reset game world
		
		case Keys.ENTER:
			// Toggle camera follow
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.player);
			//Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
			break;
		case Keys.NUM_1:
			currentLevel = 1;
			Gdx.app.debug(TAG, "Level 1 selected ");
			init();
			break;
		case Keys.NUM_2:
			currentLevel = 2;
			Gdx.app.debug(TAG, "Level 2 selected ");
			init();
			break;
		case Keys.NUM_3:
			currentLevel = 3;
			Gdx.app.debug(TAG, "Level 3 selected ");
			init();
			break;
		case Keys.NUM_4:
			currentLevel = 4;
			Gdx.app.debug(TAG, "Level 4 selected ");
			init();
			break;
		case Keys.NUM_5:
			currentLevel = 5;
			Gdx.app.debug(TAG, "Level 5 selected ");
			init();
			break;
		case Keys.ESCAPE:
			Gdx.app.exit();
			break;
		case Keys.BACK:
			Gdx.app.exit();
			break;
		case Keys.R:
			if(level.player.hasShield){
				level.player.activateShield();
			}
			
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
			level.player.shoot(true);
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

	//checks if a level is won.
	public boolean isLevelWon(){
		//if all enemies are still alive the game isnt over.
		return level.levelDecoration.enemies.size <= 0;
	}

	//sees if the game is over.
	public boolean isGameOver() {
		//game is over if the player has died
		if(level.player.isDead()) return true;

		//if we are on the last level and this level has just been won then we won the game.
		if(currentLevel == Constants.MAX_LEVEL && isLevelWon())return true;

		return false;
	}
	
	private void backToMenu() {
		// switch to menu screen
		game.setScreen(new MenuScreen(game));
	}
}
