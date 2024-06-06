package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;

public class DungeonCrawler extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture playerSprite;
	Texture tile;
	TextureRegion region;

	private Rectangle player;
	private Rectangle object;

	Map level;
	TiledMap tileMap;
	MapLayers layers;
	TiledMapTileLayer layer;
	TiledMapTileLayer collisionLayer;
	TiledMapRenderer mapRenderer;

	private OrthographicCamera camera;
	//private SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		playerSprite = new Texture(Gdx.files.internal("player.png"));
		tile = new Texture(Gdx.files.internal("tiles.png"));
		region = new TextureRegion(tile, 0, 0, 64, 64);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		level = new Map();

		tileMap = new TiledMap();
		layers = tileMap.getLayers();
		layer = new TiledMapTileLayer(256, 256, 16, 16);
		collisionLayer = new TiledMapTileLayer(256, 256, 16, 16);
		mapRenderer = new OrthogonalTiledMapRenderer(tileMap);

		for (int y=0;y<16;y++){
			for (int x=0;x<16;x++){
				if(level.map[y][x]==1){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

					cell.setTile(new StaticTiledMapTile(region));
					layer.setCell(x*4, y*4, cell);
				}
			}
		}
		layers.add(layer);

		player = new Rectangle();
		player.x = 960 / 2;
		player.y = 540 / 2;
		player.width = 64;
		player.height = 64;

		object = new Rectangle();
		object.x = 100;
		object.y = 100;
		object.width = 64;
		object.height = 64;

	}

	@Override
	public void render () {
		ScreenUtils.clear(0.8f, 0.8f, 0.8f, 0);

		//camera.position.set(player.getX(),player.getY(),0);

		float lerp = 5;
		Vector3 position = this.camera.position;
		position.x += (player.x - position.x) * lerp * Gdx.graphics.getDeltaTime();
		position.y += (player.y - position.y) * lerp * Gdx.graphics.getDeltaTime();

		mapRenderer.setView(camera);
		mapRenderer.render();
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(playerSprite, player.x, player.y);
		batch.draw(tile,object.x,object.y);

		batch.end();

		if(!checkCollision()){
			if(Gdx.input.isKeyPressed(Input.Keys.W)&& Gdx.input.isKeyPressed(Input.Keys.D)){
				player.x -= 60 * Gdx.graphics.getDeltaTime();
				player.y -= 60 * Gdx.graphics.getDeltaTime();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.W)&& Gdx.input.isKeyPressed(Input.Keys.A)){
				player.x += 60 * Gdx.graphics.getDeltaTime();
				player.y -= 60 * Gdx.graphics.getDeltaTime();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.S)&& Gdx.input.isKeyPressed(Input.Keys.D)){
				player.x -= 60 * Gdx.graphics.getDeltaTime();
				player.y += 60 * Gdx.graphics.getDeltaTime();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.S)&& Gdx.input.isKeyPressed(Input.Keys.A)){
				player.x += 60 * Gdx.graphics.getDeltaTime();
				player.y += 60 * Gdx.graphics.getDeltaTime();
			}
			if(Gdx.input.isKeyPressed(Input.Keys.W)) player.y += 200 * Gdx.graphics.getDeltaTime();
			if(Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= 200 * Gdx.graphics.getDeltaTime();
			if(Gdx.input.isKeyPressed(Input.Keys.D)) player.x += 200 * Gdx.graphics.getDeltaTime();
			if(Gdx.input.isKeyPressed(Input.Keys.A)) player.x -= 200 * Gdx.graphics.getDeltaTime();
		}else{
			System.out.println("colliding");
		}

	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		playerSprite.dispose();
	}

	public boolean checkCollision () {
		if (player.overlaps(object)) return true;
		else return false;
	}
}
/*
private boolean isCellBlocked(float x, float y) {
	Cell cell = null;
	boolean blocked = false;

	try {
		cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
	} catch (Exception e) {
		e.printStackTrace();
	}

	if (cell != null && cell.getTile() != null) {
		if (cell.getTile().getProperties().containsKey("blocked")) {
			blocked = true;
		}
	}
	return blocked;
}
*/