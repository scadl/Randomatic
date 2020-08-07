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
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	int balls;
	Coordinator[] coord;
	float[][] colmx;
	int score;
	float[] hit;

	//otput
	SpriteBatch batch;
	BitmapFont font;

	public void Recolor(){

		balls = (int) Math.round( (float)Math.random() * 10 );
		coord = new Coordinator[balls];
		colmx = new float[balls][4];

		for (int c=0; c<balls; c++){
			coord[c] = new Coordinator(camera);
		}

		for (int i=0; i<balls; i++){
			for (int j=0; j<4; j++){
				colmx[i][j] = (float) Math.round( (Math.random()*0.65f+0.3f) *100) / 100.00f ;
			}
		}

	}

	@Override
	public void create()
	{
		score = 0;
		camera = new OrthographicCamera();
		configureCamera();
		shapeRenderer = new ShapeRenderer();

		Recolor();

		//otput
		batch = new SpriteBatch();
		font = new BitmapFont();
		hit = new float[2];
	}

	@Override
	public void render()
	{

		//float balls3 = (float) (Math.random()) ;
		//int balls2 = (int) Math.round( (float)Math.random() *100 );


		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		Vector3 cleanHit = new Vector3(0,0,0);

		// Gdx.input origin is top-left corner
		// getX\getY is in screen coordinates
		if (Gdx.input.justTouched()) {
			// camera.unproject is a converter from screen coordinates to world.
			cleanHit = camera.unproject(
					new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			hit[0] = cleanHit.x;
			hit[1] = cleanHit.y;
		}

		for (int c=0; c<balls; c++){
			if (coord[c]!=null) {
				// shapeRenderer origin is lower-left corner
				shapeRenderer.setColor(colmx[c][0], colmx[c][1], colmx[c][2], colmx[c][3]);
				shapeRenderer.circle(coord[c].x, coord[c].y, coord[c].r);

				Circle myC = new Circle(coord[c].x, coord[c].y, coord[c].r);

				coord[c].Move();
				coord[c].BorderCheck();

				if (myC.contains(cleanHit.x, cleanHit.y)) {
					score += 1;
					coord[c] = null;
				}
				myC = null;
			}
		}

		int remBalls = coord.length - score;

		// last hit mark
		shapeRenderer.setColor(0,0,0, 1);
		shapeRenderer.rect(hit[0], hit[1], 10,10);

		shapeRenderer.end();
		//shapeRenderer.dispose();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (remBalls<=0){
			Recolor();
			score = 0;
		}

		//otput
		batch.begin();
		font.setColor(Color.BLACK);
		//font.draw(batch, new Circle(coord[0].x, coord[0].y, coord[0].r).toString(), coord[0].x, coord[0].y+20);
		font.draw(batch, "Last hit: " + Float.toString(hit[0]) + ' ' + Float.toString(hit[1]), 10, 60);
		font.draw(batch, "Balls remain: " + Integer.toString(remBalls) + ' ', 10, 40);
		font.draw(batch, "Your hits: " + Integer.toString(score) + ' ', 10, 20);
		batch.end();


	}

	private void configureCamera()
	{
		if (Gdx.graphics.getHeight() < Gdx.graphics.getWidth())
			camera.setToOrtho(false, 800, 800 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		else
			camera.setToOrtho(false, 800 * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 800);
	}

	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
		configureCamera();
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}

class Coordinator{
	public float x, y, r;
	private boolean bx=true, by=true, br=true;
	private OrthographicCamera cam;
	private int speed, groth;

	Coordinator(float x, float y, float r, OrthographicCamera cam, int spd, int gth){
		this.x=x; this.y=y; this.r=r; this.cam=cam;
		this.speed=spd; this.groth=gth;
	}
	Coordinator(OrthographicCamera cam){
		this.x= (float) Math.random() * cam.viewportWidth ;
		this.y= (float) Math.random() * cam.viewportHeight ;
		this.r= (float) Math.random() * 35 + 15 ; 			// [15 .. 50]
		// this.r= (float) Math.random() * 50 ;  				// [0 .. 50]
		//	this.speed= (int) Math.random() * 250 + 150;		// [150 .. 350]
		//	this.groth= (int) Math.random() * 20 + 10;			// [10 .. 30]
		this.speed= (int) Math.round( (float)(Math.random()+0.3) *100 );
		this.groth= (int) Math.round( (float)(Math.random()) *10 );
		this.cam=cam;
	}

	void Move(){

		if (this.bx) {
			this.x = this.x + this.speed * Gdx.graphics.getDeltaTime();
		} else {
			this.x = this.x - this.speed * Gdx.graphics.getDeltaTime();
		}

		if (this.by) {
			this.y = this.y + this.speed * Gdx.graphics.getDeltaTime();
		} else {
			this.y = this.y - this.speed * Gdx.graphics.getDeltaTime();
		}

		if (this.br) {
			this.r = this.r + this.groth * Gdx.graphics.getDeltaTime();
		} else {
			this.r = this.r - this.groth * Gdx.graphics.getDeltaTime();
		}

	}

	void BorderCheck(){
		if ( this.x+this.r > this.cam.viewportWidth) {this.bx=false; this.br=!this.br;}
		if ( this.y+this.r > this.cam.viewportHeight) {this.by=false; this.br=!this.br;}
		if ( this.x+this.r < this.r*2) {this.bx=true; this.br=!this.br;}
		if ( this.y+this.r < this.r*2) {this.by=true; this.br=!this.br;}
		if ( this.r<=15 || this.r>=this.cam.viewportWidth/2 || this.r>=this.cam.viewportHeight/2 ) { this.br=!this.br; }
	}

	boolean ClickTest() {
		if (
				Gdx.input.getX() < this.x + this.r &&
				Gdx.input.getX() > this.x - this.r &&
				Gdx.input.getY() < this.y + this.r &&
				Gdx.input.getY() > this.y - this.r
						//&&	Gdx.input.justTouched()
		){
			return true;
		} else{
			return false;
		}
	}
}
