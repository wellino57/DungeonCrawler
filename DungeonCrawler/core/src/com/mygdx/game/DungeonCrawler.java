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
import java.util.Timer;
import java.util.Vector;

public class DungeonCrawler extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img, playerSprite, tile, colors, floor, wall;
	TextureRegion region, path, blue, green, yellow, red, floorRegion, wallRegion;

	private Rectangle player;

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
		colors = new Texture(Gdx.files.internal("colors.png"));
		floor = new Texture(Gdx.files.internal("floor.jpg"));
		wall = new Texture(Gdx.files.internal("wall.jpg"));

		region = new TextureRegion(tile, 0, 0, 64, 64);
		path = new TextureRegion(playerSprite, 0, 0, 64, 64);
		floorRegion = new TextureRegion(floor, 0, 0, 64, 64);
		wallRegion = new TextureRegion(wall, 0, 0, 64, 64);

		blue = new TextureRegion(colors, 0, 0, 64, 64);
		green = new TextureRegion(colors, 64, 0, 64, 64);
		yellow = new TextureRegion(colors, 128, 0, 64, 64);
		red = new TextureRegion(colors, 196, 0, 64, 64);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		level = new Map();

		tileMap = new TiledMap();
		layers = tileMap.getLayers();
		layer = new TiledMapTileLayer(256, 256, 16, 16);
		collisionLayer = new TiledMapTileLayer(256, 256, 16, 16);
		mapRenderer = new OrthogonalTiledMapRenderer(tileMap);

		updateBoard();

		layers.add(layer);

		player = new Rectangle();
		player.x = 960 / 2;
		player.y = 540 / 2;
		player.width = 40;
		player.height = 40;

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

		if(Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			System.out.println("Mouse : "+touchPos.x/64+" "+touchPos.y/64);
			//player.x = touchPos.x - 64 / 2;
			//player.y = touchPos.y - 64 / 2;
			System.out.println(getNode((int)touchPos.x/64, (int)touchPos.y/64).value);
			System.out.println("Player : "+player.x/64+" "+player.y/64);
			tracePath(pathfinder((int)Math.floor(player.x/64), (int)Math.floor(player.y/64), (int)Math.floor(touchPos.x/64), (int)Math.floor(touchPos.y/64)));
			updateBoard();
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

		//if (Intersector.overlaps(player, object)) {
		//	collisionDetected = true;
		//}

		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);
				if (cell != null && cell.getTile().getTextureRegion() == wallRegion) {
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

	public Node getNode(int x, int y){
		return level.nodeMap[x][y];
	}

	private int calculateDistance(Node node1, Node node2){
		int xDistance = Math.abs(node1.x - node2.x);
		int yDistance = Math.abs(node1.y - node2.y);
		int remaining = xDistance + yDistance;
		return remaining;
	}

	private ArrayList<Node> getPath(Node end){
		ArrayList<Node> path = new ArrayList<>();
		path.add(end);
		Node current = end;

		while (current.cameFrom != null){
			System.out.println("Sciezka: "+current.x+" "+current.y);
			path.add(current.cameFrom);
			current.value = 3;
			current = current.cameFrom;
		}
		Collections.reverse(path);
		return path;
	}

	private void tracePath(ArrayList<Node> path) {
		if (path != null){
			Timer timer = new Timer();

			for (Node n : path){

				player.x = n.x*64;
				player.y = (15-n.y)*64;
			}
		}
	}

	public ArrayList<Node> pathfinder( int playerX, int playerY, int mouseX, int mouseY){
		ArrayList<Node> open = new ArrayList<>();
		ArrayList<Node> closed = new ArrayList<>();

		// Reset g, h, and f values for all nodes
		for (int row = 0; row < 16; row++) {
			for (int col = 0; col < 16; col++) {
				level.nodeMap[col][row].g = Integer.MAX_VALUE;
				level.nodeMap[col][row].h = 0;
				level.nodeMap[col][row].f = 0;
				level.nodeMap[col][row].cameFrom = null;
				level.nodeMap[col][row].start = false;
				level.nodeMap[col][row].end = false;
				if (level.nodeMap[col][row].value == 3) level.nodeMap[col][row].value = 0;
			}
		}

		// Create the start node and assign g, h, and f value
		Node startNode = getNode(playerX, playerY);
		startNode.start = true;
		startNode.g = 0;
		startNode.h = calculateDistance(startNode, getNode(mouseX, mouseY));
		startNode.f = startNode.g + startNode.h;
		startNode.value = 3;

		getNode(mouseX, mouseY).end = true;

		// Append start node to the open list
		open.add(startNode);

		while (!open.isEmpty()){
			// Get node with the lowest f score in the open list
			Node current = open.get(0);
			for (int i = 1; i < open.size(); i++){
				Node node = open.get(i);
				if (node.f < current.f){
					current = node;
				}
			}

			// Remove current from open list
			open.remove(current);

			// Add current to closed
			closed.add(current);

			// Check if current is the end node
			if (current.end){
				return getPath(current);
			}

			// Get neighbours list of the current node
			ArrayList<Node> neighbours = getNeighbours( current, closed);

			// For each neighbour of the current node
			for (Node neighbour : neighbours){
				int tentativeG = current.g + calculateDistance(current, neighbour);
				if (tentativeG < neighbour.g){
					neighbour.cameFrom = current;
					neighbour.g = tentativeG;
					neighbour.h = calculateDistance(neighbour, getNode(mouseX, mouseY));
					neighbour.f = neighbour.g + neighbour.h;

					if (!open.contains(neighbour)){
						open.add(neighbour);
					}
				}
			}
		}

		return null; // No path found
	}

	private ArrayList<Node> getNeighbours(Node node, ArrayList<Node> closed){
		ArrayList<Node> neighbours = new ArrayList<>();

		int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Only orthogonal moves

		for (int[] dir : directions){
			int col = node.x + dir[0];
			int row = 15 - node.y + dir[1];
			if ((0 <= col && col < 16) && (0 <= row && row < 16)){
				Node neighbour = getNode(col, row);
				if (!closed.contains(neighbour) && neighbour.value == 0){
					neighbours.add(neighbour);
				}
			}
		}

		return neighbours;
	}

	private void updateBoard(){
		for (int y=0;y<16;y++){
			for (int x=0;x<16;x++){
				TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

				switch (level.nodeMap[x][15-y].value){
					case 0:
						cell.setTile(new StaticTiledMapTile(floorRegion));
						break;
					case 1:
						cell.setTile(new StaticTiledMapTile(wallRegion));
						break;
					case 3:
						cell.setTile(new StaticTiledMapTile(blue));
						break;
				}

				layer.setCell(x*4, -y*4+60, cell);
			}
		}
	}
}
