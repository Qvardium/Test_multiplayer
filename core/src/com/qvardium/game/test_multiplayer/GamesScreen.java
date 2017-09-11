package com.qvardium.game.test_multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.qvardium.game.test_multiplayer.objects.Player;

/**
 * Created by Qvardium on 04.07.2017.
 */

public class GamesScreen extends ScreenAdapter {

    //---------for menu--------------------
    SpriteBatch batch;
    OrthographicCamera cam;
    Viewport viewport;
    ImageTextButton signIn, signOut, quickGame, selectGame, invatePl, seeInv;
    Stage stage;
    MyTestGame game;
    AssetManager assetManager;
    BitmapFont font;
    TextureAtlas forMenuTexture;
    //-------- for game--------------------
    String myID;
    Label label;

    final float scaleX,scaleY;

    public GamesScreen(MyTestGame gg){
        batch = gg.batch;
        cam = gg.cam;
        viewport = gg.viewport;
        game=gg;
        assetManager= gg.assetManager;
        font=gg.font;

        assetManager.load("ui_menu.pack",TextureAtlas.class);
        assetManager.finishLoading();

        forMenuTexture = assetManager.get("ui_menu.pack",TextureAtlas.class);

        //----------font----------------
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("timesbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.color= Color.GREEN;

        generator.scaleToFitSquare(1,1,2);

        font = generator.generateFont(parameter);
        generator.dispose();
        //--------------------------------

        //----подгружаем все необходимое для меню
        for_menu_load();

        scaleX = 1280/Gdx.graphics.getWidth();
        scaleY = 768/Gdx.graphics.getHeight();
    }

    void for_game_load() {
        String[] ids = game.googlePlayService.getIDs();

        Color[] colors = new Color[5];
        colors[0] = Color.OLIVE;
        colors[1] = Color.CORAL;
        colors[2] = Color.CYAN;
        colors[3] = Color.FIREBRICK;
        colors[4] = Color.FOREST;

        int ss=game.googlePlayService.getPlayers();
        myID = game.googlePlayService.getMyID();

        for(int ii=0;ii<ss;ii++){
            game.players.add(new Player(100*(ii+1),100,colors[ii],ids[ii],ii));
        }

        game.inGame=true;
        game.load_game=false;

        signIn.setVisible(false);
        signOut.setVisible(false);
        quickGame.setVisible(false);
        selectGame.setVisible(false);
        invatePl.setVisible(false);
        seeInv.setVisible(false);

        label = new Label("",new Label.LabelStyle(font,Color.GREEN));
        label.setPosition(50,680);
        stage.addActor(label);
    }

    void for_menu_load(){
        signIn = new ImageTextButton("Connect",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        signOut = new ImageTextButton("Dissconected",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        quickGame = new ImageTextButton("Quick Game",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        selectGame = new ImageTextButton("Select Game",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        invatePl = new ImageTextButton("Invate PL",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        seeInv = new ImageTextButton("See Invate",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        signIn.setPosition(100,500);
        signOut.setPosition(100,300);
        quickGame.setPosition(700,500);
        selectGame.setPosition(700,300);
        invatePl.setPosition(100,100);
        seeInv.setPosition(700,100);

        stage = new Stage(viewport, batch);

        //---режим быстрой игры работает некоректно... нужно будет исправить...--------------
        //---режим приглашения игрока не работает... нужно будет разобраться почему...-------
        stage.addActor(signIn);
        stage.addActor(signOut);
        //stage.addActor(quickGame);
        stage.addActor(selectGame);
        //stage.addActor(invatePl);
        //stage.addActor(seeInv);

        Gdx.input.setInputProcessor(stage);
        if(game.googlePlayService.isSignedIn()) signIn.setVisible(false);
        if(!game.googlePlayService.isSignedIn()) signOut.setVisible(false);
        if(!game.isYou_invate()) invatePl.setVisible(false);
    }

    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(game.load_game) for_game_load();

        if(game.inGame) for_game();
        else for_menu();

        cam.update();
        batch.setProjectionMatrix(cam.combined);

        stage.act(delta);
        stage.draw();
    }

    private void for_menu() {
        if(game.isYou_invate()) invatePl.setVisible(true);

        if(signIn.isPressed()){
            signIn.getClickListener().cancel();
            game.googlePlayService.SignIn();
            signOut.setVisible(true);
            signIn.setVisible(false);
        }
        else if(signOut.isPressed()){
            signOut.getClickListener().cancel();
            if(game.googlePlayService.isSignedIn()) game.googlePlayService.SignOut();
            signIn.setVisible(true);
            signOut.setVisible(false);
        }
        else if(quickGame.isPressed()){
            quickGame.getClickListener().cancel();
            if(game.googlePlayService.isSignedIn()) game.googlePlayService.QuickGame();

        }
        else if(selectGame.isPressed()){
            selectGame.getClickListener().cancel();
            if(game.googlePlayService.isSignedIn()) game.googlePlayService.initMatch();
            //game.load_game=true;
        }
        else if(invatePl.isPressed()){
            invatePl.getClickListener().cancel();
            if(game.googlePlayService.isSignedIn()) game.googlePlayService.asceptInv();
        }
        else if(seeInv.isPressed()){
            seeInv.getClickListener().cancel();
            if(game.googlePlayService.isSignedIn()) game.googlePlayService.seeInovation();
        }
    }

    private void for_game() {

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        if(Gdx.input.isTouched()){
            float x=Gdx.input.getX()*scaleX;
            float y=768-(Gdx.input.getY()*scaleY);
            for (Player p: game.players) {
                if(p.getID().equals(myID)){
                    p.setPosition(x,y);
                    game.googlePlayService.sendPos(x,y);
                }
            }
        }

        batch.begin();

        for(int i =0;i<game.players.size;i++){
            game.players.get(i).draw(batch);
        }

        batch.end();

        label.setText("X="+Gdx.input.getX()+
                "\nY="+Gdx.input.getY()+
                "\nW="+Gdx.graphics.getWidth()+" H="+Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {

    }



    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}
