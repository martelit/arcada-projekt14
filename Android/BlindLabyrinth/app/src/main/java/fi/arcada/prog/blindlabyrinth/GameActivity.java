package fi.arcada.prog.blindlabyrinth;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;


public class GameActivity extends Activity {

    public AudioEngine Audio;
    public boolean aeBound = false;

    private ServiceConnection aeConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioEngine.AudioBinder binder = (AudioEngine.AudioBinder) service;
            Audio = binder.getService();
            aeBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            aeBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, AudioEngine.class);
        bindService(intent, aeConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (aeBound) {
            unbindService(aeConnection);
        }
    }
}
