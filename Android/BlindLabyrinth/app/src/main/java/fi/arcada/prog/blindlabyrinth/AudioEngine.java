package fi.arcada.prog.blindlabyrinth;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.*;
import java.util.Map;

/**
 * Created by Linus on 11/26/2014.
 */

public class AudioEngine extends Service {
    private final IBinder mBinder = new AudioBinder();

    protected MediaPlayer musicPlayer;
    protected SoundPool soundPlayer;
    protected float soundVolume = (float) 1.0;

    protected HashMap<String, Integer> sounds = new HashMap<String, Integer>();
    protected LinkedHashMap<String, Integer> music;
    protected Iterator musicIterator;

    protected Random randomGenerator;

    protected int moveId;
    protected int moveDuration; //The normal duration
    protected int moveTime; //The calculated duration
    protected boolean movePlaying = false;
    final Handler moveHandler = new Handler();
    protected int movementMax = 7;

    final Handler wallHandler = new Handler();
    protected boolean wallPlay = true;

    protected boolean musicOn = true;
    protected boolean soundOn = true;

    protected boolean hasPlayed = false;


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
        music = new LinkedHashMap<String, Integer>();
        music.put("music0", R.raw.qcl1);
        music.put("music1", R.raw.qcl2);
        music.put("music2", R.raw.qcl0);

        HashMap<String, Integer> soundRes = new HashMap<String, Integer>();
        soundRes.put("move", R.raw.rolling);
        soundRes.put("wall", R.raw.wall);
        soundRes.put("token", R.raw.tokenfound);
        soundRes.put("levelcompleted", R.raw.levelcompleted);

        MediaPlayer temp = MediaPlayer.create(App.getContext(), soundRes.get("move"));
        moveDuration = temp.getDuration();
        temp.release();
        temp = null;

        randomGenerator = new Random();

        soundPlayer = new SoundPool(soundRes.size(), AudioManager.STREAM_MUSIC, 0);
        for (Map.Entry<String, Integer> entry : soundRes.entrySet()) {
            int id = soundPlayer.load(App.getContext(), entry.getValue(), 1);
            sounds.put(entry.getKey(), id);
        }

        //Reading music/sound settings here
        SharedPreferences prefs = Cache.getInstance().getPref();
        musicOn = (prefs.getInt("music", 1) == 1);
        soundOn = (prefs.getInt("sound", 1) == 1);

        reset();
    }

    public void reset() {
        resetIterator();

        musicPlayer = new MediaPlayer();
        setMusicVolume((float)0.8);

        if(musicOn) playMusic();
    }

    public void release() {

    }

    public void onPause() {

        if(musicPlayer != null) musicPlayer.pause();
        stopMove();
    }

    public void onPlay() {
        if(musicOn && musicPlayer != null) {
            musicPlayer.start();
        }
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
        hasPlayed = true;

        Integer id = music.get(tag);
        if(id != 0) {
            if(musicPlayer != null) {
                musicPlayer.release();
            }
            musicPlayer = MediaPlayer.create(App.getContext(), id);
            musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }

    }

    private void playNext() {
        Map.Entry<String, Integer> entry = (Map.Entry) musicIterator.next();
        if(Cache.getInstance().getPref().getInt("lastsong", 0) == entry.getValue()) {
            playNext();
            return;
        }
        if(!musicIterator.hasNext()) resetIterator();

        playMusic(entry.getKey());
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });
        SharedPreferences.Editor e = Cache.getInstance().getEdit();
        e.putInt("lastsong", entry.getValue());
        e.commit();
    }

    public void resetIterator() {
        musicIterator = (Iterator) music.entrySet().iterator();
    }

    public void playSound(String tag, float rate) {
        if(!soundOn) return;
        Integer id = sounds.get(tag);
        if(id != 0) {
            soundPlayer.play(id, soundVolume, soundVolume, 0, 0, rate);
        }
    }

    public void playSoundRR(String tag) { //Random Rate
        float rate = (randomGenerator.nextFloat() * (float)1.5) + (float)0.5;
        playSound(tag, rate);
    }

    private ArrayList<Integer> moves = new ArrayList<Integer>();
    private float lastRate = 1;
    private boolean movePaused = false;

    public void playMove(int movementX, int movementY) {
        if(!soundOn) return;

        if(movementX < 0) movementX *= -1;
        if(movementY < 0) movementY *= -1;
        int movement = movementX + movementY;


        int sum = 0;
        int amount = 0;
        int average = 0;
        int max = 0;
        int min = 0;
        for(Integer i : moves) {
            sum += i;
            amount++;
            if(i > max) max = i;
            if(i < min) min = i;
        }
        if(amount != 0) average = sum / amount;
        Log.v("move", movement + " / " + average);

        if(!movePlaying) {
            moveId = soundPlayer.play(sounds.get("move"), soundVolume, soundVolume, 1, -1, lastRate);
            movePlaying = true;
        }

        if(movement < min) {
            soundPlayer.pause(moveId);
            movePaused = true;
            lastRate = (float)0.5;
        } else {
            if(movement > average) {
                lastRate += 0.1; // (float) 1.2;
            } else if(movement < average) {
                lastRate -= 0.1; // (float) 0.5;
            }
            if(movePaused) {
                soundPlayer.resume(moveId);
                movePaused = false;
            }
            if(lastRate > 1.2) lastRate = (float)1.2;
            if(lastRate < 0.5) lastRate = (float)0.5;
            soundPlayer.setRate(moveId, lastRate);
        }


        if(moves.size() > 10) moves.remove(1); //limit
        moves.add(movement);
    }

    public void stopMove() {
        soundPlayer.stop(moveId);
        movePlaying = false;
        lastRate = (float)0.5;
    }

    public void playMove1(int movement) {
        if(!soundOn) return;
        if(movement < 0) movement *= -1;
        if(movement < 2) {
            soundPlayer.stop(moveId);
            return;
        }
        float rate = (float)1.0 ;//(movement / movementMax) / 2;
        float volume = soundVolume * (float)0.8;
        Log.v("rate", Float.toString(rate));

        if(movement > movementMax) movementMax = movement;
        if(movePlaying) {
            soundPlayer.setRate(moveId, rate);
        } else {
            moveId = soundPlayer.play(sounds.get("move"), volume, volume, 0, 0, rate);
            moveTime = moveDuration * (int)rate;
            movePlaying = true;
            moveHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    movePlaying = false;
                }
            }, moveTime);
        }
    }

    public void playWall() {
        if(wallPlay) {
            playSoundRR("wall");
            wallPlay = false;
        }
        //stop the sound from replaying all the time
        wallHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wallPlay = true;
            }
        }, 100);
    }

    public void setMusic(boolean on) {
        musicOn = on;
        if(on) {
           if(hasPlayed) {
                onPlay();
            } else {
                reset();
            }

        } else {
            onPause();
        }
    }

    public void setSound(boolean on) {
        soundOn = on;
    }
}
