package fi.arcada.prog.blindlabyrinth;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class GameActivity extends Activity {

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(this.getClass().toString(), "resume");
        Cache.getInstance().inFocus = true;
        if (Cache.getInstance().aeBound) {
            Cache.getInstance().Audio.onPlay();
        }
    }

    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().toString(), "pause");
        Cache.getInstance().inFocus = false;
        if (Cache.getInstance().aeBound) {

            //Delay the pausing of the music by 0.1s too see if we are just switching GameActivity
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!Cache.getInstance().inFocus) {
                        Cache.getInstance().Audio.onPause();
                    }
                }
            }, 100);
        }
    }

}
