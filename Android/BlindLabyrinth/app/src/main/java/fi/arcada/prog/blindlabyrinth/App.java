package fi.arcada.prog.blindlabyrinth;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Linus on 11/29/2014.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}