package com.qvardium.game.test_multiplayer;

/**
 * Created by Qvardium on 03.07.2017.
 */

public interface ForGooglePlayService {
    public void SignIn();
    public void SignOut();
    public boolean isSignedIn();
    public void QuickGame();
    public void initMatch();
    public void setGame(MyTestGame game);
    public void sendPos(float x, float y);
    public int getPlayers();
    public String[] getIDs();
    public String getMyID();
    public void seeInovation();
    public void asceptInv();

}
