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

	// Persistent storage files
	public static final String	PREFERENCES			= "game.prefs";

	// Speed Constants (most relative to SCROLL_SPEED)
	public static final float	SCROLL_SPEED		= 1.0f;

	public static final float	PLANE_H_SPEED		= 5.0f;
	public static final float	PLANE_MIN_V_SPEED	= -3 * SCROLL_SPEED;
	public static final float	PLANE_MAX_V_SPEED	= 4 * SCROLL_SPEED;

	public static final float	PLAYER_SHOOT_DELAY	= 0.2f;
	public static final float	BULLET_SPEED		= 2.0f * PLANE_MAX_V_SPEED;

	public static final float	BULLET_DIE_DELAY	= 1.2f;
	public static final float	ENEMY_DIE_DELAY		= 0.2f;
	public static final float	ENEMY_SHOOT_DELAY	= 1.f;
}
