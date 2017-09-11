package com.qvardium.game.test_multiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Qvardium on 03.07.2017.
 */

public class GSGameHelper extends GameHelper implements RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener, OnInvitationReceivedListener {

    final static String TAG = "Multi_game";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;
    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;
    // My participant ID in the currently active game
    String mMyId = null;
    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;
    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];

    private Activity activity;
    private String mRoomID;
    private MyTestGame game;

    public GSGameHelper(Activity activity, int clientsToUse) {
        super(activity, clientsToUse);
        this.activity = activity;
        // TODO Auto-generated constructor stub
    }

    public void quickGame(){
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 3, 0);
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();
        Games.RealTimeMultiplayer.create(getApiClient(), roomConfig);

        // prevent screen from sleeping during handshake
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public void initMatch(){
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 3);
        this.activity.startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder((RoomUpdateListener) this)
                .setMessageReceivedListener((RealTimeMessageReceivedListener) this)
                .setRoomStatusUpdateListener((RoomStatusUpdateListener) this);
    }

    @Override
    public void onActivityResult(int request,int response, Intent data){
        super.onActivityResult(request, response, data);

        if (request == GSGameHelper.RC_INVITATION_INBOX){
            if (response != Activity.RESULT_OK) {
                return;
            }

            Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

            // accept invitation
            RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
            roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId())
                    .setMessageReceivedListener(this)
                    .setRoomStatusUpdateListener(this);
            Games.RealTimeMultiplayer.join(getApiClient(), roomConfigBuilder.build());
            //game.setScreen(new GameScreen(game));
        }
        else if (request == GSGameHelper.RC_WAITING_ROOM){
            if (response == Activity.RESULT_CANCELED || response == GamesActivityResultCodes.RESULT_LEFT_ROOM ){
                Games.RealTimeMultiplayer.leave(getApiClient(), this, mRoomID);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                BaseGameUtils.showAlert(activity, "Отмена");
            }else if(mParticipants.size()>=2){
                BaseGameUtils.showAlert(activity, "Создание игры");
                game.load_game = true;
            }

        }
        else if (request == GSGameHelper.RC_SELECT_PLAYERS){
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            Log.d(TAG, "Invitee count: " + invitees.size());

            // get the automatch criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
            }

            // create the room
            Log.d(TAG, "Creating room...");
            RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
            rtmConfigBuilder.addPlayersToInvite(invitees);
            rtmConfigBuilder.setMessageReceivedListener(this);
            rtmConfigBuilder.setRoomStatusUpdateListener(this);
            if (autoMatchCriteria != null) {
                rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            Games.RealTimeMultiplayer.create(getApiClient(), rtmConfigBuilder.build());

        }else{
            super.onActivityResult(request, response, data);
        }
    }

    //----------------------------------------------------------------------
    //-------------ВЫЗЫВАЕТСЯ ПРИ СОЕДИНЕНИИ ИГРОКА-------------------------
    //----------------------------------------------------------------------
    @Override
    public void onRoomCreated(int arg0, Room arg1) {
        //1111111111111111111111111111111111111111111111111
        BaseGameUtils.showAlert(activity, "onRoomCreated");
        if (arg0 != GamesStatusCodes.STATUS_OK) {
            BaseGameUtils.showAlert(activity, "Room creation error");
            BaseGameUtils.makeSimpleDialog(activity, "Error al crear la partida", "Room creation error").show();
            Gdx.app.log("R", "Room Created FAILED");
        }else{
            Gdx.app.log("R", "Room Created");
            mRoomID = arg1.getRoomId();
            final int MIN_PLAYERS = Integer.MAX_VALUE;
            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(getApiClient(), arg1, MIN_PLAYERS);

            this.activity.startActivityForResult(i, RC_WAITING_ROOM);
        }

    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        //222222222222222222222222222222222222222222222222222
        updateRoom(room);
        BaseGameUtils.showAlert(activity, "OnPeerJoined");
        for(int i=0;i<arg1.size();i++) game.addPlayer(arg1.get(i));
    }

    @Override
    public void onRoomConnecting(Room room) {
        // TODO Auto-generated method stub
        //33333333333333333333333333333333333333333333333333
        BaseGameUtils.showAlert(activity, "OnRoomConnecting");
        updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        // TODO Auto-generated method stub
        //444444444444444444444444444444444444444444444444444444
        BaseGameUtils.showAlert(activity, "onConnectedToRoom");
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(getApiClient()));
        Log.w(TAG, "Last connect room");

        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if(mRoomID==null)
            mRoomID = room.getRoomId();
    }

    @Override
    public void onPeersConnected(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        //55555555555555555555555555555555555555555555555555555555555
        BaseGameUtils.showAlert(activity, "onPeersConnected");
        updateRoom(room);


    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        // TODO Auto-generated method stub
        //6666666666666666666666666666666666666666666666666666666666666
        BaseGameUtils.showAlert(activity, "onRoomConnected");
        if (statusCode != GamesStatusCodes.STATUS_OK) {

            return;
        }
        if (room != null) {
            mParticipants = room.getParticipants();
            Log.w(TAG, "Room conected");
        }
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //-----------ВЫЗЫВАЕТСЯ КОГДА ИГРОК ПОКИДАЕТ КОМНАТУ--------------------
    //----------------------------------------------------------------------
    @Override
    public void onPeersDisconnected(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        //33333333333333333333333333333333333333333333333333333
        BaseGameUtils.showAlert(activity, "onPeersDisconnected");
        updateRoom(room);
        for(int i=0;i<arg1.size();i++) game.deletPlayer(arg1.get(i));
    }

    @Override
    public void onPeerLeft(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        //2222222222222222222222222222222222222222222222222222222
        updateRoom(room);
        BaseGameUtils.showAlert(activity, "onPeerLeft");

    }

    @Override
    public void onDisconnectedFromRoom(Room arg0) {
        // TODO Auto-generated method stub
        //111111111111111111111111111111111111111111111111111111
        mRoomID = null;
        BaseGameUtils.showAlert(activity, "onDisconectedFromRoom");
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @Override
    public void onJoinedRoom(int arg0, Room arg1) {
        BaseGameUtils.showAlert(activity, "onJoinedRoom");
        if (arg0 != GamesStatusCodes.STATUS_OK) {
            Gdx.app.log("R", "Joined FAILED");
        }else{
            Gdx.app.log("R", "Joined Room");
            mRoomID = arg1.getRoomId();
            final int MIN_PLAYERS = Integer.MAX_VALUE;
            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(getApiClient(), arg1, MIN_PLAYERS);

            this.activity.startActivityForResult(i, RC_WAITING_ROOM);
        }


    }

    @Override
    public void onLeftRoom(int arg0, String arg1) {
        BaseGameUtils.makeSimpleDialog(activity, "Вышел из комнаты");
        Gdx.app.log("LEAVE", "Me fui de la Room");
        BaseGameUtils.showAlert(activity, "onLeftRoom");

    }





    public void setGame(MyTestGame game){
        this.game = game;
    }


    public void sendPos(float x,float y){
        try{
            byte[] mensaje;
            byte a=1;
            mensaje = ByteBuffer.allocate(9).put(a).putFloat(x).putFloat(y).array();

            for (Participant p : mParticipants) {
                if (p.getParticipantId().equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                    // it's an interim score notification, so we can use unreliable
                Games.RealTimeMultiplayer.sendUnreliableMessage(getApiClient(), mensaje, mRoomID,
                            p.getParticipantId());
                }
        }catch(Exception e){

        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        float x, y;
        byte[] b = rtm.getMessageData();
        ByteBuffer bf = ByteBuffer.wrap(b);
        byte a = bf.get();
        if(a==1f) {
            x = bf.getFloat();
            y = bf.getFloat();
            game.updateGameWorld(x,y,rtm.getSenderParticipantId());
        }
//        else if(a==0f){
//            byte[] aa = rtm.getMessageData();
//            for(int i=1;i<aa.length;i++) aa[i]=bf.array()[i+1];
//            game.setString(new String(aa));
//        }
    }

    @Override
    public void onP2PConnected(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onP2PDisconnected(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        updateRoom(room);
        BaseGameUtils.showAlert(activity, "onPeerDeclined");
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        // TODO Auto-generated method stub
        updateRoom(room);
        BaseGameUtils.showAlert(activity, "onPeerInvitedToRoom");
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        // TODO Auto-generated method stub
        BaseGameUtils.showAlert(activity, "onRoomAutoMatching");
        updateRoom(room);
    }



    void updateRoom(Room room) {

        if (room != null) {
            mParticipants = room.getParticipants();
        }
    }

    public void asceptIn() {
        Log.d(TAG, "Accepting invitation: " + mIncomingInvitationId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(mIncomingInvitationId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        Games.RealTimeMultiplayer.join(getApiClient(), roomConfigBuilder.build());
        mIncomingInvitationId = null;

    }

    public void seeInov() {
        Intent intent = Games.Invitations.getInvitationInboxIntent(getApiClient());
        this.activity.startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        BaseGameUtils.showAlert(activity, "onInvitationReceived");
        mIncomingInvitationId = invitation.getInvitationId();
        game.setYou_invate(true);
    }

    @Override
    public void onInvitationRemoved(String s) {
        BaseGameUtils.showAlert(activity, "onInvitationRemoved");
        if (mIncomingInvitationId.equals(s)&&mIncomingInvitationId!=null) {
            mIncomingInvitationId = null;
            game.setYou_invate(false);
        }
    }

    public void sendText(String ss) {
        try{
            byte[] men = ss.getBytes();
            byte a = 0;
            byte[] mensaje = new byte[1+men.length];
            mensaje[0]=a;
            for(int i=1;i<mensaje.length;i++) mensaje[i]=men[i-1];

            for (Participant p : mParticipants) {
                if (p.getParticipantId().equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                // it's an interim score notification, so we can use unreliable
                Games.RealTimeMultiplayer.sendUnreliableMessage(getApiClient(), mensaje, mRoomID,
                        p.getParticipantId());
            }
        }catch(Exception e){

        }
    }
}
