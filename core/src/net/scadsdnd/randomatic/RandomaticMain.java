package net.scadsdnd.randomatic;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

public class RandomaticMain extends ApplicationAdapter {

	// Global engine variables, empty
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;

	// Global game variables, empty
	int balls;
	Coordinator[] coord;
	float[][] colmx;
	int score;
	float[] hit;

	// Global text output variables, empty
	SpriteBatch batch;
	BitmapFont font;

	// Prepare the game objects
	public void Recolor(){

		balls = (int) Math.round( (float)Math.random() * 100 );		// Set amount of balls, randomly
		coord = new Coordinator[balls];								// Create array for Balls objects
		colmx = new float[balls][4];								// Create array for their props

		for (int c=0; c<balls; c++){
			coord[c] = new Coordinator(camera);						// Fill the array of Balls with actual objects
		}

		for (int i=0; i<balls; i++){								// Loop through all new balls
			for (int j=0; j<4; j++){								// Loop the props of each of them

				// Fill each prop with random values in specified safe range
				colmx[i][j] = (float) Math.round( (Math.random()*0.65f+0.3f) *100) / 100.00f ;
			}
		}

	}

	// Create game field
	@Override
	public void create()
	{
		score = 0;								// Score to zero
		camera = new OrthographicCamera();		// Create an camera
		configureCamera();						// Call the camera adjastment method
		shapeRenderer = new ShapeRenderer();	// Init the Shapes Drawing engine

		Recolor();								// Populate the field

		//Prepare the output
		batch = new SpriteBatch();				// Init the Text Drawing engine
		font = new BitmapFont();				// Init simple text font
		hit = new float[2];						// Create the array for th last hit coords
	}

	// Render a final frame.
	// Called each screen update.
	@Override
	public void render()
	{
		// Set the background - white opaque
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Allow the use of transparency for the shapes.
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.update();										// Update the camera
		shapeRenderer.setProjectionMatrix(camera.combined);		// Set simple matrix for coords
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);	// Set the shape fill and start drawing engine

		Vector3 cleanHit = new Vector3(0,0,0);		// Create storage for hit coords

		// -----------------------------------
		// Gdx.input origin is top-left corner
		// getX\getY is in screen coordinates

		// Do this if user touched\clicked game board
		if (Gdx.input.justTouched()) {
			// camera.unproject is a converter from screen coordinates to world.
			cleanHit = camera.unproject(
					new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			// Store or Update the converted click coordinate for continual usage
			hit[0] = cleanHit.x;
			hit[1] = cleanHit.y;
		}

		// loop thorough the Balls array
		for (int c=0; c<balls; c++){
			// Check if read Ball destroyed previously (by player)
			if (coord[c]!=null) {

				// -----------------------------------------
				// shapeRenderer origin is lower-left corner

				// Set the new ball color, from prepared randomized props array
				shapeRenderer.setColor(colmx[c][0], colmx[c][1], colmx[c][2], colmx[c][3]);
				// Draw the new circle with updated\new coords+alpha and set color
				shapeRenderer.circle(coord[c].x, coord[c].y, coord[c].r);

				// Create and initalize a Circle object fot hit test
				Circle myC = new Circle(coord[c].x, coord[c].y, coord[c].r);

				coord[c].Move();			// Call Ball's update coords method
				coord[c].BorderCheck();		// Call Ball's screen border check

				// Perform intersection test of user hit (touch) coordinates
				// with fantom circle, duplicating visible (drawn) circle.
				if (myC.contains(cleanHit.x, cleanHit.y)) {
					score += 1;			// hit detected > +1 to score
					coord[c] = null;	// hit detected > destroy the ball
				}
				myC = null; 			// Destroy fantom (test) circle
			}
		}

		// Calculate remain balls from current score
		int remBalls = coord.length - score;

		// Draw the small mark at last hit coords with black color
		shapeRenderer.setColor(0,0,0, 0.7f);					// Set color: black, opaque
		shapeRenderer.circle(hit[0], hit[1], 5, 4);	// Set center, radius, segments of circle.

		shapeRenderer.end();				// Stop shape Drawing engine
		Gdx.gl.glDisable(GL20.GL_BLEND);	// Disable Alpha support

		// Check if the field is empty
		if (remBalls<=0){
			Recolor();			// Respawn new balls
			score = 0;			// Reset the scores
		}

		//Text output block
		batch.begin();					// Start the text renderer
		font.setColor(Color.BLACK);		// Set font color
		//Print out some info in UI for the player
		font.draw(batch, "Balls remain: " + Integer.toString(remBalls) + ' ', 10, 40);
		font.draw(batch, "Your hits: " + Integer.toString(score) + ' ', 10, 20);
		batch.end();					// Stop the text renderer


	}

