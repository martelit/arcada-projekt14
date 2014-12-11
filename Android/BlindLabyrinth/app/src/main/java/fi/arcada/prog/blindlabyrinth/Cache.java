package fi.arcada.prog.blindlabyrinth;

import android.content.SharedPreferences;

/**
 * Created by Linus on 11/29/2014.
 */
public class Cache {
    private static Cache instance = null;
    protected Cache() {

    }
    public static Cache getInstance() {
        if(instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public AudioEngine Audio;
    public boolean aeBound = false;

    public boolean inFocus = false; //is our application in focus? [Used by GameActivity]

    final static String SETTINGS = "blindLabyrinthPref";

    public SharedPreferences getPref() {
        return App.getContext().getSharedPreferences(Cache.SETTINGS, 0);
    }
    public SharedPreferences.Editor getEdit() {
           return getPref().edit();
    }


}
