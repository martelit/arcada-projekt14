package fi.arcada.prog.blindlabyrinth;

import android.graphics.Point;
import android.util.Log;

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
    public void checkCollisions() {

        //Check for collisions against the edges of the screen.
        if(xPosition < 0 && xSpeed < 0) xSpeedDoubleVersion *= -0.4;
        if(xPosition > view.getWidth()-width && xSpeed > 0) xSpeedDoubleVersion *= -0.4;
        if(yPosition < 0 && ySpeed < 0) ySpeedDoubleVersion *= -0.4;
        if(yPosition > view.getHeight()-height && ySpeed > 0) ySpeedDoubleVersion *= -0.4;
    }

    public Point getPosition() {
        return new Point(xPosition, yPosition);
    }

    public Point getTop() {
        return new Point(xPosition + width / 2, yPosition);
    }
    public Point getRight() {
        return new Point(xPosition + width, yPosition + height);
    }
    public Point getBottom() {
        return new Point(xPosition + width / 2, yPosition + height);
    }
    public Point getLeft() {
        return new Point(xPosition, yPosition + height / 2);
    }

    public void updateRect() {
        size.set(xPosition, yPosition, xPosition+width, yPosition+height);
    }
    public void handleCollisionTop() {
        Log.d("collision", "top");
        ySpeedDoubleVersion *= -0.4;
        yPosition += 1;
        updateRect();
    }
    public void handleCollisionRight() {
        Log.d("collision", "right");
        xSpeedDoubleVersion *= -0.4;
        xPosition -= 1;
        updateRect();
    }
    public void handleCollisionBottom() {
        Log.d("collision", "bottom");
        ySpeedDoubleVersion *= -0.4;
        yPosition -= 1;
        updateRect();
    }
    public void handleCollisionLeft() {
        Log.d("collision", "left");
        xSpeedDoubleVersion *= -0.4;
        xPosition += 1;
        updateRect();
    }

    //Method for moving the ball once with the current speed compared to where it was positioned before the call,
    //in other words updates its position for the next drawing.
    public void move(float acceleratorX, float acceleratorY, Map map)
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
        //accelerationX = (double) acceleratorX;  //Testing on an actual android suggested reversed movement with this line. I don't have a phone myself to test it, but anyone is welcome to confirm this and swap this row with the one above.
        accelerationX = ((double) acceleratorX)*(-1.0);   //Movement in the x-axis seemed to be reverse of what was needed, so the value is now taken times minus one.
        accelerationY = (double) acceleratorY;

        //Ignores minor acceleration values given by the accelerometer.
        //The idea is to remove drawn out lingering movement when a phone is for example left on a table.
        if(accelerationX <= 0.5 && accelerationX >= -0.5){
            accelerationX = 0;
        }
        if(accelerationY <= 0.5 && accelerationY >= -0.5) {
            accelerationY = 0;
        }

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

        //Fills a list with many small lists of paired x and y values which each represent one dot on the outer line of the ball.
        for(int angle = 0; angle < 360; angle += 15) {
            collisionPointXAndYPos = getCircleXAndYPosition(angle);
            ballCoordinatesList.add(collisionPointXAndYPos);
        }

        //Launches a preemptive check for collisions during the coming movement of the ball on any black dots on the labyrinth map.
        //If collision is detected, appropriate action is taken, such as removing the ball from the area of collision and giving it new speed values.
        preemptiveCollisionCheck(map);

        //Clears the list so it's ready for the next time move() is called.
        ballCoordinatesList.clear();

        //Updates xPosition and yPosition with new values according to the current speed.
        xPosition = xPosition + xSpeed;
        yPosition = yPosition + ySpeed;

        //Before correct movement can be chosen, a check for collisions should run, since that could affect direction and strength of the speed.
        checkCollisions();

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
        //size.set(xPosition, yPosition, xPosition+width, yPosition+height);
        updateRect();
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

    //For calculating a point (x and y location from the middle point of a square figure) on the ball's outer edge based on an angle given.
    public ArrayList<Integer> getCircleXAndYPosition(int angle) {
        ArrayList<Integer> CircleXAndYPos = new ArrayList<Integer>();

        //The math happens here, using sin/cos to get the values needed.
        CircleXAndYPos.add((int) Math.round((width/2)*Math.cos(angle))+xPosition+Math.round(width/2));
        CircleXAndYPos.add((int) Math.round((width/2)*Math.sin(angle))+yPosition+Math.round(height/2));

        return CircleXAndYPos;
    }

    //Checks if the next movement is going to collide with a wall (black pixel on the map) before any actual movement is made.
    //If contact is found, the ball will be backtracked on its own path until it doesn't touch a wall anymore.
    //Anything else can be done to it afterwards depending on what's needed.
    public void preemptiveCollisionCheck(Map map) {

        //The possibly high x and y speeds of the current attempted movement will first need to be broken up into smaller parts.
        //This way no matter how high the speed, the ball will always collide with the first obstacle and not for example totally fly through a wall or collide with a pixel in the middle of a wall.
        //In order to do this, the bigger value can't be above 1 pixel per comparison/tick (part of the actual frame seen in-game). The smaller one will then be divided with the bigger one for something between 0 and under 1.
        //Absolute values (never negative, >= 0) are used, since xSpeed and/or ySpeed could be negative. Perhaps it doesn't matter all that much, but I tried to keep it simple and clear for myself.
        if(Math.abs(xSpeed) > Math.abs(ySpeed)) {   //When xSpeed is larger than ySpeed, this runs, making the test run 1 pixel in x-axis every tick, while y is something less, resulting in 0 and 1 pixel ticks.
            preemptiveXSpeed = 1;
            preemptiveYSpeed = (double) Math.abs(ySpeed)/(double) Math.abs(xSpeed);
        }
        else if(Math.abs(xSpeed) < Math.abs(ySpeed)) {  //Same as above, but when ySpeed is larger than xSpeed.
            preemptiveYSpeed = 1;
            preemptiveXSpeed = (double) Math.abs(xSpeed)/(double) Math.abs(ySpeed);
        }
        else {  //For when both xSpeed and ySpeed are the same. Results in both ticking for 1 pixel until collision is made, or until the total attempted distance is moved without one.
            preemptiveXSpeed = 1;
            preemptiveYSpeed = 1;
        }

        //This is a compensation for not using negative values above. If xSpeed or ySpeed is currently negative, the ticks should also be, so as not to move in the wrong direction.
        if(xSpeed < 0) preemptiveXSpeed *= -1;
        if(ySpeed < 0) preemptiveYSpeed *= -1;

        //While loop that runs until the absolute value of the distance moved in either x-axis or y-axis by ticks is the same as the attempted xSpeed or ySpeed depending on direction.
        //Both of there conditions need to match, but logically they should both match exactly after the same amount of ticks (the higher value of xSpeed and ySpeed for the current frame), unless one of them was 0 to begin with,
        // or there are some minor decimal errors...
        while(Math.abs(preemptiveXDistance) < Math.abs(xSpeed) || Math.abs(preemptiveYDistance) < Math.abs(ySpeed)) {

            //While the while runs, the preemptive imaginary distance traveled in each direction is increased with the values given earlier for each tick.
            preemptiveXDistance += preemptiveXSpeed;
            preemptiveYDistance += preemptiveYSpeed;

            //Once values for a new tick are given, a check needs to run for each of the dots on the outer line of the ball where the user wants collision checks to occur.
            //This amount is decided by a for loop in the move() method, where lower angle values give more dots to check on the ball's outer line.
            for(ArrayList<Integer> listObj : ballCoordinatesList) { //The for itself takes an ArrayList (ballCoordinatesList) that contains even more ArrayLists (of collisionPointXAndYPos, each containing 2 integers, namely one set of x- and y coordinates).

                //If collision is found for any of the paired x- and y coordinates (representing one dot on the outer line), this runs.
                if(map.checkCollision(new Point(listObj.get(0) + (int) preemptiveXDistance, listObj.get(1) + (int) preemptiveYDistance))) {

                    //Since collision happened, a correct combination of xSpeed and ySpeed was found just one tick before the one that caused a collision.
                    //These speeds will now be given as the true values for xSpeed and ySpeed for the coming frame visible to the player, resulting with the ball moving just beside a wall with one or more of its collision points.
                    xSpeed = (int) (preemptiveXDistance-preemptiveXSpeed);
                    ySpeed = (int) (preemptiveYDistance-preemptiveYSpeed);

                    //This is just to ensure the while above breaks.
                    preemptiveXDistance = xSpeed;
                    preemptiveYDistance = ySpeed;

                    //Glues the ball on hit. Removed after proper bouncing has been coded. Until then it's recommended to keep.
                    xSpeedDoubleVersion = 0;
                    ySpeedDoubleVersion = 0;

                    //If collision occurs, the loop is no longer needed and break is called.
                    break;
                }
            }

            //If the while loop reaches this stage without the for loop activating if() for collision detection, one tick of movement has successfully been made without collision in any of the points the user wants to check.
        }

        //Once all is said and done, the values used by this method are reset so they are ready to be used again during the next frame.
        preemptiveXSpeed = 0;
        preemptiveYSpeed = 0;
        preemptiveXDistance = 0;
        preemptiveYDistance = 0;
    }
}
