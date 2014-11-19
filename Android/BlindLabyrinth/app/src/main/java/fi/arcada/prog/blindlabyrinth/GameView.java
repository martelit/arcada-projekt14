package fi.arcada.prog.blindlabyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
/**
 * Created by rusty on 8.11.2014.
 */

public class GameView extends View implements Runnable, SensorEventListener {

    //For holding accelerometer values in x, y and z axis.
    //Z can be ignored unless someone wanted to do the project in 3d and use all dimensions.
    float acceleratorX = 0;
    float acceleratorY = 0;
    float acceleratorZ = 0;

    boolean DEBUG_CONTROLS = true;

    public Bitmap ballBitmap;
    public Ball ball;
    public Map map;
    public Controller ctrl;

    //Default values given to a created ball.
    //Free to be changed later on.
    public int ballXStartPosition = 20, ballYStartPosition = 20;
    public int ballWidth = 50, ballHeight = 50;
    public int ballXStartSpeed = 0, ballYStartSpeed = 0;

    private SensorManager sensorManager;

    public GameView(Context context) {
        super(context);

        //Information about settings are stored in a SharedPreferences file called "blindLabyrinthPref".
        SharedPreferences prefs = context.getSharedPreferences("blindLabyrinthPref", 0);

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

        //images used for background and bitmaps are stored in app/src/main/java/res/drawable
        //this.setBackgroundResource(R.drawable.nameOfChosenLabyrinthBackgroundForGameScreen);
        //ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

        Bitmap map1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.mask1);

        map = new Map(map1, map1);

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
        ball = new Ball(ballXStartPosition, ballYStartPosition, ballWidth, ballHeight, Color.BLUE, this, ballXStartSpeed, ballYStartSpeed);
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
        if(map.checkCollision(ball.getPosition())) ball.handleCollision();

        map.draw(canvas);
        if(!DEBUG_CONTROLS) {
            ball.move(acceleratorX, acceleratorY);
        } else {
            Point d = ctrl.getDirection();
            ball.move(d.x * 14, d.y * 14);
            ctrl.draw(canvas);
        }
        canvas.drawBitmap(ballBitmap, null, ball.getSize(), ball.getColor());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
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
