package net.scadsdnd.randomatic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

public class GameBoard extends ScreenAdapter {

    // Global game variables, empty
    int balls;
    Coordinator[] coord;
    float[][] colmx;
    int score;
    float[] hit;

    RandomaticMain RandomatciGame;

    public GameBoard(RandomaticMain game){
        this.RandomatciGame = game;
    }

    // Prepare the game objects
    public void Recolor(){

        balls = (int) Math.round( (float)Math.random() * 100 );		// Set amount of balls, randomly
        coord = new Coordinator[balls];								// Create array for Balls objects
        colmx = new float[balls][4];								// Create array for their props

        for (int c=0; c<balls; c++){
            coord[c] = new Coordinator(RandomatciGame.camera);						// Fill the array of Balls with actual objects
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
    public void show()
    {

        score = 0;								// Score to zero
        configureCamera();						// Call the camera adjastment method
        Recolor();								// Populate the field
        hit = new float[2];						// Create the array for th last hit coords
    }

    // Render a final frame.
    // Called each screen update.
    @Override
    public void render(float delta)
    {
        // Set the background - white opaque
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Allow the use of transparency for the shapes.
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        RandomatciGame.camera.update();										// Update the camera
        RandomatciGame.shapeRenderer.setProjectionMatrix(RandomatciGame.camera.combined);		// Set simple matrix for coords
        RandomatciGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);	// Set the shape fill and start drawing engine

        Vector3 cleanHit = new Vector3(0,0,0);		// Create storage for hit coords

        // -----------------------------------
        // Gdx.input origin is top-left corner
        // getX\getY is in screen coordinates

        // Do this if user touched\clicked game board
        if (Gdx.input.justTouched()) {
            // camera.unproject is a converter from screen coordinates to world.
            cleanHit = RandomatciGame.camera.unproject(
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
                RandomatciGame.shapeRenderer.setColor(colmx[c][0], colmx[c][1], colmx[c][2], colmx[c][3]);
                // Draw the new circle with updated\new coords+alpha and set color
                RandomatciGame.shapeRenderer.circle(coord[c].x, coord[c].y, coord[c].r);

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
        RandomatciGame.shapeRenderer.setColor(0,0,0, 0.7f);					// Set color: black, opaque
        RandomatciGame.shapeRenderer.circle(hit[0], hit[1], 5, 4);	// Set center, radius, segments of circle.

        RandomatciGame.shapeRenderer.end();				// Stop shape Drawing engine
        Gdx.gl.glDisable(GL20.GL_BLEND);	// Disable Alpha support

        // Check if the field is empty
        if (remBalls<=0){
            Recolor();			// Respawn new balls
            score = 0;			// Reset the scores
        }

        //Text output block
        RandomatciGame.batch.begin();					// Start the text renderer
        RandomatciGame.font.setColor(Color.BLACK);		// Set font color
        RandomatciGame.font.getData().setScale(3);		// Set text scale
        //Print out some info in UI for the player
        RandomatciGame.font.draw(RandomatciGame.batch, "Balls remain: " + Integer.toString(remBalls) + ' ', 10, 50);
        RandomatciGame.font.draw(RandomatciGame.batch, "Your hits: " + Integer.toString(score) + ' ', 20, 100);
        RandomatciGame.batch.end();					// Stop the text renderer


    }

    // Camera adjastment method
    // Updates the viewport if device resolution changed
    // This will calculate height or width of the window from the smallest side set to 800 pixels
    private void configureCamera()
    {
        int desktopSide = 1024;
        if (Gdx.graphics.getHeight() < Gdx.graphics.getWidth())
            RandomatciGame.camera.setToOrtho(false, desktopSide, desktopSide * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        else
            RandomatciGame.camera.setToOrtho(false, desktopSide * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), desktopSide);
    }

    // Called when renderers destroyed
    @Override
    public void hide(){

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
