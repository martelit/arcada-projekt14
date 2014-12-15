package fi.arcada.prog.blindlabyrinth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by rusty on 8.11.2014.
 */

public class GameView extends View implements Runnable, SensorEventListener {

    //For holding accelerometer values in x and y axis, also a few new floats containing sets for original and fixed new values.
    float acceleratorXOriginal = 0;
    float acceleratorYOriginal = 0;
    float acceleratorXNew = 0;
    float acceleratorYNew = 0;

    boolean DEBUG_CONTROLS = false;
    boolean HA;

    public Bitmap ballBitmap;
    public Ball ball;
    public Map map;
    public Controller ctrl;
    Paint blackPaint;
    Path ballPath;
    Path tokensPath;
    ArrayList<Path> tokensPathList;
    ArrayList<Point> tokensPointList;
    Region region;
    SharedPreferences prefs;
    String gameMode;
    String size;
    int multiplierOne;
    int multiplierTwo;
    boolean gradientMode;
    boolean countdownHasNotFinished;
    boolean countdownIsNotCreated;

    //Default values given to a created ball.
    //Free to be changed later on.
    public int ballXStartPosition = 60, ballYStartPosition = 60;
    public int ballWidth = 11, ballHeight = 11;
    public int ballXStartSpeed = 0, ballYStartSpeed = 0;

    private SensorManager sensorManager;

    //Testing to fix bug with landscape as default mode.
    public int rotationIndex;

    protected Thread gameThread;
    protected Dialog winDialog;
    protected Point displaySize;


    public GameView(Context context) {
        super(context);

        blackPaint = new Paint();
        ballPath = new Path();
        tokensPath = new Path();
        tokensPathList = new ArrayList<Path>();
        tokensPointList = new ArrayList<Point>();
        region = new Region();

        blackPaint.setColor(Color.BLACK);

        countdownHasNotFinished = true;
        countdownIsNotCreated = true;

        //Information about settings are stored in a SharedPreferences file called "blindLabyrinthPref".
        prefs = context.getSharedPreferences(Cache.SETTINGS, 0);

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
        else if(prefs.getString("ball", "nothing").equals("ball4")) {
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball4);
        }
        else {  //Gives default ball.
            ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
        }

