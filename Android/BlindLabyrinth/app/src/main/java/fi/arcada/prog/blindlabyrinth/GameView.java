package fi.arcada.prog.blindlabyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by rusty on 8.11.2014.
 */

public class GameView extends View implements Runnable, SensorEventListener {

    //For holding accelerometer values in x, y and z axis.
    //Z can be ignored unless someone wanted to do the project in 3d and use all dimensions.
    float acceleratorX = 0;
    float acceleratorY = 0;
    float acceleratorZ = 0;

    boolean DEBUG_CONTROLS = false;

    public Bitmap ballBitmap;
    public Ball ball;
    public Map map;
    public Controller ctrl;
    Paint blackPaint;
    Path ballPath;
    Region region;
    SharedPreferences prefs;
    String gameMode;
    String size;
    int multiplierOne;
    int multiplierTwo;

    //Default values given to a created ball.
    //Free to be changed later on.
    public int ballXStartPosition = 60, ballYStartPosition = 60;
    public int ballWidth = 11, ballHeight = 11;
    public int ballXStartSpeed = 0, ballYStartSpeed = 0;

    private SensorManager sensorManager;

    public GameView(Context context) {
        super(context);

        blackPaint = new Paint();
        ballPath = new Path();
        region = new Region();

        blackPaint.setColor(Color.BLACK);

        //Information about settings are stored in a SharedPreferences file called "blindLabyrinthPref".
        prefs = context.getSharedPreferences("blindLabyrinthPref", 0);

        //Selects the ball depending on what settings have been given.
        if(prefs.getString("ball", "nothing").equals("ball1")) {
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
        }
        else if(prefs.getString("ball", "nothing").equals("ball2")) {
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball2);
        }
        else if(prefs.getString("ball", "nothing").equals("ball3")) {
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball3);
        }
        else {  //Gives default ball.
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
        }

        Log.v("pref stuff", prefs.getString("gameMode", "nothing"));

        //Selects the visibility depending on what settings have been given.
        if(prefs.getString("gameMode", "nothing").equals("trailblazer")) {
            gameMode = "trailblazer";
            multiplierOne = 2;
        }
        else if(prefs.getString("gameMode", "nothing").equals("glowstick")) {
            gameMode = "glowstick";
            multiplierOne = 3;
        }
        else if(prefs.getString("gameMode", "nothing").equals("darkness")) {
            gameMode = "darkness";
            multiplierOne = 4;
        }
        else if(prefs.getString("gameMode", "nothing").equals("lights_on")) {
            gameMode = "lights_on";
            multiplierOne = 1;
        }
        else {  //Gives default visibility.
            gameMode = "lights_on";
            multiplierOne = 1;
        }

        Log.v("gameMode", gameMode);

        //Selects the size depending on what settings have been given.
        if(prefs.getString("size", "nothing").equals("small")) {
            size = "small";
            multiplierTwo = 1;
        }
        else if(prefs.getString("size", "nothing").equals("medium")) {
            size = "medium";
            multiplierTwo = 2;
        }
        else if(prefs.getString("size", "nothing").equals("large")) {
            size = "large";
            multiplierTwo = 3;
        }
        else {  //Gives default size.
            size = "small";
            multiplierTwo = 1;
        }

        //images used for background and bitmaps are stored in app/src/main/java/res/drawable
        //this.setBackgroundResource(R.drawable.nameOfChosenLabyrinthBackgroundForGameScreen);
        //ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

        Bitmap map1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mask1);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        map = new Map(map1, map1, map1, map1, displaySize.x);

        //This line has been somewhat changed so it can be used in GameView (context added before a few things).
        sensorManager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

		/*	More sensor speeds (taken from api docs)
		    SENSOR_DELAY_FASTEST get sensor data as fast as possible
		    SENSOR_DELAY_GAME	rate suitable for games
		 	SENSOR_DELAY_NORMAL	rate (default) suitable for screen orientation changes
		*/

        Log.v("acceleratorX", "" + acceleratorX);
        Log.v("acceleratorY", ""+acceleratorY);


        ctrl = new Controller(  BitmapFactory.decodeResource(context.getResources(), R.drawable.up),
                                BitmapFactory.decodeResource(context.getResources(), R.drawable.right),
                                BitmapFactory.decodeResource(context.getResources(), R.drawable.down),
                                BitmapFactory.decodeResource(context.getResources(), R.drawable.left));

        startGame();
    }

    //Responsible for creating/launching/resetting everything needed for a new game to begin.
    public void startGame() {
        ball = new Ball(map.startX(), map.startY(), ballWidth, ballHeight, Color.BLUE, this, ballXStartSpeed, ballYStartSpeed);
        createThreads();
    }

    //Creates and launches a thread that refreshes the game screen every x amount of time.
    //If any other threads need to be created/launched in the future, it could happen here.
    public void createThreads() {

        //The thread is about this (this class), while .start executes run() in the corresponding class,
        //in this case found just below this method
        new Thread(this).start();
    }

    //This method loops and refreshes the game screen for as long as needed during a round.
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //launches onDraw() below.
            postInvalidate();
        }
    }

    //For everything that needs to be done every time run() above has completed a cycle of Thread.sleep(30).
    //In other words code for what happens every counted frame during a game.
    protected void onDraw(Canvas canvas)
    {
        if(map.isCompleted(ball.getPosition())) {
            //Hooray, move to another screen or something... This is the "End event trigger"
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("score", prefs.getInt("score", 0) + (multiplierOne * multiplierTwo));
            editor.commit();

            Log.v("END", "The ball is in the goal");
        }

        if(map.findsToken(ball.getPosition())) {
            //Yay, we found a token, add the counter graphics and play a sound
            Log.v("TOKEN", "Token found");
        }

        /*if(map.checkCollision(ball.getTop())) ball.handleCollisionTop();
        else if(map.checkCollision(ball.getBottom())) ball.handleCollisionBottom();
        if(map.checkCollision(ball.getRight())) ball.handleCollisionRight();
        else if(map.checkCollision(ball.getLeft())) ball.handleCollisionLeft();*/

        map.draw(canvas);
        if(!DEBUG_CONTROLS) {
            ball.move(acceleratorX, acceleratorY, map);
        } else {
            Point d = ctrl.getDirection();
            ball.move(d.x * 14, d.y * 14, map);
            ctrl.draw(canvas);
        }

        //Draws the ball.
        canvas.drawBitmap(ballBitmap, null, ball.getSize(), ball.getColor());

        //Decides how much darkness is drawn in addition to the labyrinth and the ball.
        if(gameMode.equals("trailblazer")) {
            //Set the current path for ball as region.
            region.setPath(ballPath, new Region(0, 0, canvas.getWidth(), canvas.getHeight()));

            //Then put the region to ballPath (looks a bit clumsy and there probably is a better way of doing this, but at least it works and doesn't have big performance issues).
            ballPath = region.getBoundaryPath();

            //Makes a path in the form of a circle around the ball, which will be used as an excluded part when drawing the black rectangle filling the screen.
            ballPath.addCircle(ball.getPosition().x+ball.width/2, ball.getPosition().y+ball.height/2, ball.width/2+ball.width, Path.Direction.CW);

            //This is the line that makes so the path isn't involved as a part of the black rectangle.
            canvas.clipPath(ballPath, Region.Op.DIFFERENCE);

            //Fills the entire screen with a black rectangle.
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blackPaint);
        }
        else if(gameMode.equals("glowstick")) {
            ballPath.addCircle(ball.getPosition().x+ball.width/2, ball.getPosition().y+ball.height/2, ball.width/2+ball.width, Path.Direction.CW);
            canvas.clipPath(ballPath, Region.Op.DIFFERENCE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blackPaint);

            //Resets the path, since it shouldn't leave a trail behind in this mode and even if it should, adding new circles to a path for every little movement of the ball is a huge performance issue.
            ballPath.rewind();
        }
        else if(gameMode.equals("darkness")) {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blackPaint);
        }
        else if(gameMode.equals("lights_on")) {
            //Nothing really needs to be done here, at least not for now.
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                ctrl.updateDirection(x, y);
                return true;
            case MotionEvent.ACTION_UP:
                ctrl.resetDirection();
                return true;
        }
        return false;
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

        // check sensor type
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            acceleratorX=event.values[0];
            acceleratorY=event.values[1];
            acceleratorZ=event.values[2];
        }
    }

}
