package wit.cgd.warbirds.game.util;

public class Constants {

	// Game world dimensions
	public static final float	VIEWPORT_WIDTH		= 8.0f;
	public static final float	VIEWPORT_HEIGHT		= 8.0f;

	// GUI dimensions
	public static final float	VIEWPORT_GUI_WIDTH	= 480.0f;
	public static final float	VIEWPORT_GUI_HEIGHT	= 800.0f;

	// atlas for all game sprites
	public static final String	TEXTURE_ATLAS_GAME	= "images/game.atlas";
	
	//levels in the game
	public static final String LEVEL_01					= "levels/level-01.json";
	public static final String LEVEL_02					= "levels/level-02.json";
    public static final String LEVEL_03					= "levels/level-03.json";
    public static final String LEVEL_04					= "levels/level-04.json";
    public static final String LEVEL_05					= "levels/level-05.json";
    public static final int	   MAX_LEVEL				= 5;



	// Speed Constants (most relative to SCROLL_SPEED)
	public static final float	SCROLL_SPEED		= 1.0f;

	public static final float	PLANE_H_SPEED		= 5.0f;
	public static final float	PLANE_MIN_V_SPEED	= -3 * SCROLL_SPEED;
	public static final float	PLANE_MAX_V_SPEED	= 4 * SCROLL_SPEED;

	public static final float	PLAYER_SHOOT_DELAY	= 0.2f;
	public static final float	BULLET_SPEED		= 2.0f * PLANE_MAX_V_SPEED;

	public static final float	BULLET_DIE_DELAY	= 1.2f;
	public static final float	DIE_DELAY			= 0.2f;
	public static final float	ENEMY_SHOOT_DELAY	= 1.f;
	
	public static final float   DAMAGE_DELAY		= 0.5f;
	public static final float	INVINCIBLE_TIME		= 7.0f;

	// location of game specific skin and atlas
	public static final String  SKIN_UI                 = "images/ui.json";
	public static final String  TEXTURE_ATLAS_UI        = "images/ui.atlas";

	// location of libgdx default skin and atlas
	public static final String  SKIN_LIBGDX_UI          = "images/uiskin.json";
	public static final String  TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";

	public static final int BUTTON_PAD      = 5;


	// Game setting (preferences + stats) files
	public static final String STATS = "game.stats";
	// Persistent storage files
	public static final String	PREFERENCES			= "game.prefs";

}
