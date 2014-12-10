package fi.arcada.prog.blindlabyrinth;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

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
    protected double xDistanceImaginary, yDistanceImaginary;
    protected double preemptiveXSpeed, preemptiveYSpeed;
    protected double preemptiveXDistance, preemptiveYDistance;

    ArrayList<ArrayList> ballCoordinatesList;
    ArrayList<Integer> collisionPointXAndYPos;

    protected ArrayList<Double> speedAndLeftoverDecimal;

    protected boolean gradientMode;

    //gradiant test
    Paint gPaint;
    Paint gPaintFade;
    int midPointLength;
    int gradientLength;
    int gradientFadeLength;

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
    double collisionPointReverseAngle;
    double testAngle;
    double xTest;
    double yTest;
    double kTest;
    double xTestTemp;
    double yTestTemp;
    double kTestTemp;
    double dirWallX;
    double dirWallY;
    double dirWallK;
    double wallXTotal;
    double wallYTotal;
    double xSpeedDoubleFromCollision;
    double ySpeedDoubleFromCollision;
    double dirOutK;

    //Variables for getNewSpeedVectorXAndY. Probably only a temporary storing place.
    ArrayList<Double> xAndY;
    double baseVector;
    double multiplier;

    //Constructor designed for usage when an object of Ball is created.
    //Initializes values.
    public GraphicsObject(int ballXStartPosition, int ballYStartPosition, int ballWidth, int ballHeight, int color, GameView view, int BallXStartSpeed, int BallYStartSpeed, boolean gradientMode) {
        xPosition = ballXStartPosition;
        yPosition = ballYStartPosition;
        width = ballWidth;
        height = ballHeight;
        this.color = new Paint();
        this.color.setColor(color);
        this.view = view;
        xSpeed = BallXStartSpeed;
        ySpeed = BallYStartSpeed;
        this.gradientMode = gradientMode;

        //Checks if width and height are even and changes them to odd if they are (odd numbers work better with formulas since they have a true middle point in pixels)
        if ( (width & 1) == 0 ) width--;
        if ( (height & 1) == 0 ) height --;

        //Creates a rectangle from the given data, serving as the location data of the ball + how big it is.
        size = new RectF();
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);

        //Gives a middle point for the ball for faster access without having to do the math every time.
        midPointLength = (int) (Math.round((double) (width / 2)));

        //Gives length to the gradient.
        gradientLength = width*2;

        xSpeedDoubleVersion = (double) xSpeed;
        ySpeedDoubleVersion = (double) ySpeed;

        xDistanceImaginary = 0;
        yDistanceImaginary = 0;

        ballCoordinatesList = new ArrayList<ArrayList>();
        collisionPointXAndYPos = new ArrayList<Integer>();

        //Some default values for gradient paint. No need to change them.
        gPaint = new Paint();
        gPaint.setColor(Color.BLACK);
        gPaint.setStrokeWidth(1);
        gPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //Default value for gradient paint fade version. Changed later according to mode.
        gPaintFade = new Paint();
        gPaintFade.setColor(Color.BLACK);

        //Default value to avoid null value error.
        gradientFadeLength = midPointLength+width*2;
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

    public Paint getGradient() {
        return gPaint;
    }

    public Point getPosition() {
        return new Point(xPosition, yPosition);
    }

    public void setGradientShader() {
        gPaint.setShader(new RadialGradient(xPosition+midPointLength, yPosition+midPointLength, gradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP));
    }

    public void setGradientFadeLengthTrailblazer() {
        gradientFadeLength = midPointLength+width*2;
    }

    public void setGradientFadeLengthGlowstick() {
        gradientFadeLength = (int) (midPointLength+width*2.2);
    }

    public void setGradientFadeColorTrailblazer() {
        gPaintFade.setColor(Color.parseColor("#99000000"));
    }

    public void setGradientFadeColorGlowstick() {
        gPaintFade.setColor(Color.BLACK);
    }
}
