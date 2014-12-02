package fi.arcada.prog.blindlabyrinth;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.*;
import java.util.Map;

/**
 * Created by Linus on 11/26/2014.
 */

public class AudioEngine extends Service {
    private final IBinder mBinder = new AudioBinder();

    private final Random mGenerator = new Random();

    protected MediaPlayer musicPlayer = new MediaPlayer();
    protected SoundPool soundPlayer;
    protected float soundVolume = (float) 1.0;

    protected HashMap<String, Integer> sounds = new HashMap<String, Integer>();
    protected HashMap<String, Integer> music;
    protected Iterator musicIterator;

    public class AudioBinder extends Binder {
        AudioEngine getService() {
            return AudioEngine.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        musicPlayer.stop();
        musicPlayer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public AudioEngine() {
        HashMap<String, Integer> music = new HashMap<String, Integer>();
        music.put("music0", R.raw.qcl1);
        music.put("music1", R.raw.qcl2);
        music.put("music2", R.raw.qcl0);


        HashMap<String, Integer> sounds = new HashMap<String, Integer>();
        sounds.put("move", R.raw.rollin);

        init(music, sounds);
        playMusic();
    }

    public void init(HashMap<String, Integer> musicRes, HashMap<String, Integer> soundRes) {
        soundPlayer = new SoundPool(soundRes.size(), AudioManager.STREAM_MUSIC, 0);
        setMusicVolume((float)0.8);
        for (Map.Entry<String, Integer> entry : soundRes.entrySet()) {
            int id = soundPlayer.load(App.getContext(), entry.getValue(), 1);
            sounds.put(entry.getKey(), id);
        }

        music = musicRes;

        resetIterator();
    }

    public void onPause() {
        if(musicPlayer != null) musicPlayer.pause();
    }

    public void onPlay() {
        if(musicPlayer != null) musicPlayer.start();
    }

    public void setMusicVolume(float vol) {
        musicPlayer.setVolume(vol, vol);
    }

    public void setSoundVolume(float vol) {
        soundVolume = vol;
    }

    public void playMusic() {
        playNext();
    }

    public void playMusic(String tag) {
        Integer id = music.get(tag);
        if(id != 0) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = musicPlayer.create(App.getContext(), id);
            musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }
    }

    private void playNext() {
        if(!musicIterator.hasNext()) resetIterator();
        Map.Entry<String, Integer> entry = (Map.Entry) musicIterator.next();
        playMusic(entry.getKey());
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });
    }

    public void resetIterator() {
        musicIterator = (Iterator) music.entrySet().iterator();
    }

    public void playSound(String tag, float rate) {
        Integer id = sounds.get(tag);
        if(id != 0) {
            soundPlayer.play(id, soundVolume, soundVolume, 0, 0, rate);
        }
    }
}
