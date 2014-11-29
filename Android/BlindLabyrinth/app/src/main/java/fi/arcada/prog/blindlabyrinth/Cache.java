package fi.arcada.prog.blindlabyrinth;

/**
 * Created by Linus on 11/29/2014.
 */
public class Cache {
    private static Cache instance = null;
    protected Cache() {
        // Exists only to defeat instantiation.
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
}
