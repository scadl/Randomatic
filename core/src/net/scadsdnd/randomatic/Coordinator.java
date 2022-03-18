package net.scadsdnd.randomatic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

// My custom class
// Maintaining all Balls properties and manipulations
public class Coordinator{

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
