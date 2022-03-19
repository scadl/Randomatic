package net.scadsdnd.randomatic;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

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

	// Create game field
	@Override
	public void create()
	{
		//Prepare the output
		batch = new SpriteBatch();				// Init the Text Drawing engine
		font = new BitmapFont();				// Init simple text font
		shapeRenderer = new ShapeRenderer();	// Init the Shapes Drawing engine
		camera = new OrthographicCamera();		// Create an camera
		myStage = new Stage();					// Scene for UI

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
