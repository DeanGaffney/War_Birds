package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.ai.AbstractEnemy;
import wit.cgd.warbirds.ai.Boss;
import wit.cgd.warbirds.ai.GreenPlane;
import wit.cgd.warbirds.ai.WhitePlane;
import wit.cgd.warbirds.ai.YellowPlane;
import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class LevelDecoration extends AbstractGameObject {

	private TextureRegion water;
	private TextureRegion shieldReg;
	private TextureRegion islandBig;
	private TextureRegion islandSmall;
	private TextureRegion islandTiny;

	public Array<Island> islands;
	public Array<AbstractEnemy> enemies;
	public Array<Shield> shields;

	private class Island extends AbstractGameObject {
		private TextureRegion region;

		public Island(Level level, TextureRegion region) {
			super(level);
			this.region = region;
		}

		public void render (SpriteBatch batch) {
			batch.draw(region.getTexture(), 
					position.x + origin.x, position.y + origin.y,
					origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, 
					region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
					false, false);
		}
	}

	//shield power ups.
	public class Shield extends AbstractGameObject {
		private TextureRegion region;
		public boolean collected;

		public Shield(Level level, TextureRegion region) {
			super(level);
			this.region = region;
			collected = false;
			init();
		}

		public void init(){
			dimension.set(1, 1);
			// Center image on game object
			origin.set(dimension.x / 2, dimension.y / 2);
			bounds.set(0, 0, dimension.x, dimension.y);
			hitBox.set(position.x,position.y,bounds.width,bounds.height);
		}
		
		@Override
		public void update(float deltaTime){
			bounds.set(0, 0, dimension.x, dimension.y);
			hitBox.set(position.x,position.y,bounds.width,bounds.height);
		}

		@Override
		public void render(SpriteBatch batch) {
			if(collected)return;

			batch.draw(region.getTexture(), 
					position.x + origin.x, position.y + origin.y,
					origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, 
					region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
					false, false);			
		}
	}

	public LevelDecoration (Level level) {
		super(level);
		init();
	}

	private void init () {
		dimension.set(1, 1);

		islandBig = Assets.instance.levelDecoration.islandBig;
		islandSmall = Assets.instance.levelDecoration.islandSmall;
		islandTiny = Assets.instance.levelDecoration.islandTiny;
		water = Assets.instance.levelDecoration.water;

		islands = new Array<Island>();
		shields = new Array<Shield>();
		enemies = new Array<AbstractEnemy>();
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion region = water;
		int t =  (int) (Constants.VIEWPORT_HEIGHT*scale.x/scale.y);

		// water
		for (int k=(int) level.start; k<level.end; ++k) 
			for (int c=-((int) Constants.VIEWPORT_WIDTH/2); c<(int) (Constants.VIEWPORT_WIDTH/2); ++c) batch.draw(region.getTexture(), 
					origin.x + position.x+c, origin.y + position.y+k, 
					origin.x, origin.y, 
					1.1f, 1.1f, 
					1, 1, rotation, 
					region.getRegionX(), region.getRegionY(),
					region.getRegionWidth(), region.getRegionHeight(), false, false);

		// islands
		for (Island island: islands) {
			region = island.region;
			if (island.position.y<level.start || island.position.y>level.end) continue;
			batch.draw(region.getTexture(), 
					island.position.x-island.origin.x, island.position.y-island.origin.y, 
					island.origin.x, island.origin.y, 
					1.1f, 1.1f, 
					1, 1, island.rotation, 
					region.getRegionX(), region.getRegionY(),
					region.getRegionWidth(), region.getRegionHeight(), false, false);
		}

		//render shields.
		for(Shield shield : shields){
			region = shield.region;
			if (shield.position.y<level.start || shield.position.y>level.end) continue;
			batch.draw(region.getTexture(), 
					shield.position.x-shield.origin.x, shield.position.y-shield.origin.y, 
					shield.origin.x, shield.origin.y, 
					1.1f, 1.1f, 
					1, 1, shield.rotation, 
					region.getRegionX(), region.getRegionY(),
					region.getRegionWidth(), region.getRegionHeight(), false, false);
		}

		//render enemies (controlled in the Abstract Enemy class)
		for(AbstractEnemy enemy:enemies){
			enemy.render(batch);
		}
	}

	public void update(float deltaTime){
		for(AbstractEnemy enemy:enemies)
			enemy.update(deltaTime);
		
		for(Shield shield : shields)
			shield.update(deltaTime);
	}

	public void addIsland(String name, float x, float y, float rotation) {
		Island island = null;
		if (name.equals("islandBig")) {
			island = new Island(level, islandBig);
		} else if (name.equals("islandSmall")) {
			island = new Island(level, islandSmall);
		} else if (name.equals("islandTiny")) {
			island = new Island(level, islandTiny);
		}
		island.origin.x = island.dimension.x/2; 
		island.origin.y = island.dimension.y/2; 
		island.position.set(x,y);
		island.rotation = rotation;
		islands.add(island);
	}

	public void addEnemy(String name, float x, float y, float rotation) {
		AbstractEnemy enemy = null;
		if (name.equals("green")) {
			enemy = new GreenPlane(level);
		} else if (name.equals("white")) {
			enemy = new WhitePlane(level);
		} else if (name.equals("yellow")) {
			enemy = new YellowPlane(level);
		}else if (name.equals("boss")){
			enemy = new Boss(level);
		}
		enemy.origin.x = enemy.dimension.x/2; 
		enemy.origin.y = enemy.dimension.y/2; 
		enemy.position.set(x,y);
		enemy.rotation = rotation;
		enemies.add(enemy);
	}

	//add shield
	public void addShield(String name, float x, float y, float rotation) {
		Shield shield = null;
		if (name.equals("shield")) {
			shield = new Shield(level, Assets.instance.shield.region);
		} 
		shield.origin.x = shield.dimension.x/2; 
		shield.origin.y = shield.dimension.y/2; 
		shield.position.set(x,y);
		shield.rotation = rotation;
		shields.add(shield);
	}
}
