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
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class MainActivity extends GameActivity {

    private ServiceConnection aeConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioEngine.AudioBinder binder = (AudioEngine.AudioBinder) service;
            Cache.getInstance().Audio = binder.getService();
            Cache.getInstance().aeBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Cache.getInstance().aeBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Cache.getInstance().aeBound) {
            unbindService(aeConnection);
            Cache.getInstance().aeBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Cache.getInstance().aeBound) {
            Intent intent = new Intent(this, AudioEngine.class);
            bindService(intent, aeConnection, Context.BIND_AUTO_CREATE);
        }

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
        ((ImageButton) findViewById(R.id.btnGameViewBlank)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameViewBlank();
            }
        });
        ((ImageButton) findViewById(R.id.btnSettings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings();
            }
        });
    }


    public void startGame() {
        Cache.getInstance().Audio.playSound("move", (float)2.0);
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
