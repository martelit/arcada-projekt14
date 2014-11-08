package fi.arcada.prog.blindlabyrinth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * Created by rusty on 8.11.2014.
 */

public class GameView extends View implements Runnable {

    public Bitmap ballBitmap;
    public Ball ball;

    //Default values given to a created ball.
    //Free to be changed later on.
    public int ballXStartPosition = 20, ballYStartPosition = 20;
    public int ballWidth = 50, ballHeight = 50;
    public int ballXStartSpeed = 4, ballYStartSpeed = 6;

    public GameView(Context context) {
        super(context);

        //images used for background and bitmaps are stored in app/src/main/java/res/drawable-hdpi
        //this.setBackgroundResource(R.drawable.nameOfChosenLabyrinthBackgroundForGameScreen);
        ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

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
        ball.move();
        canvas.drawBitmap(ballBitmap, null, ball.getSize(), ball.getColor());
    }
}
