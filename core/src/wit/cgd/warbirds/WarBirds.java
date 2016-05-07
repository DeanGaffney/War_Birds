package wit.cgd.warbirds;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.screens.GameScreen;
import wit.cgd.warbirds.game.util.GamePreferences;

public class WarBirds extends Game {

	@Override
	public void create() {

		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());

		// Load preferences for audio settings 
		GamePreferences.instance.load();
		
		// TODO start playing music
		// AudioManager.instance.play(Assets.instance.music.song01);

		// TODO Start game at menu screen
		setScreen(new GameScreen(this));

	}

}