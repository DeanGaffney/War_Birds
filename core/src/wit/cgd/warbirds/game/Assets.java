package wit.cgd.warbirds.game;

import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {

	public static final String	TAG			= Assets.class.getName();
	public static final Assets	instance	= new Assets();

	private AssetManager		assetManager;

	public AssetFonts			fonts;

	public AssetSounds			sounds;
	public AssetMusic			music;

	public AssetPlayer			player;
	public AssetEnemy			boss;
	public AssetEnemy			greenEnemy;
	public AssetEnemy			whiteEnemy;
	public AssetEnemy			yellowEnemy;

	public Asset				bullet;
	public Asset				doubleBullet;
	public Asset				shield;
	public AssetLevel			levels;				//this loads the level indicator images
	
	public AssetLevelDecoration	levelDecoration;

	private Assets() {}

	public void init(AssetManager assetManager) {

		this.assetManager = assetManager;
		assetManager.setErrorListener(this);

		// load texture for game sprites
		assetManager.load(Constants.TEXTURE_ATLAS_GAME, TextureAtlas.class);
		assetManager.finishLoading();


		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		// create atlas for game sprites
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_GAME);
		for (Texture t : atlas.getTextures())
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		fonts = new AssetFonts();

		// create game resource objects
		player = new AssetPlayer(atlas);
		boss = new AssetEnemy(atlas, "boss");
		greenEnemy = new AssetEnemy(atlas,"green");
		whiteEnemy = new AssetEnemy(atlas,"white");
		yellowEnemy = new AssetEnemy(atlas,"yellow");
		levelDecoration = new AssetLevelDecoration(atlas);
		bullet = new Asset(atlas, "bullet");
		doubleBullet  = new Asset(atlas, "bullet_double");
		levels = new AssetLevel(atlas,"wave");
		shield = new Asset(atlas,"shield");
		

		// load sounds
		assetManager.load("sounds/ui_click.wav", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/boss_defeated.wav", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/enemy_shoot.wav", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/enemy_explosion.wav", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/player_explosion.wav", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/player_shoot.mp3", Sound.class);
		assetManager.finishLoading();
		assetManager.load("sounds/player_hit.wav", Sound.class);
		assetManager.finishLoading();

		//load music
		assetManager.load("music/menu_music.mp3", Music.class);
		assetManager.finishLoading();
		assetManager.load("music/game_music.mp3", Music.class);
		assetManager.finishLoading();

		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset + "'", (Exception) throwable);
	}

	public class Asset {
		public final AtlasRegion	region;

		public Asset(TextureAtlas atlas, String imageName) {
			region = atlas.findRegion(imageName);
			Gdx.app.log(TAG, "Loaded asset '" + imageName + "'");
			System.out.println(region);
		}
	}
	
	/*
	 * for some reason when I loaded these level indicators as normal Assets (the inner class above)
	 * they were always null. So I decided I would create an array of the level indicator regions ("waves.png)
	 * and then just pick the correect index according to the level. This seems to work.
	 */
	public class AssetLevel {
		public final Array<AtlasRegion>	regions;

		public AssetLevel(TextureAtlas atlas, String imageName) {
			regions = atlas.findRegions(imageName);
			Gdx.app.log(TAG, "Loaded asset '" + imageName + "'");
			System.out.println(regions);
		}
	}

	public class AssetPlayer {
		public final AtlasRegion	region;
		public final Animation		animationNormal;
		public final Animation		animationExplosionBig;

		public AssetPlayer(TextureAtlas atlas) {
			region = atlas.findRegion("player");

			Array<AtlasRegion> regions = atlas.findRegions("player");
			animationNormal = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
			regions = atlas.findRegions("explosion_big");
			animationExplosionBig = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
		}
	}

	public class AssetEnemy {
		public final AtlasRegion	region;
		public final Animation		animationNormal;
		public final Animation		animationExplosionBig;

		public AssetEnemy(TextureAtlas atlas,String enemyType){
			region = atlas.findRegion("enemy_plane_"+enemyType); 
			String explosionType = (enemyType.equals("boss")) ? "explosion_big" : "explosion_large";
			Array<AtlasRegion> regions = atlas.findRegions("enemy_plane_"+enemyType);
			animationNormal = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
			regions = atlas.findRegions(explosionType);
			animationExplosionBig = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
		}
	}

	public class AssetLevelDecoration {

		public final AtlasRegion	islandBig;
		public final AtlasRegion	islandSmall;
		public final AtlasRegion	islandTiny;
		public final AtlasRegion	water;

		public AssetLevelDecoration(TextureAtlas atlas) {
			water = atlas.findRegion("water");
			islandBig = atlas.findRegion("island_big");
			islandSmall = atlas.findRegion("island_small");
			islandTiny = atlas.findRegion("island_tiny");
		}
	}

	public class AssetFonts {
		public final BitmapFont	defaultSmall;
		public final BitmapFont	defaultNormal;
		public final BitmapFont	defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's built-in 15px bitmap font
			defaultSmall = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(2.5f);
			defaultBig.getData().setScale(4.0f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public class AssetSounds {

		// TODO list reference to sound assets
		// public final Sound first;
		public final Sound  click;
		public final Sound  enemyShoot;
		public final Sound  enemyExplosion;
		public final Sound  playerExplosion;
		public final Sound  playerShoot;
		public final Sound  playerHit;
		public final Sound  bossDefeated;

		public AssetSounds(AssetManager am) {
			click = am.get("sounds/ui_click.wav", Sound.class);
			enemyShoot = am.get("sounds/enemy_shoot.wav", Sound.class);
			enemyExplosion = am.get("sounds/enemy_explosion.wav", Sound.class);
			playerExplosion = am.get("sounds/player_explosion.wav", Sound.class);
			playerShoot = am.get("sounds/player_shoot.mp3", Sound.class);
			playerHit = am.get("sounds/player_hit.wav", Sound.class);
			bossDefeated = am.get("sounds/boss_defeated.wav", Sound.class);
		}

	}

	public class AssetMusic {

		public final Music menuSong;
		public final Music gameSong;

		public AssetMusic(AssetManager am) {
			menuSong = am.get("music/menu_music.mp3", Music.class);
			gameSong = am.get("music/game_music.mp3",Music.class);
		}
	}

}
