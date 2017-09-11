package com.qvardium.game.test_multiplayer.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Qvardium on 08.07.2017.
 */

public class Player {
    float x,y;
    int index;
    Color color;
    String myId;
    Texture texture;
    Pixmap pixmap;

    public Player(float xx,float yy, Color c, String id, int index){
        this.index=index;
        x=xx;
        y=yy;
        color=c;
        myId = id;
        pixmap = new Pixmap(100,100, Pixmap.Format.RGB888);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                pixmap.drawPixel(i,j,c.toIntBits());
            }
        }
        texture = new Texture(pixmap);
    }

    public void draw(SpriteBatch b){
        //b.setColor(color);
        b.draw(texture,x-50,y-50);
    }
    public void setPosition(float xxx, float yyy){
        x=xxx;
        y=yyy;
    }

    public String getID(){
        return myId;
    }

    public Texture getTexture(){
        return texture;
    }

    public int getIndex(){
        return index;
    }
}
