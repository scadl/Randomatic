package net.scadsdnd.randomatic;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.I18NBundle;

import javax.xml.bind.annotation.XmlType;

public class RandomaticMain extends Game {

	// How to use multiple scenes
	//https://happycoding.io/tutorials/libgdx/game-screens

	// Global engine variables, empty
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;




	// Global text output variables, empty
	SpriteBatch batch;
	BitmapFont font;
	Stage myStage;


	I18NBundle langStrs;

	// Create game field
	@Override
	public void create()
	{

		FileHandle baseFileLoc = Gdx.files.internal("libgdx_locale/randLoc");
		langStrs = I18NBundle.createBundle(baseFileLoc);

		// Add new Font with unicode support
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.characters =  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзиклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789][_!$%#@|\\\\/?-+=()*&.;,{}\\\"´`'<>";
		param.size = 15;
		param.incremental = true;

		//Prepare the output
		batch = new SpriteBatch();				// Init the Text Drawing engine
		font = generator.generateFont(param);	// Init simple text font
		shapeRenderer = new ShapeRenderer();	// Init the Shapes Drawing engine
		camera = new OrthographicCamera();		// Create an camera
		myStage = new Stage();					// Scene for UI

		generator.dispose();

		// Antialiasing for font layer
		font.getRegion().getTexture().setFilter(
				Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font.getData().setScale(3, 3);			// Set text scale

		setScreen(new MainMenu(this));
	}

	// Called when renderers destroyed
	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
		batch.dispose();
		font.dispose();
	}


}
