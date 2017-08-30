package com.qvardium.game.test_multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Qvardium on 03.07.2017.
 */

public class GameScreen extends ScreenAdapter {

    MyTestGame mg;
    SpriteBatch batch;
    OrthographicCamera cam;
    Viewport viewport;
    BitmapFont font;
    String myID;
    Stage stage;
    ImageTextButton send;
    TextField tf;
    Label label;
    AssetManager assetManager;

    public GameScreen(MyTestGame myTestGame) {
        mg=myTestGame;
        batch = mg.batch;
        cam = mg.cam;
        viewport = mg.viewport;
        assetManager = myTestGame.assetManager;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("timesbd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.color= Color.GREEN;

        generator.scaleToFitSquare(1,1,2);

        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();


        cam.update();
        batch.setProjectionMatrix(cam.combined);

        String[] ids = mg.googlePlayService.getIDs();

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

        int ss=mg.googlePlayService.getPlayers();
        for(int ii=0;ii<ss;ii++){
            mg.players.add(new Player(100*(ii+1),100,colors[ii],ids[ii]));
        }

        myID = mg.googlePlayService.getMyID();



        stage = new Stage(viewport,batch);

        TextureAtlas forMenuTexture = assetManager.get("ui_menu.pack",TextureAtlas.class);

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

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f,0.8f ,0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        if(Gdx.input.isTouched()){
            for(int i =0;i<mg.players.size;i++){
                if(mg.players.get(i).myId.hashCode()==myID.hashCode()){
                    mg.players.get(i).setPosition(Gdx.input.getX(),Gdx.input.getY());
                    mg.googlePlayService.sendPos(Gdx.input.getX(),Gdx.input.getY());
                }
            }
        }

        if(send.isPressed()){
            send.getClickListener().cancel();
            mg.googlePlayService.sendText(tf.getText());
            mg.setString(tf.getText());
        }

        label.setText(mg.getString());

        batch.begin();

        for(Player p: mg.players){
            p.draw(batch);
        }

        batch.end();

        cam.update();
        batch.setProjectionMatrix(cam.combined);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }

}