	// Camera adjastment method
	// Updates the viewport if device resolution changed
	// This will calculate height or width of the window from the smallest side set to 800 pixels
	private void configureCamera()
	{
		if (Gdx.graphics.getHeight() < Gdx.graphics.getWidth())
			camera.setToOrtho(false, 800, 800 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		else
			camera.setToOrtho(false, 800 * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 800);
	}

	// Called when renderers destroyed
	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
	}

	// Called when window size is changed
	@Override
	public void resize(int width, int height)
	{
		configureCamera();
	}

	// Called when game windows minimized
	@Override
	public void pause()
	{

	}

	// Called when window restored
	@Override
	public void resume()
	{

	}
}

// My custom class
// Maintaining all Balls properties and manipulations
class Coordinator{

	// Class-wide variables
	public float x, y, r;							// Coords and Radius
	private boolean bx=true, by=true, br=true;		// Moving directions
	private OrthographicCamera cam;					// Camera refrence
	private int speed, groth;						// Move Speed and Groth Rate

	// Constructor N1 - Spawn a Ball at specific coords, with set radius and speed
	Coordinator(float x, float y, float r, OrthographicCamera cam, int spd, int gth){
		this.x=x; this.y=y; this.r=r; this.cam=cam;
		this.speed=spd; this.groth=gth;
	}

	// Constructor N2 - Spawn a Ball at random coords, with random radius and speed
	Coordinator(OrthographicCamera cam){
		this.x= (float) Math.random() * cam.viewportWidth ;		// [0 .. ~800]
		this.y= (float) Math.random() * cam.viewportHeight ;	// [0 .. ~800]
		this.r= (float) Math.random() * 35 + 15 ; 				// [15 .. 50]
		this.speed= (int) Math.round( (float)(Math.random()+0.3) *100 );
		this.groth= (int) Math.round( (float)(Math.random()) *10 );
		this.cam= cam;
	}

	// Updating location of the Ball, based on active direction
	void Move(){
		// Move alongside the X axis
		if (this.bx) {
			this.x = this.x + this.speed * Gdx.graphics.getDeltaTime();
		} else {
			this.x = this.x - this.speed * Gdx.graphics.getDeltaTime();
		}
		// Move alongside the Y axis
		if (this.by) {
			this.y = this.y + this.speed * Gdx.graphics.getDeltaTime();
		} else {
			this.y = this.y - this.speed * Gdx.graphics.getDeltaTime();
		}
		// Grow or shrink the radius
		if (this.br) {
			this.r = this.r + this.groth * Gdx.graphics.getDeltaTime();
		} else {
			this.r = this.r - this.groth * Gdx.graphics.getDeltaTime();
		}
	}

	// Check the the intersection of the Ball with Screen borders
	void BorderCheck(){
		// Test an each axis, including radius for flying out of screen and flipping their direction.
		if ( this.x+this.r > this.cam.viewportWidth) {this.bx=false; this.br=!this.br;}
		if ( this.y+this.r > this.cam.viewportHeight) {this.by=false; this.br=!this.br;}
		if ( this.x+this.r < this.r*2) {this.bx=true; this.br=!this.br;}
		if ( this.y+this.r < this.r*2) {this.by=true; this.br=!this.br;}
		if ( this.r<=15 || this.r>=this.cam.viewportWidth/2 || this.r>=this.cam.viewportHeight/2 ) { this.br=!this.br; }
	}
}
