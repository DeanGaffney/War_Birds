package wit.cgd.warbirds.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import wit.cgd.warbirds.game.util.Constants;

public class WorldRenderer implements Disposable {

	private static final String	TAG	= WorldRenderer.class.getName();

	public OrthographicCamera	camera;
	public OrthographicCamera	cameraGUI;

	private SpriteBatch			batch;
	private WorldController		worldController;
	private float				guiDisplayTime;
	private boolean				renderMessage;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
		guiDisplayTime = 3;
		renderMessage = true;
	}

	public void resize(int width, int height) {

		float scale = (float)height/(float)width;
		camera.viewportHeight = scale * Constants.VIEWPORT_HEIGHT;
		camera.update();
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = scale*Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();

		// update level decoration
		worldController.level.levelDecoration.scale.y =  scale;
	}

	public void render() {

		// Game rendering
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();

		batch.begin();
		// GUI + HUD rendering 

		batch.end();

		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// TODO
		renderGui(batch);
		batch.end();
	}

	//calls all gui rendering
	public void renderGui(SpriteBatch batch) {
		//render player health
		renderHealth(batch);

		//render remaining enemies
		renderRemainingEnemies(batch);

		//render boss messages
		renderBossMessages(batch);

		//renders level indicators
		renderLevelGui(batch);

		//render shield powerup.
		renderShield(batch);

		//renders game over
		renderGameOver(batch);
		
		//render player score
		renderScore(batch);

	}

	//displays current health for the player.
	private void renderHealth(SpriteBatch batch){
		float x = (Constants.VIEWPORT_GUI_WIDTH - Constants.VIEWPORT_GUI_WIDTH) + 150;
		float y = Constants.VIEWPORT_GUI_HEIGHT - 50;
		int health = (int) worldController.level.player.health;
		BitmapFont fontHealth = Assets.instance.fonts.defaultBig;
		if(worldController.level.player.health > 40){	//change from green to red depending on health
			fontHealth.setColor(0f, 1f, 0f, 1);
		}else{
			fontHealth.setColor(1f, 0f, 0f, 1);
		}
		fontHealth.draw(batch, "Health: "+health, x, y, 0, Align.center,true);
	}
	
	//renders players score on screen
	private void renderScore(SpriteBatch batch){
		float x = cameraGUI.viewportWidth - 160;
		float y = cameraGUI.viewportHeight - cameraGUI.viewportHeight + 20;
		int score = worldController.level.player.score;
		BitmapFont scoreFont = Assets.instance.fonts.defaultNormal;
		scoreFont.setColor(152f,	245f	,255f, 1);
		scoreFont.draw(batch, "Score: "+score, x, y, 0, Align.center,true);
		scoreFont.setColor(152f,	245f	,255f, 1);
	}

	//renders the remaining enemies in the level.
	private void renderRemainingEnemies(SpriteBatch batch){
		float x = Constants.VIEWPORT_GUI_WIDTH * 2.4f;
		float y = Constants.VIEWPORT_GUI_HEIGHT - 50;
		int enemies = (int) worldController.level.levelDecoration.enemies.size;
		BitmapFont enemyFont = Assets.instance.fonts.defaultBig;
		enemyFont.setColor(1, 0f, 0f, 1);
		enemyFont.draw(batch, "Enemies: "+enemies, x, y, 0, Align.center,true);
	}

	//render boss messages
	private void renderBossMessages(SpriteBatch batch){
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		BitmapFont bossFont = Assets.instance.fonts.defaultBig;
		bossFont.setColor(255, 94, 0, 1);

		//if one enemy is left its the boss,else if its 0 the boss must be defeated.
		if(worldController.level.levelDecoration.enemies.size == 1 && renderMessage){
			guiDisplayTime -= Gdx.graphics.getDeltaTime();
			if(guiDisplayTime <= 0){
				renderMessage = false;
				guiDisplayTime = 3;
			}else{
				bossFont.draw(batch, "Boss Incoming!", x, y,0,Align.center,true);
			}
		}

		if(worldController.level.levelDecoration.enemies.size == 0 && !worldController.isGameOver()){
			guiDisplayTime -=Gdx.graphics.getDeltaTime();
			if(guiDisplayTime <= 0){
				renderMessage = true;		//turn back on for next level.
				guiDisplayTime = 3;
			}else{
				bossFont.draw(batch, "Boss Defeated!\nLevel Complete", x, y,0,Align.center,true);
			}
		}
	}

	//renders shield power up with timer if player is using shield power up.
	private void renderShield(SpriteBatch batch){
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight - 80;
		float timeLeftShieldPowerup = worldController.level.player.invincibleTime;

		//draw shield on screen, if player has shield make it solid colour,else make it faded.
		if(worldController.level.player.hasShield){
			batch.draw(Assets.instance.shield.region, x, y, 50, 50, 100, 100, .7f, -.5f, 0);

		}else{
			batch.setColor(1, 1, 1, 0.5f);
			batch.draw(Assets.instance.shield.region, x, y, 50, 50, 100, 100, .7f, -.5f, 0);

			batch.setColor(1, 1, 1, 1);
		}

		//do fade in and out effect when timer is running out.
		if (timeLeftShieldPowerup > 0 && !worldController.level.player.hasShield) {
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftShieldPowerup < 4) {
				if (((int) (timeLeftShieldPowerup * 5) % 2) != 0) {
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.shield.region, x, y, 50, 50, 100, 100, .7f, -.5f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultNormal.draw(batch, "" + (int) timeLeftShieldPowerup, x + 100, y + 50);
		}
	}

	//renders game over.
	public void renderGameOver(SpriteBatch batch){
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		BitmapFont gameOver = Assets.instance.fonts.defaultBig;
		gameOver.setColor(255, 94, 0, 1);

		//if one enemy is left its the boss,else if its 0 the boss must be defeated.
		if(worldController.isGameOver()){
			if(worldController.currentLevel == Constants.MAX_LEVEL){
				gameOver.draw(batch, "GAME OVER!\n YOU WIN!!", x, y,0,Align.center,true);
			}else{
				gameOver.draw(batch, "GAME OVER!\n YOU LOSE!!", x, y,0,Align.center,true);
			}
		}
	}

	//renders the gui to indicate what level it is.
	private void renderLevelGui(SpriteBatch batch){
		float x = Constants.VIEWPORT_WIDTH / 2;
		float y = Constants.VIEWPORT_HEIGHT/ 2;
		TextureRegion levelRegion = null;
		switch(worldController.currentLevel){
		case 1:
			levelRegion = Assets.instance.levels.regions.get(0);
			break;
		case 2:
			levelRegion = Assets.instance.levels.regions.get(1);;
			break;
		case 3:
			levelRegion = Assets.instance.levels.regions.get(2);;
			break;
		case 4:
			levelRegion = Assets.instance.levels.regions.get(3);;
			break;
		case 5:
			levelRegion = Assets.instance.levels.regions.get(4);
			break;
		}
		batch.draw(levelRegion, x, y, 50, 50, 100, 100, 1.5f, -1f, 0);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
