package com.qvardium.game.test_multiplayer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.qvardium.game.test_multiplayer.objects.Player;

public class MyTestGame extends Game {

	SpriteBatch batch;
	OrthographicCamera cam;
	Viewport viewport;
	AssetManager assetManager;
	BitmapFont font;
	boolean inGame;
	boolean load_game;

	public static ForGooglePlayService googlePlayService;
	Array<Player> players;


	public boolean isYou_invate() {
		return you_invate;
	}

	public void setYou_invate(boolean you_invate) {
		this.you_invate = you_invate;
	}

	boolean you_invate;


	public MyTestGame(ForGooglePlayService gs){

		googlePlayService=gs;
		gs.setGame(this);
		players = new Array<Player>();
		assetManager=new AssetManager();

	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		viewport = new FitViewport(1280,768,cam);
		you_invate = false;
		inGame=false;
		load_game=false;
		setScreen(new GamesScreen(this));
	}

	public void updateGameWorld(float x, float y, String id) {

		for (int i=0;i<players.size;i++) {
			if(players.get(i).getID().equals(id)){
				players.get(i).setPosition(x,y);
			}
		}
	}

	public void addPlayer(String id){

		players.add(new Player(100,100, Color.GREEN,id,players.size));

	}

	public void deletPlayer(String id){
		for(int i=0;i<players.size;i++){
			if(players.get(i).getID().equals(id)) {
				players.get(i).getTexture().dispose();
				players.removeIndex(i);
			}
		}
	}

}
