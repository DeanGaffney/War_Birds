package wit.cgd.warbirds;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.screens.MenuScreen;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.GamePreferences;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class WarBirds extends Game {

	@Override
	public void create() {

		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());

		// Load preferences for audio settings 
		GamePreferences.instance.load();
		
		// start playing music
		AudioManager.instance.play(Assets.instance.music.menuSong);

		//Start game at menu screen
		setScreen(new MenuScreen(this));

	}

}