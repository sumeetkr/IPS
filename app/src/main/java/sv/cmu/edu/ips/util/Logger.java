package sv.cmu.edu.ips.util;

import android.util.Log;

/**
 * Created by sumeet on 9/10/14.
 */
public class Logger {
    public static final String TAG = "IPS";
    private static final boolean DEBUG = true;

    public static void log(String text){
        Log.d(TAG, text);
    }

    public static void debug(String text){
        if(DEBUG) Log.d(TAG, text);
    }

}