        //Selects the visibility depending on what settings have been given.
        if(prefs.getString("gameMode", "nothing").equals("trailblazer")) {
            gameMode = "trailblazer";
            gradientMode = true;
            multiplierOne = 2;
        }
        else if(prefs.getString("gameMode", "nothing").equals("glowstick")) {
            gameMode = "glowstick";
            gradientMode = true;
            multiplierOne = 3;
        }
        else if(prefs.getString("gameMode", "nothing").equals("darkness")) {
            gameMode = "darkness";
            gradientMode = false;
            multiplierOne = 4;
        }
        else if(prefs.getString("gameMode", "nothing").equals("lights_on")) {
            gameMode = "lights_on";
            gradientMode = false;
            multiplierOne = 1;
        }
        else {  //Gives default visibility.
            gameMode = "lights_on";
            gradientMode = false;
            multiplierOne = 1;
        }

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
        this.setBackgroundColor(Color.BLACK);
        //ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap mask = null;
        Bitmap skin = null;
        Bitmap goal = BitmapFactory.decodeResource(context.getResources(), R.drawable.goal);
        Bitmap token = BitmapFactory.decodeResource(context.getResources(), R.drawable.token);
        int mode = 0; // 1 = small, 2 = medium, 3 = large
        if(prefs.getString("size", "small").equals("small")) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.s1_m, options);
            skin = BitmapFactory.decodeResource(context.getResources(), R.drawable.s1_s, options);
            mode = 1;
        } else if(prefs.getString("size", "").equals("medium")) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.m1_m, options);
            skin = BitmapFactory.decodeResource(context.getResources(), R.drawable.m1_s, options);
            mode = 2;
        } else if(prefs.getString("size", "").equals("large")) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.l1_m, options);
            skin = BitmapFactory.decodeResource(context.getResources(), R.drawable.l1_s, options);
            mode = 3;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        map = new Map(mask, skin, goal, token, displaySize.x, displaySize.y, mode, Color.DKGRAY);

        //This line has been somewhat changed so it can be used in GameView (context added before a few things).
        sensorManager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        if(DEBUG_CONTROLS) {
            ctrl = new Controller(  BitmapFactory.decodeResource(context.getResources(), R.drawable.up),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.right),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.down),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.left));
        }


        //Gives 0-3 depending on rotation (0 = 0 grades, 1 = 90, 2 = 180, 3 = 270) and is used in fixing the problem devices running landscape mode by default encountered.
        rotationIndex = display.getRotation();

        //___________________________________________________________________________________________________________________________________________________________________________________________________________________________
        //CHANGE THIS TO REFLECT SETTINGS CHOSEN!
        HA = true;

        startGame();
    }

    //Responsible for creating/launching/resetting everything needed for a new game to begin.
    public void startGame() {
        ball = new Ball(map.startX(), map.startY(), map.ballSize(), map.ballSize(), Color.BLUE, this, ballXStartSpeed, ballYStartSpeed);
        setGradientMode();
        createThreads();

        Cache.getInstance().inGame = true;
    }

    public void endGame() {
        Cache c = Cache.getInstance();

        c.inGame = false;
        c.Audio.stopMove();
    }

    public void release() {
        map.release();
        if(DEBUG_CONTROLS) ctrl.release();
        ballBitmap.recycle();

    }

    //Sets a few things to right values if trailblazer, glowstick or darkness is chosen as mode.
    private void setGradientMode() {
        if(gameMode.equals("trailblazer")) {
            ball.setGradientFadeLengthTrailblazer();
            ball.setGradientFadeColorTrailblazer();
        }
        else if(gameMode.equals("glowstick")) {
            ball.setGradientFadeLengthGlowstick();
            ball.setGradientFadeColorGlowstick();
        }
        else if(gameMode.equals("darkness")) {
            ball.setGradientFadeLengthDarkness();
            ball.setGradientFadeColorDarkness();
        }
    }

    //Creates and launches a thread that refreshes the game screen every x amount of time.
    //If any other threads need to be created/launched in the future, it could happen here.
    public void createThreads() {

        //The thread is about this (this class), while .start executes run() in the corresponding class,
        //in this case found just below this method
        gameThread = new Thread(this);
        gameThread.start();
    }

    //This method loops and refreshes the game screen for as long as needed during a round.
    @Override
    public void run() {
        while(Cache.getInstance().inGame) {
            try {
                Thread.sleep(30);

                //launches onDraw() below.
                postInvalidate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //For everything that needs to be done every time run() above has completed a cycle of Thread.sleep(30).
    //In other words code for what happens every counted frame during a game.
    protected void onDraw(Canvas canvas)
    {
        if(!Cache.getInstance().inGame) return;
        if(gameMode.equals("darkness") && countdownHasNotFinished) {    //Runs during the countdown of darkness mode.
            if(countdownIsNotCreated) {
                new CountDownTimer(3200, 1000) {

                    public void onTick(long millisUntilFinished) {

                        //If someone wanted to make something happen during different parts of the countdown, this would be the place.
                        if(millisUntilFinished == 3000) {

                        }
                        else if(millisUntilFinished == 2000) {

                        }
                        else if(millisUntilFinished == 1000) {

                        }
                        else if(millisUntilFinished == 1) {

                        }
                    }

                    //Countdown is finished.
                    public void onFinish() {
                        countdownHasNotFinished = false;
                    }
                }.start();

                countdownIsNotCreated = false;
            }

            //During countdown only the map and ball are displayed on the screen.
            map.draw(canvas);
            canvas.drawBitmap(ballBitmap, null, ball.getSize(), ball.getColor());

        }
        else {  //Runs normally when countdown is done in darkness mode.
            if(map.isCompleted(ball.getSize())) {
                //Hooray, move to another screen or something... This is the "End event trigger"
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("score", prefs.getInt("score", 0) + (multiplierOne * multiplierTwo));
                editor.commit();

                Cache.getInstance().Audio.playSound("levelcompleted",(float)2.0);

                GameViewBlank gvb = (GameViewBlank) getContext();
                gvb.showWin();

                Log.v("END", "The ball is in the goal");
            }

            if(map.findsToken(ball.getSize())) {
                //Yay, we found a token, add the counter graphics and play a sound

                if(HA) {
                    tokensPathList.add(new Path(){{
                        addCircle(ball.getPosition().x+ball.midPointLength, ball.getPosition().y+ball.midPointLength, ball.tokenGradientFadeLength, Path.Direction.CW);
                    }});
                }
                else {
                    //For each token found a new point of the map is added to a list.
                    tokensPointList.add(new Point(ball.xPosition+ball.midPointLength, ball.yPosition+ball.midPointLength));
                }

                //Each token also gets its own countdown timer. When it's time is up, the position is emptied from the list.
                new CountDownTimer(5000, 1000) {

                    //Nothing is done during the countdown.
                    public void onTick(long millisUntilFinished) {

                    }

                    //Countdown is finished.
                    public void onFinish() {
                        if(HA) {
                            if(!tokensPathList.isEmpty()) {
                                for (int a = 0; a < tokensPathList.size(); a++) {
                                    if(!(tokensPathList.get(a) == null)) {
                                        tokensPathList.remove(a);
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            if(!tokensPointList.isEmpty()) {
                                for (int a = 0; a < tokensPointList.size(); a++) {
                                    if(!(tokensPointList.get(a) == null)) {
                                        tokensPointList.remove(a);
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }.start();

                String tokenText =  "Token #" + Integer.toString(map.foundTokens)  + " found";
                Cache.getInstance().Audio.playSound("token",(float) 2.0);
                if(map.foundTokens == map.maxTokens) tokenText += ". You've found all tokens.";
                Toast.makeText(App.getContext(), tokenText, Toast.LENGTH_SHORT).show();
                Log.v("TOKEN", "Token found");
            }

            map.draw(canvas);
            if(!DEBUG_CONTROLS) {
                ball.move(acceleratorXNew, acceleratorYNew, map, HA);
            } else {
                Point d = ctrl.getDirection();
                ball.move(d.x * 14, d.y * 14, map, HA);
                ctrl.draw(canvas);
            }

            //Draws the ball.
            if(!ballBitmap.isRecycled()) canvas.drawBitmap(ballBitmap, null, ball.getSize(), ball.getColor());

            //Decides what else is drawn depending on the game mode.
            if(gameMode.equals("trailblazer")) {

                if(HA) {
                    //If a token has been found and countdown hasn't ended, the list won't be empty and this runs, drawing the round ring(s) on the screen depending on how many paths the list has at that moment.
                    if(!tokensPathList.isEmpty()) {

                        tokensPath.rewind();

                        for(Path tokenPathFromList : tokensPathList) {
                            tokensPath.addPath(tokenPathFromList);
                        }
                        canvas.clipPath(tokensPath, Region.Op.DIFFERENCE);
                    }
                }
                else {
                    //Gives the gradient paint color based on current information about certain things like ball position and tokens activated.
                    ball.setGradientShaderTrailblazer(tokensPointList);
                }



                //Draws a rectangle covering the whole screen with the newly given gradient paint.
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), ball.getGradient());

                //Set the current path for ball as region.
                region.setPath(ballPath, new Region(0, 0, canvas.getWidth(), canvas.getHeight()));

                //Then put the region to ballPath (looks a bit clumsy and there probably is a better way of doing this, but at least it works and doesn't have big performance issues).
                ballPath = region.getBoundaryPath();

                //Makes a path in the form of a circle around the ball, which will be used as an excluded part when drawing the black rectangle filling the screen.
                ballPath.addCircle(ball.getPosition().x+ball.midPointLength, ball.getPosition().y+ball.midPointLength, ball.gradientFadeLength, Path.Direction.CW);

                //This is the line that makes so the path isn't involved as a part of the black rectangle.
                canvas.clipPath(ballPath, Region.Op.DIFFERENCE);

                if(!HA) {
                    //Takes every point currently in the list from activating tokens and draws a circle around them, making that area not be affected by further drawing this time.
                    if(!(tokensPointList.isEmpty())) {
                        for(final Point p : tokensPointList) {
                            canvas.clipPath(new Path(){{addCircle(p.x, p.y, ball.tokenGradientFadeLength, Path.Direction.CW);}}, Region.Op.DIFFERENCE);
                        }
                    }
                }

                //Fills the entire screen with a black rectangle.
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blackPaint);
            }
            else if(gameMode.equals("glowstick")) {

                if(HA) {
                    //If a token has been found and countdown hasn't ended, the list won't be empty and this runs, drawing the round ring(s) on the screen depending on how many paths the list has at that moment.
                    if(!tokensPathList.isEmpty()) {
                        //Log.v("tokensPathList", "Not empty now!");
                        tokensPath.rewind();

                        for(Path tokenPathFromList : tokensPathList) {
                            tokensPath.addPath(tokenPathFromList);
                            //Log.v("tokensPath content", ""+tokensPath.toString());
                        }
                        canvas.clipPath(tokensPath, Region.Op.DIFFERENCE);
                    }
                }
                else {
                    ball.setGradientShaderGlowstick(tokensPointList);
                }

                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), ball.getGradient());
            }
            else if(gameMode.equals("darkness")) {

                if(HA) {
                    //If a token has been found and countdown hasn't ended, the list won't be empty and this runs, drawing the round ring(s) on the screen depending on how many paths the list has at that moment.
                    if(!tokensPathList.isEmpty()) {
                        //Log.v("tokensPathList", "Not empty now!");
                        tokensPath.rewind();

                        for(Path tokenPathFromList : tokensPathList) {
                            tokensPath.addPath(tokenPathFromList);
                            //Log.v("tokensPath content", ""+tokensPath.toString());
                        }
                        canvas.clipPath(tokensPath, Region.Op.DIFFERENCE);
                    }

                    //Fills the whole screen with a black rectangle.
                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blackPaint);
                }
                else {
                    ball.setGradientShaderDarknessMode(tokensPointList);

                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), ball.getGradient());
                }
            }
            else if(gameMode.equals("lights_on")) {
                //Nothing really needs to be done here, at least not for now.
            }

            if(DEBUG_CONTROLS) {
                ctrl.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(DEBUG_CONTROLS) {
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
        }

        return false;
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

        // check sensor type
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            //Assign original directions
            acceleratorXOriginal = event.values[0];
            acceleratorYOriginal = event.values[1];

            //Fix possible orientation problems.
            fixOrientationProblem();
        }
    }

    public void fixOrientationProblem() {
        if(rotationIndex == 0) {
            acceleratorXNew = acceleratorXOriginal;
            acceleratorYNew = acceleratorYOriginal;
        }
        else if(rotationIndex == 1) {
            acceleratorXNew = (-1)*acceleratorYOriginal;
            acceleratorYNew = acceleratorXOriginal;
        }
        else if(rotationIndex == 2) {
            acceleratorXNew = (-1)*acceleratorXOriginal;
            acceleratorYNew = (-1)*acceleratorYOriginal;
        }
        else if(rotationIndex == 3) {
            acceleratorXNew = acceleratorYOriginal;
            acceleratorYNew = (-1)*acceleratorXOriginal;
        }
        else {
            Log.v("rotationIndex fail", "rotationIndex wasn't 0-3, but instead "+rotationIndex);
        }
    }
}
