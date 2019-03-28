package net.scadsdnd.randomatic;

import com.badlogic.gdx.ApplicationAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.*;
import java.util.*;

import javax.swing.Spring;


public class RandomaticMain extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	int balls;
	Coordinator[] coord;
	float[][] colmx;

	//otput
	SpriteBatch batch;
	BitmapFont font;

	public void Recolor(){

		balls = (int) Math.round( (float)Math.random() *100 );
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
		camera = new OrthographicCamera();
		configureCamera();
		shapeRenderer = new ShapeRenderer();

		Recolor();

		//otput
		batch = new SpriteBatch();
		font = new BitmapFont();

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

		for (int c=0; c<balls; c++){
			shapeRenderer.setColor(colmx[c][0], colmx[c][1], colmx[c][2], colmx[c][3]);
			shapeRenderer.circle(coord[c].x, coord[c].y, coord[c].r);
			coord[c].Move(); coord[c].Check();
		}

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (Gdx.input.justTouched()){ Recolor(); }

		//otput 
		batch.begin();
		font.setColor(Color.BLACK);
		//font.draw(batch, Float.toString(colmx[0][0]) + ' ' + Float.toString(colmx[0][1]) + ' ' + Float.toString(colmx[0][2]) + ' ' + Float.toString(colmx[0][3]), 100, 50);
		//font.draw(batch, Float.toString(colmx[1][0]) + ' ' + Float.toString(colmx[1][1]) + ' ' + Float.toString(colmx[1][2]) + ' ' + Float.toString(colmx[1][3]), 100, 70);
		//font.draw(batch, Float.toString(colmx[2][0]) + ' ' + Float.toString(colmx[2][1]) + ' ' + Float.toString(colmx[2][2]) + ' ' + Float.toString(colmx[2][3]), 100, 90);
		//font.draw(batch,Integer.toString(balls2), 100, 90);
		//font.draw(batch,Float.toString(balls3), 100, 110);
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

	void Check(){
		if ( this.x+this.r > this.cam.viewportWidth) {this.bx=false; this.br=!this.br;}
		if ( this.y+this.r > this.cam.viewportHeight) {this.by=false; this.br=!this.br;}
		if ( this.x+this.r < this.r*2) {this.bx=true; this.br=!this.br;}
		if ( this.y+this.r < this.r*2) {this.by=true; this.br=!this.br;}
		if ( this.r<=15 || this.r>=this.cam.viewportWidth/2 || this.r>=this.cam.viewportHeight/2 ) { this.br=!this.br; }
	}
}
