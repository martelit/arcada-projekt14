package fi.arcada.prog.blindlabyrinth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class MainActivity extends GameActivity {

    public Timer musicTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Bind controls to needed actions here, button example -LL
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
        ((Button) findViewById(R.id.btnaccel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccelerometer ();
            }
        });
        ((Button) findViewById(R.id.btnGameViewBlank)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameViewBlank();
            }
        });
        ((Button) findViewById(R.id.btnSettings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings();
            }
        });

        musicTimer.schedule(musicInit, 500, 500);
    }

    private TimerTask musicInit = new TimerTask() {
        @Override
        public void run() {
            if(aeBound) {
                //Initialize the audio engine
                HashMap<String, Integer> music = new HashMap<String, Integer>();
                music.put("music0", R.raw.qcl0);
                music.put("music1", R.raw.qcl1);
                music.put("music2", R.raw.qcl2);


                HashMap<String, Integer> sounds = new HashMap<String, Integer>();
                sounds.put("move", R.raw.rollin);

                Audio.init(music, sounds);
                Audio.playMusic();
                musicTimer.cancel();
            }
        }
    };

    public void startGame() {
        Audio.playSound("move", (float)2.0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void startAccelerometer() {
        Intent intent = new Intent(getApplicationContext(), Accelerometer.class);
        startActivity(intent);
    }
    public void startGameViewBlank() {
        Intent intent = new Intent(getApplicationContext(), GameViewBlank.class);
        startActivity(intent);
    }
    public void startSettings() {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }
}
