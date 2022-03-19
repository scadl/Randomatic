package net.scadsdnd.randomatic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ResultScreen extends ScreenAdapter {

    Skin mySkin;
    RandomaticMain randomaticGame;
    int hitsCount = 0;
    int missCount = 0;
    int limitCount = 0;
    boolean isWin = true;

    public ResultScreen(RandomaticMain game){
        this.randomaticGame = game;
    }

    @Override
    public void show() {

        // Clear a ui layer
        randomaticGame.myStage.clear();

        Gdx.input.setInputProcessor(randomaticGame.myStage);

        // Programmatically create a skin - collection of ui properties
        mySkin = new Skin();

        // Programmatically a Texture
        Pixmap myPixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        myPixmap.setColor(Color.WHITE);
        myPixmap.fill();

        // Store our empty texture to skin
        mySkin.add("WhitTex", new Texture(myPixmap));

        // Attach global font to current skin
        mySkin.add("myFont", randomaticGame.font);

        // Construct a table - root skeleton of a ui
        Table myTable = new Table(mySkin);
        myTable.setFillParent(true);
        myTable.background("WhitTex");
        randomaticGame.myStage.addActor(myTable);

        // Handle final Labels
        Color lblColor = Color.RED;
        String lblText = "You loose!\nTry again!";
        if(isWin){
            lblColor = Color.GREEN;
            lblText = "You win!\nCongrats!";
        }
        Label.LabelStyle lblStyle = new Label.LabelStyle(randomaticGame.font, lblColor);
        Label lblResult = new Label(lblText, lblStyle);
        myTable.add(lblResult);

        // Handle a Back key in game code
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE){
                    randomaticGame.setScreen(new MainMenu(randomaticGame));
                }
                return true;
            }
        });

        //super.show();
    }

    @Override
    public void render(float delta) {
        //ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        randomaticGame.myStage.act( Math.min( Gdx.graphics.getDeltaTime(), 1 / 30f ) );
        randomaticGame.myStage.draw();
        //super.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        randomaticGame.myStage.getViewport().update(width, height, true);
        //super.resize(width, height);
    }
}
