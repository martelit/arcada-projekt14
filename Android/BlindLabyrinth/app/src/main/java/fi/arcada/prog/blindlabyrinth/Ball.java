package fi.arcada.prog.blindlabyrinth;

import java.util.ArrayList;

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
        //OLD POORLY DESIGNED CODE BELOW. NOT RECOMMENDED FOR USAGE IF IT CAN BE AVOIDED.
        //___________________________________________________________________________________________________________________________________________________________________________________________________________
        //Before checking for collisions and altering xSpeedDoubleVersion or ySpeedDoubleVersion, these should be saved temporarily.
        //The last value is important due to there still being code after checkCollisions that relies on the old value.
        /*
        xSpeedDoubleVersionTempOldValue = xSpeedDoubleVersion;
        ySpeedDoubleVersionTempOldValue = ySpeedDoubleVersion;
        */
        //___________________________________________________________________________________________________________________________________________________________________________________________________________

        //Check for collisions against the edges of the screen.
        if(xPosition < 0 && xSpeed < 0) xSpeedDoubleVersion *= -0.4;
        if(xPosition > view.getWidth()-width && xSpeed > 0) xSpeedDoubleVersion *= -0.4;
        if(yPosition < 0 && ySpeed < 0) ySpeedDoubleVersion *= -0.4;
        if(yPosition > view.getHeight()-height && ySpeed > 0) ySpeedDoubleVersion *= -0.4;
    }

    //Method for moving the ball once with the current speed compared to where it was positioned before the call,
    //in other words updates its position for the next drawing.
    public void move(float acceleratorX, float acceleratorY)
    {
        //Code for using an acceleration formula to decide the ball's movement.
        //Works, but is used for meters and seconds in real life, so adjustment of values is needed to get movement that makes sense.
        //___________________________________________________________________________________________________________________________________________________________________________________________________________
        //Taking the t spot of the formula. This doesn't accurately represent the elapsed time between two updates of the game screen,
        //at least not until something like a timer is connected to it. It can be changed to either increase or decrease the strength of acceleration depending on what feels good to use.
        timeStamp = 0.4;

        //The closer friction is to 1, the more of the ball's movement is preserved each time the position is updated.
        //Just leave as 1 if we need/want a ball that doesn't slow down on its own.
        friction = 0.993;

        //Acceleration values are taken from the method's in parameters here.
        //accelerationX = ((double) acceleratorX)*(-1.0);   //Movement in the x-axis seemed to be reverse of what was needed, so the value is now taken times minus one.
        accelerationX = (double) acceleratorX;  //Testing on an actual android suggested reversed movement with this line. I don't have a phone myself to test it, but anyone is welcome to confirm this and swap this row with the one above.
        accelerationY = (double) acceleratorY;

        //Hardcoded acceleration values can be given here for testing purposes or something...
        //accelerationX = 1.0;
        //accelerationY = 8.5;

        //d = vt + (1/2)at2 formula for distance covered when velocity, time and acceleration are known
        //d = v + (1/2)at2 is used where d is the new speed (in pixels to move this time),
        //v is the current speed (in pixels moved last time, and this time without counting acceleration) and
        //(1/2)at2 is the additional movement caused this time due to the current acceleration a.
        //pow(double a, double b) returns a to the power of b (a^b)
        xSpeedDoubleVersion = xSpeedDoubleVersion*friction+0.5*accelerationX*Math.pow(timeStamp, 2.0);
        //Log.v("xSpeedDoubleVersion", "" + xSpeedDoubleVersion);
        ySpeedDoubleVersion = ySpeedDoubleVersion*friction+0.5*accelerationY*Math.pow(timeStamp, 2.0);
        //Log.v("ySpeedDoubleVersion", ""+ySpeedDoubleVersion);

        //The imaginary distances are leftover parts that couldn't be expressed with a whole pixel during the last calculation.
        //Probably quite unnecessary, but could as well stay there unless it gets in the way somehow.
        //Takes the last leftover and combines it with the current speed as the whole movement to be attempted.
        xDistanceImaginary = xDistanceImaginary + xSpeedDoubleVersion;
        //Log.v("xDistanceImaginary", ""+xDistanceImaginary);

        //As long as the total amount of pixels to be moved is 1 or more in any direction, that value is sent to be taken apart into total pixels and a leftover value for the next move() call.
        if(xDistanceImaginary >= 1 || xDistanceImaginary <= -1) {
            speedAndLeftoverDecimal = getSpeedAndLeftoverDecimalValue(xDistanceImaginary);
            xSpeed = (int) Math.round(speedAndLeftoverDecimal.get(0));
            xDistanceImaginary = speedAndLeftoverDecimal.get(1);
        }
        //If not, no speed in pixels is given and the leftover remains the same.
        else {
            xSpeed = 0;
        }

        //Same thing as for movement in the x-axis.
        yDistanceImaginary = yDistanceImaginary + ySpeedDoubleVersion;
        //Log.v("yDistanceImaginary", "" + yDistanceImaginary);

        if(yDistanceImaginary >= 1 || yDistanceImaginary <= -1) {
            speedAndLeftoverDecimal = getSpeedAndLeftoverDecimalValue(yDistanceImaginary);
            ySpeed = (int) Math.round(speedAndLeftoverDecimal.get(0));
            yDistanceImaginary = speedAndLeftoverDecimal.get(1);
        }
        else {
            ySpeed = 0;
        }
        //As xSpeed and ySpeed have been decided, the part using the acceleration formula to get speeds is done.
        //___________________________________________________________________________________________________________________________________________________________________________________________________________

        //If the acceleration formula above isn't used to determine xSpeed and ySpeed, this should.
        //xSpeed = (int) Math.round(acceleratorX);
        //ySpeed = (int) Math.round(acceleratorY);

        //Updates xPosition and yPosition with new values according to the current speed.
        xPosition = xPosition + xSpeed;
        yPosition = yPosition + ySpeed;

        //Before correct movement can be chosen, a check for collisions should run, since that could affect direction and strength of the speed.
        checkCollisions();

        //OLD POORLY DESIGNED CODE BELOW. NOT RECOMMENDED FOR USAGE IF IT CAN BE AVOIDED.
        //___________________________________________________________________________________________________________________________________________________________________________________________________________
        //This is where new values for the ball's location are given.
        //To keep the ball from sinking in to the screen some checks need to be in place before any new position will be given.
        //This part handles the temporary values for xSpeedDoubleVersion and ySpeedDoubleVersion, since the new ones already given
        //represent data for the next time.
        /*if(!(xPosition <= 1 && xSpeed < 0 && xSpeedDoubleVersionTempOldValue <= 0 && xSpeedDoubleVersionTempOldValue > -1)
                &&
                !(xPosition >= view.getWidth()-width-1 && xSpeed > 0 && xSpeedDoubleVersionTempOldValue >= 0 && xSpeedDoubleVersionTempOldValue < 1))
        {
            Log.v("Bounce, ", "xPosition" + xPosition+ " xSpeed"+xSpeed+" xSpeedDoubleVersiont"+xSpeedDoubleVersionTempOldValue);
            xPosition = xPosition + xSpeed;
        }
        else {
            if(xPosition < 1) {
                Log.v("Glue, ", "xPosition" + xPosition+ " xSpeed"+ySpeed+" xSpeedDoubleVersiont"+xSpeedDoubleVersionTempOldValue);
                xPosition = 1;
            }
            else if(xPosition > view.getWidth()-width-1) {
                xPosition = view.getWidth()-width-1;
            }
            xSpeedDoubleVersion = 0;
        }
        if(!(yPosition <= 1 && ySpeed < 0 && ySpeedDoubleVersionTempOldValue <= 0 && ySpeedDoubleVersionTempOldValue > -1)
                &&
                !(yPosition >= view.getHeight()-height-1 && ySpeed > 0 && ySpeedDoubleVersionTempOldValue >= 0 && ySpeedDoubleVersionTempOldValue < 1))
        {
            //Log.v("Bounce, ", "yPosition" + yPosition+ " ySpeed"+ySpeed+" ySpeedDoubleVersiont"+ySpeedDoubleVersionTempOldValue);
            yPosition = yPosition + ySpeed;
        }
        else {
            //Log.v("Glue, ", "yPosition" + yPosition+ " ySpeed"+ySpeed+" ySpeedDoubleVersiont"+ySpeedDoubleVersionTempOldValue);
            if(yPosition < 1) {
                yPosition = 1;
            }
            else if(yPosition > view.getHeight()-height-1) {
                yPosition = view.getHeight()-height-1;
            }
            ySpeedDoubleVersion = 0;
        }*/
        //___________________________________________________________________________________________________________________________________________________________________________________________________________

        //Last checks if either of the positions are outside the allowed field and if that's the case, moves them inside again before the actual size position is updated.
        if(xPosition < 1) {
            xPosition = 1;
        }
        if(xPosition > view.getWidth()-width) {
            xPosition = view.getWidth()-width;
        }
        if(yPosition < 1) {
            yPosition = 1;
        }
        if(yPosition > view.getHeight()-height) {
            yPosition = view.getHeight()-height;
        }

        //The position of the ball is set here, based on what has happened to xPosition and yPosition before.
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);
    }

    //Method to use in conjunction with the acceleration formula to get values for the ball's movement.
    //Give it 3.54 for a list with 3 and 0.54, 6.06 for 6 and 0.06 etc. Also works with negative numbers.
    //The idea is to preserve any leftover movement that can't be expressed in full pixels and carry it over to the next calculation.
    public ArrayList<Double> getSpeedAndLeftoverDecimalValue(double d) {
        ArrayList<Double> list = new ArrayList<Double>();
        double speed = 0.0;

        if(d >= 1.0) {
            while (d > 1.0) {
                d--;
                speed++;
            }

            list.add(speed);
            list.add(d);
            return list;
        }
        else if(d <= -1.0) {
            while (d < -1.0) {
                d++;
                speed--;
            }

            list.add(speed);
            list.add(d);
            return list;
        }
        else {
            list.add(speed);
            list.add(d);
            return list;
        }
    }
}
