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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;

public class DungeonCrawler extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture playerSprite;
	Texture tile;

	private Rectangle player;
	private Rectangle object;

	private OrthographicCamera camera;
	//private SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		playerSprite = new Texture(Gdx.files.internal("player.png"));
		tile = new Texture(Gdx.files.internal("tiles.png"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		player = new Rectangle();
		player.x = 800 / 2 - 64 / 2;
		player.y = 20;
		player.width = 64;
		player.height = 64;

		object = new Rectangle();
		object.x = 100;
		object.y = 100;
		object.width = 128;
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

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(playerSprite, player.x, player.y);
		batch.draw(tile,object.x,object.y);
		batch.end();

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

	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		playerSprite.dispose();
	}
}
