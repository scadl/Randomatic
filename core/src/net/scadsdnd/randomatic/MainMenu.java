package net.scadsdnd.randomatic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenu extends ScreenAdapter {

    // UI basics
    // https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui
    // https://rskupnik.github.io/libgdx-ui-overview

    RandomaticMain randomaticGame;
    Skin mySkin;

    public MainMenu(RandomaticMain game){
        this.randomaticGame = game;
    }

    @Override
    public void show() {

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

        // Create special style for TextButton Widget
        // TextButtonStyle(Drawable up, Drawable down, Drawable checked, BitmapFont font)
        TextButton.TextButtonStyle txtBtnStyle =
                new TextButton.TextButtonStyle(
                mySkin.newDrawable("WhitTex", Color.DARK_GRAY),
                mySkin.newDrawable("WhitTex", Color.DARK_GRAY),
                mySkin.newDrawable("WhitTex", Color.BLUE),
                mySkin.getFont("myFont"));
        txtBtnStyle.over = mySkin.newDrawable("WhitTex", Color.LIGHT_GRAY);
        mySkin.add("myBtnStyle", txtBtnStyle);        // add style to skin

        // Construct a table - root skeleton of a ui
        Table myTable = new Table(mySkin);                  // Assign a skin to a table be able set props like bkg
        myTable.setFillParent(true);
        randomaticGame.myStage.addActor(myTable);

        // Add image filled with red with help of our empty texture
        Image myImg = new Image( new Texture(Gdx.files.internal("icon.png")) );
        myTable.background("WhitTex");                      // Assign our empty texture as skeleton background
        myTable.add(myImg).size(128).pad(35);
        myTable.row();

        Label.LabelStyle lblStyle = new Label.LabelStyle(randomaticGame.font, Color.BLACK);
        Label lblResult = new Label("RANDOMatic", lblStyle);
        myTable.add(lblResult).pad(15);
        myTable.row();

        // Finally crate a button widget and add it to table skeleton
        final TextButton btnZen = new TextButton("Zen Mode", mySkin, "myBtnStyle");
        final TextButton btnChallange = new TextButton("Challange", mySkin, "myBtnStyle");
        final TextButton btnExit = new TextButton("Exit", mySkin, "myBtnStyle");
        myTable.add(btnZen).width(300).pad(5);
        myTable.row();
        myTable.add(btnChallange).width(300).pad(5);
        myTable.row();
        myTable.add(btnExit).width(300).pad(15);

        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
        // revert the checked state.
        btnZen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("Zen mode start.");
                GameBoard ZenBoard = new GameBoard(randomaticGame);
                ZenBoard.isZen = true;
                randomaticGame.setScreen(ZenBoard);
            }
        });
        btnChallange.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameBoard ChaBoard = new GameBoard(randomaticGame);
                ChaBoard.isZen = false;
                randomaticGame.setScreen(ChaBoard);
            }
        });
        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
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
