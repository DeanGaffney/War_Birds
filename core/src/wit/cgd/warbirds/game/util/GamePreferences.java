package wit.cgd.warbirds.game.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class GamePreferences {

	public static final String          TAG         = GamePreferences.class.getName();

    public static final GamePreferences instance    = new GamePreferences();
    private Preferences                 prefs;
    public boolean						sound;
    public float						soundVolume;
    public boolean						music;
    public float						musicVolume;
    public float 						enemyShootSpeed;		//used to adjust the speed between enemies firing bullets.
    
    private GamePreferences() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load() {
    	enemyShootSpeed = MathUtils.clamp(prefs.getFloat("enemyShootSpeed",0), 0.5f,3.0f);
    	sound = prefs.getBoolean("sound");
    	soundVolume = MathUtils.clamp(prefs.getFloat("soundVolume",0),0,1);
    	music = prefs.getBoolean("music");
    	musicVolume = MathUtils.clamp(prefs.getFloat("musicVolume",0),0,1);
    }

    public void save() {
		prefs.putFloat("enemyShootSpeed", enemyShootSpeed);
    	prefs.putBoolean("sound", sound);
		prefs.putFloat("soundVolume", soundVolume);
    	prefs.putBoolean("music", music);
		prefs.putFloat("musicVolume", musicVolume);
        prefs.flush();
    }
    
}
