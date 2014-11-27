package fi.arcada.prog.blindlabyrinth;

import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by rusty on 8.11.2014.
 */

//Class meant to be used as an extension for all objects drawn during the game.
//Contains some basic values and methods these objects commonly need access to, so writing them once could be a good idea.
public class GraphicsObject {
    protected int xPosition, yPosition;
    protected int width, height;
    protected Paint color;
    protected GameView view;
    protected int xSpeed, ySpeed;

    protected RectF size;

    protected double timeStamp;
    protected double friction;
    protected double accelerationX, accelerationY;
    protected double xSpeedDoubleVersion, ySpeedDoubleVersion;
    protected double xSpeedDoubleVersionTempOldValue, ySpeedDoubleVersionTempOldValue;
    protected double xDistanceImaginary, yDistanceImaginary;
    protected double preemptiveXSpeed, preemptiveYSpeed;
    protected double preemptiveXDistance, preemptiveYDistance;

    ArrayList<ArrayList> ballCoordinatesList;
    ArrayList<Integer> collisionPointXAndYPos;

    protected ArrayList<Double> speedAndLeftoverDecimal;

    //Constructor designed for usage when an object of Ball is created.
    //Initializes values.
    public GraphicsObject(int ballXStartPosition, int ballYStartPosition, int ballWidth, int ballHeight, int color, GameView view, int BallXStartSpeed, int BallYStartSpeed) {
        xPosition = ballXStartPosition;
        yPosition = ballYStartPosition;
        width = ballWidth;
        height = ballHeight;
        this.color = new Paint();
        this.color.setColor(color);
        this.view = view;
        xSpeed = BallXStartSpeed;
        ySpeed = BallYStartSpeed;

        //Creates a rectangle from the given data, serving as the location data of the ball + how big it is.
        size = new RectF();
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);

        xSpeedDoubleVersion = (double) xSpeed;
        ySpeedDoubleVersion = (double) ySpeed;

        xDistanceImaginary = 0;
        yDistanceImaginary = 0;

        ballCoordinatesList = new ArrayList<ArrayList>();
        collisionPointXAndYPos = new ArrayList<Integer>();
    }

    //Call this if you need the positional data/size for a rectangle formed object.
    //For a Ball object, the move() method is designed to alter the values contained within size.
    public RectF getSize()
    {
        return size;
    }

    //Call this if you need the objects color for something.
    //For now needed as a parameter when drawing, but all in all likely quite useless and easily replaceable.
    public Paint getColor()
    {
        return color;
    }
}
