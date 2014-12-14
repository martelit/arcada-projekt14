package fi.arcada.prog.blindlabyrinth;


import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
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

    //gradient test
    Paint gPaint;
    Paint gPaintFade;
    int midPointLength;
    int gradientLength;
    int gradientFadeLength;
    int tokenGradientFadeLength;
    Shader ballShader;
    Shader oneTokenShader;
    ComposeShader CS;
    Boolean firstPoint;
    Boolean firstComposePoint;

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
    double xTotal;
    double yTotal;
    double baseVector;
    double multiplier;

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

        //Gives initial speed to the ball.
        xSpeedDoubleVersion = (double) xSpeed;
        ySpeedDoubleVersion = (double) ySpeed;

        //Imaginary distances are used to remember smaller distances than 1 pixel for more accurate movement over time.
        xDistanceImaginary = 0;
        yDistanceImaginary = 0;

        //Used in collision calculations.
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

        //Default values for different gradient lengths (radius for the circles) to avoid null value error.
        gradientFadeLength = midPointLength+width*2;
        tokenGradientFadeLength = midPointLength+width*5;
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

    //The following 3 methods are used to update the gradient paint once per frame, depending on which game mode is chosen of course.
    public void setGradientShaderGlowstick(ArrayList<Point> pointsForShaders) {

        ballShader = new RadialGradient(xPosition+midPointLength, yPosition+midPointLength, gradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP);

        if(pointsForShaders.isEmpty()) {
            gPaint.setShader(ballShader);
        }
        else {

            firstPoint = true;

            for(Point p : pointsForShaders) {
                if(firstPoint) {

                    CS = new ComposeShader(ballShader, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.DST_IN);

                    firstPoint = false;
                }
                else {

                    CS = new ComposeShader(CS, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.DST_IN);
                }
            }

            gPaint.setShader(CS);
        }
    }

    public void setGradientShaderTrailblazer(ArrayList<Point> pointsForShaders) {

        ballShader = new RadialGradient(xPosition+midPointLength, yPosition+midPointLength, gradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP);

        if(pointsForShaders.isEmpty()) {
            gPaint.setShader(ballShader);
        }
        else {

            firstPoint = true;

            for(Point p : pointsForShaders) {
                if(firstPoint) {

                    CS = new ComposeShader(ballShader, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN);

                    firstPoint = false;
                }
                else {

                    CS = new ComposeShader(CS, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN);
                }
            }

            gPaint.setShader(CS);
        }
    }

    public void setGradientShaderDarknessMode(ArrayList<Point> pointsForShaders) {

        if(pointsForShaders.isEmpty()) {
            gPaint.setShader(new RadialGradient(0, 0, 1, gPaintFade.getColor(), gPaintFade.getColor(), Shader.TileMode.CLAMP));
        }
        else {

            firstPoint = true;
            firstComposePoint = true;

            for(Point p : pointsForShaders) {
                if(firstPoint) {

                    oneTokenShader = new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP);

                    if(pointsForShaders.size() == 1) {
                        gPaint.setShader(oneTokenShader);
                        break;
                    }
                    firstPoint = false;
                }
                else {

                    if(firstComposePoint) {
                        CS = new ComposeShader(oneTokenShader, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.DST_IN);

                        firstComposePoint = false;
                    }
                    else {
                        CS = new ComposeShader(CS, new RadialGradient(p.x, p.y, tokenGradientFadeLength, Color.TRANSPARENT, gPaintFade.getColor(), Shader.TileMode.CLAMP), PorterDuff.Mode.DST_IN);
                    }
                }

                gPaint.setShader(CS);
            }
        }
    }

    //Used to set radius of visibility around both the ball and tokens. One for each game mode.
    public void setGradientFadeLengthTrailblazer() {
        gradientFadeLength = midPointLength+width*2;
        tokenGradientFadeLength = (int) (midPointLength+width*6.3);
    }

    public void setGradientFadeLengthGlowstick() {
        gradientFadeLength = (int) (midPointLength+width*2.2);
        tokenGradientFadeLength = midPointLength+width*7;
    }

    public void setGradientFadeLengthDarkness() {
        tokenGradientFadeLength = midPointLength+width*7;
    }

    //Used to set color of the area outside the vision of the ball and tokens. One for each game mode.
    public void setGradientFadeColorTrailblazer() {
        gPaintFade.setColor(Color.parseColor("#E0000000"));
    }

    public void setGradientFadeColorGlowstick() {
        gPaintFade.setColor(Color.BLACK);
    }

    public void setGradientFadeColorDarkness() {
        gPaintFade.setColor(Color.BLACK);
    }
}
