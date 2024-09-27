package net.scadsdnd.randomatic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameBoard extends ScreenAdapter {

    // Global game variables, empty
    private int balls;
    private Coordinator[] coord;
    private float[][] colmx;
    private int score;
    private int limit;
    private int miss;

    private RandomaticMain randomaticGame;

    public Boolean isZen = true;

    public GameBoard(RandomaticMain game){
        this.randomaticGame = game;
    }

    // Prepare the game objects
    public void Recolor(){

        balls = (int) Math.round( (float)Math.random() * 100 );		// Set amount of balls, randomly
        coord = new Coordinator[balls];								// Create array for Balls objects
        colmx = new float[balls][4];								// Create array for their props
        limit = Math.round(balls / 3);

        for (int c=0; c<balls; c++){
            coord[c] = new Coordinator(randomaticGame.camera);						// Fill the array of Balls with actual objects
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
        miss = 0;

        configureCamera();						// Call the camera adjastment method
        Recolor();								// Populate the field

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

        randomaticGame.camera.update();										                    // Update the camera
        randomaticGame.shapeRenderer.setProjectionMatrix(randomaticGame.camera.combined);		// Set simple matrix for coords
        // Set the shape fill
        // start drawing engine
        randomaticGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector2 cleanHit = new Vector2(0,0);		// Create storage for hit coords

        // -----------------------------------
        // Gdx.input origin is top-left corner
        // getX\getY is in screen coordinate

        // Do this if user touched\clicked game board
        if (Gdx.input.justTouched()) {
            // camera.unproject is a converter from screen coordinates to world.
            // Store or Update the converted click coordinate for continual usage
            cleanHit.x = randomaticGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).x;
            cleanHit.y = randomaticGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y;

            boolean flagBallHit = false;

            // loop thorough the Balls array
            for (int c=0; c<balls; c++) {
                // Check if read Ball destroyed previously (by player)
                if (coord[c] != null) {
                    // Create and initialize a Circle object for the hit test
                    Circle myC = new Circle(coord[c].x, coord[c].y, coord[c].r);

                    // Perform intersection test of user hit (touch) coordinates
                    // with fantom circle, duplicating visible (drawn) circle.
                    if (myC.contains(cleanHit)) {
                        score += 1;             // hit detected > +1 to score
                        coord[c] = null;        // hit detected > destroy the ball
                        flagBallHit = true;
                    }
                    myC = null;            // Destroy fantom (test) circle
                }
            }
            if (!flagBallHit){
                miss += 1;
            }
        }

        // loop thorough the Balls array
        for (int c=0; c<balls; c++){
            // Check if read Ball destroyed previously (by player)
            if (coord[c]!=null) {

                // -----------------------------------------
                // shapeRenderer origin is lower-left corner

                // Set the new ball color, from prepared randomized props array
                randomaticGame.shapeRenderer.setColor(colmx[c][0], colmx[c][1], colmx[c][2], colmx[c][3]);
                // Draw the new circle with updated\new coords+alpha and set color
                randomaticGame.shapeRenderer.circle(coord[c].x, coord[c].y, coord[c].r);

                coord[c].Move();            // Call Ball's update coords method
                coord[c].BorderCheck();        // Call Ball's screen border check

            }
        }

        // Calculate remain balls from current score
        int remBalls = coord.length - score;

        // Draw the small mark at last hit coords with black color
        randomaticGame.shapeRenderer.setColor(0,0,0, 0.7f);					// Set color: black, opaque
        randomaticGame.shapeRenderer.circle(cleanHit.x, cleanHit.y, 5, 4);	// Set center, radius, segments of circle.

        randomaticGame.shapeRenderer.end();				// Stop shape Drawing engine
        Gdx.gl.glDisable(GL20.GL_BLEND);	// Disable Alpha support


            // Check if the field is empty
            if (remBalls <= 0) {
                // ZenMode - unlimited
                if (isZen) {
                    Recolor();            // Respawn new balls
                    score = 0;            // Reset the scores
                    miss = 0;             // User miss clicks
                } else {
                    // User win Challange mode
                    EndGame(true);
                }
            }


            if (miss >= limit && !isZen ){
                // User loosed in challange mode
                EndGame(false);
            }

        //Text output block
        randomaticGame.batch.begin();					// Start the text renderer
        randomaticGame.font.setColor(Color.BLACK);		// Set font color
        //Print out some info in UI for the player
        if (!isZen) {
            randomaticGame.font.draw(randomaticGame.batch, randomaticGame.langStrs.get("lbl_lives") + Integer.toString(limit) + ' ', 10, 200);
            randomaticGame.font.draw(randomaticGame.batch, randomaticGame.langStrs.get("lbl_miss") + Integer.toString(miss) + ' ', 10, 150);
        }
        randomaticGame.font.draw(randomaticGame.batch, randomaticGame.langStrs.get("lbl_hits") + Integer.toString(score) + ' ', 10, 100);
        randomaticGame.font.draw(randomaticGame.batch, randomaticGame.langStrs.get("lbl_remain") + Integer.toString(remBalls) + ' ', 10, 50);
        randomaticGame.batch.end();					// Stop the text renderer


    }

    // Camera adjastment method
    // Updates the viewport if device resolution changed
    // This will calculate height or width of the window from the smallest side set to 800 pixels
    private void configureCamera()
    {
        int desktopSide = 1024;
        if (Gdx.graphics.getHeight() < Gdx.graphics.getWidth())
            randomaticGame.camera.setToOrtho(false, desktopSide, desktopSide * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
        else
            randomaticGame.camera.setToOrtho(false, desktopSide * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), desktopSide);
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

    private void EndGame(boolean isWin){
        ResultScreen resScene = new ResultScreen(randomaticGame);
        resScene.hitsCount = score;
        resScene.missCount = miss;
        resScene.limitCount = limit;
        resScene.isWin = isWin;
        randomaticGame.setScreen(resScene);
    }

}
