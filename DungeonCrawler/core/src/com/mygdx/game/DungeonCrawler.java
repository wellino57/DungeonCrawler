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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

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

	private float previousX, previousY;
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
					layer.setCell(x*4, -y*4+60, cell);
				}
			}
		}
		layers.add(layer);

		player = new Rectangle();
		player.x = 960 / 2;
		player.y = 540 / 2;
		player.width = 60;
		player.height = 60;

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

		previousX = player.x;
		previousY = player.y;

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

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			System.out.println(touchPos.x/64+" "+touchPos.y/64);
			//player.x = touchPos.x - 64 / 2;
			//player.y = touchPos.y - 64 / 2;
			tracePath(findPath(touchPos.x, touchPos.y));
			System.out.println(getNode(touchPos.x, touchPos.y).val);
		}

		checkCollision();

		//System.out.println(player.x+" "+player.y);

	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		playerSprite.dispose();
	}

	private void checkCollision() {
		boolean collisionDetected = false;

		if (Intersector.overlaps(player, object)) {
			collisionDetected = true;
		}

		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle tileRect = new Rectangle(x * layer.getTileWidth(), y * layer.getTileHeight(), layer.getTileWidth()*4, layer.getTileHeight()*4);
					if (Intersector.overlaps(player, tileRect)) {
						collisionDetected = true;
						break;
					}
				}
			}
			if (collisionDetected) break;
		}

		if (collisionDetected) {
			handleCollision();
		}
	}

	private void handleCollision() {
		player.x = previousX;
		player.y = previousY;
	}

	public ArrayList<Node> findPath (double mx, double my){
		ArrayList<Node> open = new ArrayList<>();
		ArrayList<Node> closed = new ArrayList<>();

		Node startTile = getNode(player.x, player.y);
		startTile.g = 0;
		startTile.h = pathLenght(startTile, getNode(mx, my));
		startTile.f = startTile.g + startTile.h;
		open.add(startTile);

		getNode(mx, my).goal = true;

		while(!open.isEmpty()){
			Node current = open.get(0);
			for(Node n : open){
				if (n.f < current.f) current = n;
			}

			open.remove(current);
			closed.add(current);

			if (current.goal) return getPath(current);

			int[][] dirs = new int[][]{{-1,0},{1,0},{0,-1},{0,1}};
			ArrayList<Node> neighbours= new ArrayList<>();
			for(int[] dir : dirs){
				Node neighbour = getNode(current.x+dir[0], current.y+dir[1]);
				if (!closed.contains(neighbour) && neighbour.val == 0){
					neighbours.add(neighbour);

					for(Node neigh : neighbours){
						int possibleG = current.g + pathLenght(current, neigh);
						if (possibleG < neigh.g){
							neigh.precursor = current;
							neigh.g = possibleG;
							neigh.h = pathLenght(neigh, getNode(mx,my));
							neigh.f = neigh.g + neigh.h;

							if (!open.contains(neigh)){
								open.add(neigh);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public int pathLenght(Node startNode, Node endNode){
		return Math.abs(startNode.x - endNode.x) + Math.abs(startNode.y - endNode.y);
	}

	public Node getNode(double x, double y){
		int mapX = (int)Math.floor(x/64);
		int mapY = (int)Math.floor(y/64);
		return Map.nodeMap[mapX][mapY];
	}

	public ArrayList<Node> getPath(Node n){
		ArrayList<Node> path = new ArrayList<>();
		while(n.precursor != null){
			path.add(n);
			n = n.precursor;
		}
		Collections.reverse(path);
		return path;
	}

	public void tracePath(ArrayList<Node> path){
		if (path != null){
			for(Node n : path){
				player.x = n.x;
				player.y = n.y;
			}
		}
	}

}
