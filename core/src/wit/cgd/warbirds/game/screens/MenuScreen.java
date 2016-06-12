package wit.cgd.warbirds.game.screens;


import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GamePreferences;
import wit.cgd.warbirds.game.util.GameStats;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen extends AbstractGameScreen {

	@SuppressWarnings("unused")
	private static final String TAG = MenuScreen.class.getName();
	private Stage               stage;

	// MenuScreen widgets
	private Button              playButton;
	private Button              optionsButton;
	private Button              resetStatsButton;
	private Label               gameCountLabel;
	private Label               currentStreakLabel;
	private Label               longestStreakLabel;

	// options window widgets
	private Table               optionsWindowLayer;
	private Window              optionsWindow;

	private Button              optionsSaveButton;
	private Button              optionsCancelButton;

	private Label               enemyShootSpeedLabel;
	private Slider              enemyShootSpeedSlider;

	private CheckBox            soundCheckBox;
	private Slider              soundSlider;
	private CheckBox            musicCheckBox;
	private Slider              musicSlider;

	// debug
	private final float         DEBUG_REBUILD_INTERVAL  = 5.0f;
	private boolean             debugEnabled            = false;
	private float               debugRebuildStage;

	public MenuScreen(Game game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
		//Table.drawDebug(stage);
	}

	private void rebuildStage() {

		skin = new Skin(Gdx.files.internal(Constants.SKIN_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

		// build all layers
		stack.add(buildBackgroundLayer());
		stack.add(buildStatsLayer());
		stack.add(buildControlsLayer());

		optionsWindowLayer = buildOptionsWindowLayer();
		stage.addActor(optionsWindowLayer);
		stage.addActor(optionsWindowLayer);
	}

	private Table buildBackgroundLayer() {
		Table table = new Table();
		table.add(new Image(skin, "background"));
		return table;
	}

	private Table buildStatsLayer() {
		Table table = new Table();
		table.left().top();

		gameCountLabel = new Label("Number of games played: "+ GameStats.instance.gameCount, skin);
		table.add(gameCountLabel).left();
		table.row();
		currentStreakLabel = new Label("Length of current kill streak: " + GameStats.instance.currentKillStreak, skin);
		table.add(currentStreakLabel).left();
		table.row();
		longestStreakLabel = new Label("Longest kill streak: " + GameStats.instance.longestKillStreak, skin);
		table.add(longestStreakLabel).left();

		table.row();
		resetStatsButton = new Button(skin, "reset");
		table.add(resetStatsButton).pad(Constants.BUTTON_PAD);
		resetStatsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onResetStatsClicked();
			}
		});

		if (debugEnabled) table.debug();
		return table;
	}

	private Table buildControlsLayer() {
		Table table = new Table();
		table.right().bottom();

		playButton = new Button(skin, "play");
		table.add(playButton).pad(Constants.BUTTON_PAD);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		table.row();

		optionsButton = new Button(skin, "options");
		table.add(optionsButton).pad(Constants.BUTTON_PAD);
		optionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		return table;
	}

	private void onPlayClicked() { 
		game.setScreen(new GameScreen(game));
		AudioManager.instance.play(Assets.instance.music.gameSong);
	}

	private void onOptionsClicked() { 
		playButton.setVisible(false);
		optionsButton.setVisible(false);
		resetStatsButton.setVisible(false);
		optionsWindow.setVisible(true);
		loadSettings();   
	}

	private void onResetStatsClicked(){
		GameStats.instance.reset();
		rebuildStage();
	}

	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		
		// set each widget using values in prefs
		soundCheckBox.setChecked(prefs.sound);
		soundSlider.setValue(prefs.soundVolume);
		musicCheckBox.setChecked(prefs.music);
		musicSlider.setValue(prefs.musicVolume);
		enemyShootSpeedSlider.setValue(prefs.enemyShootSpeed);
		
	}

	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		// save each widget value into prefs
		prefs.sound = soundCheckBox.isChecked();
		prefs.soundVolume = soundSlider.getValue();
		prefs.music = musicCheckBox.isChecked();
		prefs.musicVolume = musicSlider.getValue();
		prefs.enemyShootSpeed = enemyShootSpeedSlider.getValue();
		System.out.println(prefs.enemyShootSpeed);
		prefs.save();
	}

	private void onSaveClicked() {
		saveSettings();
		onCancelClicked();
		AudioManager.instance.onSettingsUpdated();
	}

	private void onCancelClicked() {
		playButton.setVisible(true);
		optionsButton.setVisible(true);
		resetStatsButton.setVisible(true);
		optionsWindow.setVisible(false);
		AudioManager.instance.onSettingsUpdated();
		loadSettings();   
	}

	private Table buildOptionsWindowLayer() {

		// create instance of window
		optionsWindow = new Window("Options", defaultSkin);

		//enemy settings
		optionsWindow.add(new Label("Enemy",defaultSkin)).colspan(3);
		optionsWindow.row();
		
		//allow the enemy shooting speed to be adjusted
		enemyShootSpeedLabel = new Label("Shooting Speed:",defaultSkin);
		optionsWindow.add(enemyShootSpeedLabel);
		enemyShootSpeedSlider = new Slider(0.5f,3.0f,0.5f,false, defaultSkin);
		optionsWindow.add(enemyShootSpeedSlider);
		optionsWindow.row().padBottom(10);

		// sound settings
		optionsWindow.add(new Label("Sound Effects",defaultSkin)).colspan(3);
		optionsWindow.row();
		soundCheckBox = new CheckBox("On", defaultSkin);
		optionsWindow.add(soundCheckBox);
		soundCheckBox.addListener(new ChangeListener() {
		    @Override
		    public void changed (ChangeEvent event, Actor actor) {
		        CheckBox me = (CheckBox) actor;
		        soundSlider.setDisabled(!me.isChecked());
		    }
		  });
		soundSlider = new Slider(0.0f,1.0f,0.1f,false, defaultSkin);
		optionsWindow.add(soundSlider).colspan(2);
		optionsWindow.row().padBottom(10);


		// music settings
		optionsWindow.add(new Label("Music",defaultSkin)).colspan(3);
		optionsWindow.row();
		musicCheckBox = new CheckBox("On", defaultSkin);
		optionsWindow.add(musicCheckBox);
		musicCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				CheckBox me = (CheckBox) actor;
				musicSlider.setDisabled(!me.isChecked());
			}
		});
		musicSlider = new Slider(0.0f,1.0f,0.1f,false, defaultSkin);
		optionsWindow.add(musicSlider).colspan(2);
		optionsWindow.row().padBottom(20);


		// cancel and save buttons 
		optionsCancelButton = new Button(skin, "cancel");
		optionsWindow.add(optionsCancelButton).pad(Constants.BUTTON_PAD);
		optionsCancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		optionsSaveButton = new Button(skin, "save");
		optionsWindow.add(optionsSaveButton).pad(Constants.BUTTON_PAD);;
		optionsSaveButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});


		// tidy up window = resize and center
		optionsWindow.setColor(1, 1, 1, 0.8f);
		optionsWindow.setVisible(false);
		if (debugEnabled) optionsWindow.debug();
		optionsWindow.pack();
		optionsWindow.setPosition((Constants.VIEWPORT_GUI_WIDTH - optionsWindow.getWidth())/2,
				(Constants.VIEWPORT_GUI_HEIGHT - optionsWindow.getHeight())/2);

		// return constructed window
		return optionsWindow;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update((int)Constants.VIEWPORT_GUI_WIDTH, (int)Constants.VIEWPORT_GUI_HEIGHT, false);
	}

	@Override
	public void hide() {
		stage.dispose();
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}
	@Override public void pause() {}
}
