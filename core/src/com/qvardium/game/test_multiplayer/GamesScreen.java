package com.qvardium.game.test_multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    ImageTextButton send;
    TextField tf;
    Label label;

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

        for_menu_load();

        stage = new Stage(viewport, batch);


        stage.addActor(signIn);
        stage.addActor(signOut);
        stage.addActor(quickGame);
        stage.addActor(selectGame);
        stage.addActor(invatePl);
        stage.addActor(seeInv);

        Gdx.input.setInputProcessor(stage);
        if(game.googlePlayService.isSignedIn()) signIn.setVisible(false);
        if(!game.googlePlayService.isSignedIn()) signOut.setVisible(false);
        if(!game.isYou_invate()) invatePl.setVisible(false);
        
        for_game_load();
    }

    void for_game_load() {
        String[] ids = game.googlePlayService.getIDs();

        Color[] colors = new Color[10];
        colors[0] = Color.OLIVE;
        colors[1] = Color.CORAL;
        colors[2] = Color.CYAN;
        colors[3] = Color.FIREBRICK;
        colors[4] = Color.FOREST;
        colors[5] = Color.GOLD;
        colors[6] = Color.GREEN;
        colors[7] = Color.ORANGE;
        colors[8] = Color.RED;
        colors[9] = Color.SKY;

        int ss=game.googlePlayService.getPlayers();
        for(int ii=0;ii<ss;ii++){
            game.players.add(new Player(100*(ii+1),100,colors[ii],ids[ii]));
        }

        myID = game.googlePlayService.getMyID();

        send = new ImageTextButton("Send",
                new ImageTextButton.ImageTextButtonStyle
                        (new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button_p")),
                                new TextureRegionDrawable(forMenuTexture.findRegion("button")),
                                font));
        send.setPosition(800,600);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font;
        tfs.fontColor = Color.BLACK;

        tfs.selection = new TextureRegionDrawable(forMenuTexture.findRegion("tfSelection"));
        tfs.background = new TextureRegionDrawable(forMenuTexture.findRegion("tfbackground"));
        tfs.cursor = new TextureRegionDrawable(forMenuTexture.findRegion("cursor"));
        tf = new TextField("", tfs);
        tf.setMessageText("Enter words...");
        tf.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped (TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });
        tf.setPosition(50,650);
        tf.setWidth(700);

        label = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(),
                new Label.LabelStyle(font, font.getColor()));
        label.setPosition(50,100);

        stage.addActor(tf);
        stage.addActor(send);
        stage.addActor(label);
        
        tf.setVisible(false);
        send.setVisible(false);
        label.setVisible(false);

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
    }
    
    
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            //game.setScreen(new GameScreen(game));
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
            for(int i =0;i<game.players.size;i++){
                if(game.players.get(i).myId.hashCode()==myID.hashCode()){
                    game.players.get(i).setPosition(Gdx.input.getX(),Gdx.input.getY());
                    game.googlePlayService.sendPos(Gdx.input.getX(),Gdx.input.getY());
                }
            }
        }

        if(send.isPressed()){
            send.getClickListener().cancel();
            game.googlePlayService.sendText(tf.getText());
            game.setString(tf.getText());
        }

        label.setText(game.getString());

        batch.begin();

        for(Player p: game.players){
            p.draw(batch);
        }

        batch.end();
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
