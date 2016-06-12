package wit.cgd.warbirds.game.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameStats {
	public static final String TAG = GameStats.class.getName();

	public static final GameStats instance = new GameStats();
	private Preferences prefs;
	public int gameCount = 0;
	public int currentKillStreak = 0;
	public int longestKillStreak = 0;

	private GameStats () {
		prefs = Gdx.app.getPreferences(Constants.STATS);
	}

	public void load() { 
		gameCount = prefs.getInteger("gameCount", 0);
		currentKillStreak = prefs.getInteger("currentKillStreak", 0);
		longestKillStreak = prefs.getInteger("longestKillStreak", 0);
	}

	public void save() { 
		prefs.putInteger("gameCount", gameCount);
		prefs.putInteger("currentKillStreak", currentKillStreak);
		prefs.putInteger("longestKillStreak", longestKillStreak);
		prefs.flush();
	}    

	//called everytime you kill an enemy plane
	public void kill() { 
		currentKillStreak++;
		longestKillStreak++;
	}
	
	public void gamePlayed() { 
		gameCount++;
		currentKillStreak = 0;
	}

	public void reset() { 
		gameCount = 0;
		currentKillStreak = 0;
		longestKillStreak = 0;
	}
}
