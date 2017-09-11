package com.qvardium.game.test_multiplayer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.qvardium.game.test_multiplayer.ForGooglePlayService;
import com.qvardium.game.test_multiplayer.MyTestGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		ForGooglePlayService gs = new ForGooglePlayService() {
			@Override
			public void SignIn() {

			}

			@Override
			public void SignOut() {

			}

			@Override
			public boolean isSignedIn() {
				return true;
			}

			@Override
			public void QuickGame() {

			}

			@Override
			public void initMatch() {

			}

			@Override
			public void setGame(MyTestGame game) {

			}

			@Override
			public void sendPos(float x, float y) {

			}

			@Override
			public int getPlayers() {
				return 4;
			}

			@Override
			public String[] getIDs() {
				String [] se = {"1","2","3","4"};
				return se;
			}

			@Override
			public String getMyID() {
				return null;
			}

			@Override
			public void seeInovation() {

			}

			@Override
			public void asceptInv() {

			}

		};

		config.width=1280;
		config.height=768;

		new LwjglApplication(new MyTestGame(gs), config);
	}

}
