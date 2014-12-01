package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
    protected Paint glow;
    protected GameView view;
    protected int xSpeed, ySpeed;

    protected RectF size;
    protected RectF glowSize;

    protected double timeStamp;
    protected double friction;
    protected double accelerationX, accelerationY;
    protected double xSpeedDoubleVersion, ySpeedDoubleVersion;
    protected double xDistanceImaginary, yDistanceImaginary;
    protected double preemptiveXSpeed, preemptiveYSpeed;
    protected double preemptiveXDistance, preemptiveYDistance;

    ArrayList<ArrayList> ballCoordinatesList;
    ArrayList<Integer> collisionPointXAndYPos;

    protected ArrayList<Double> speedAndLeftoverDecimal;

    protected boolean glowMode;

    //Variables for preemptiveCollisionCheck. Probably only a temporary storing place.
    boolean collisionsStarted;
    boolean collisionsEnded;
    ArrayList<ArrayList<Integer>> collisionPoints;
    ArrayList<Integer> collisionPointsMiddle;
    double totalCollisions;
    int y2;
    int x2;
    int y1;
    int x1;
    double k1;
    double k2;
    double speedVectorLength;
    double tanAlpha;
    double tanAlpha2;
    double tanAlpha3;
    double tanAlpha4;
    double collisionPointReverseAngle;
    double testAngle;
    double xTest;
    double yTest;
    double kTest;

    //Variables for getNewSpeedVectorXAndY. Probably only a temporary storing place.
    ArrayList<Double> xAndY;
    double baseVector;
    double multiplier;
    double xTotal;
    double yTotal;

    //Constructor designed for usage when an object of Ball is created.
    //Initializes values.
    public GraphicsObject(int ballXStartPosition, int ballYStartPosition, int ballWidth, int ballHeight, int color, GameView view, int BallXStartSpeed, int BallYStartSpeed, boolean glowMode) {
        xPosition = ballXStartPosition;
        yPosition = ballYStartPosition;
        width = ballWidth;
        height = ballHeight;
        this.color = new Paint();
        this.color.setColor(color);
        this.view = view;
        xSpeed = BallXStartSpeed;
        ySpeed = BallYStartSpeed;
        this.glowMode = glowMode;

        //Checks if width and height are even and changes them to odd if they are (odd numbers work better with formulas since they have a true middle point in pixels)
        if ( (width & 1) == 0 ) width--;
        if ( (height & 1) == 0 ) height --;

        //Creates a rectangle from the given data, serving as the location data of the ball + how big it is.
        size = new RectF();
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);
        glowSize = new RectF();
        glowSize.set(xPosition-(width/2), yPosition-(height/2), xPosition+width+(width/2), yPosition+height+(height/2));

        xSpeedDoubleVersion = (double) xSpeed;
        ySpeedDoubleVersion = (double) ySpeed;

        xDistanceImaginary = 0;
        yDistanceImaginary = 0;

        ballCoordinatesList = new ArrayList<ArrayList>();
        collisionPointXAndYPos = new ArrayList<Integer>();

        glowMode = false;
    }

    //Call this if you need the positional data/size for a rectangle formed object.
    //For a Ball object, the move() method is designed to alter the values contained within size.
    public RectF getSize()
    {
        return size;
    }

    //For glowing circle around ball.
    public RectF getGlowSize() {
        return glowSize;
    }

    //Call this if you need the objects color for something.
    //For now needed as a parameter when drawing, but all in all likely quite useless and easily replaceable.
    public Paint getColor()
    {
        return color;
    }

    public Paint getGlow() {
        //blurMaskFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);

        glow = new Paint();
        //glow.setMaskFilter(blurMaskFilter);
        //glow.setColor(0xffffffff);
        glow.setColor(Color.YELLOW);

        return glow;
    }
}
