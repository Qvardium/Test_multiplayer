package com.qvardium.game.test_multiplayer;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements ForGooglePlayService
{

	final static String TAG = "Test_multi_game";

	private GSGameHelper _gameHelper;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_gameHelper = new GSGameHelper(this, GameHelper.CLIENT_GAMES);
		_gameHelper.enableDebugLog(false);

		GameHelper.GameHelperListener gameHelperListerner = new GameHelper.GameHelperListener() {

			@Override
			public void onSignInSucceeded() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSignInFailed() {
				// TODO Auto-generated method stub

			}
		};
		_gameHelper.setup(gameHelperListerner);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		initialize(new MyTestGame(this), config);
	}

	@Override
	public void SignIn() {
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Login Failed " + e.getMessage());
		}

	}

	@Override
	public void SignOut() {
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.signOut();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}

	}

	@Override
	public boolean isSignedIn() {
		return _gameHelper.isSignedIn();
	}

	@Override
	public void QuickGame(){
		try{
			runOnUiThread(new Runnable(){
				public void run(){
					_gameHelper.quickGame();
				}
			});
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}
	}

	@Override
	public void initMatch(){
		_gameHelper.initMatch();
	}

	@Override
	public void onStart(){
		super.onStart();
		_gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		_gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		_gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void setGame(MyTestGame game) {
		_gameHelper.setGame(game);
	}

	public void sendPos(float x, float y){
		_gameHelper.sendPos(x,y);
	}

	@Override
	public int getPlayers() {
		return _gameHelper.mParticipants.size();
	}

	@Override
	public String[] getIDs() {
		String[] ids = new String[_gameHelper.mParticipants.size()];

		for(int i=0;i<ids.length;i++){
			ids[i]=_gameHelper.mParticipants.get(i).getParticipantId();
		}

		return ids;
	}

	@Override
	public String getMyID() {
		return _gameHelper.mMyId;
	}

	@Override
	public void seeInovation() {
		_gameHelper.seeInov();
	}

	@Override
	public void asceptInv() {
		_gameHelper.asceptIn();
	}


}
