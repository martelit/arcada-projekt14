package fi.arcada.prog.blindlabyrinth;

/**
 * Created by rusty on 8.11.2014.
 */

//For storing and accessing data about the ball used by the player.
//As of now, the "ball" actually is merely a rectangle formed figure (the picture has transparent background).
//This file might very well be in for changes later, should the need arise for a truly circular ball.
//The extending of GraphicsObject merely gives this class some commonly accessed data fields and methods for graphical objects in general.
public class Ball extends GraphicsObject {

    //Constructor for initializing values given to the ball.
    public Ball(int ballXStartPosition, int ballYStartPosition, int ballWidth, int ballHeight, int color, GameView view, int BallXStartSpeed, int ballYStartSpeed) {

        //Runs the superclass version (in GraphicsObject) of the constructor.
        super(ballXStartPosition, ballYStartPosition, ballWidth, ballHeight, color, view, BallXStartSpeed, ballYStartSpeed);
    }

    //For checking if the ball has collided with something of interest.
    public void checkCollisions()
    {
        //Nothing here yet, the ball will just fly out of the screen...
    }

    //Code for moving the ball once with the current speed compared to where it was positioned before the call,
    //in other words updates its position for the next drawing.
    public void move()
    {
        //Before correct movement can be chosen, a check for collisions should run, since that could affect the speed.
        checkCollisions();

        //Early code just for the sake of updating the position of a ball object, in other words testing movement.
        //Will be changed later to reflect speed given by an acceleration formula, where the values of the accelerometer are used.
        xPosition = xPosition + xSpeed;
        yPosition = yPosition + ySpeed;
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);
    }
}
