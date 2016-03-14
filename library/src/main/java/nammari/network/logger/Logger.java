package nammari.network.logger;

import android.util.Log;

/**
 * Created by nammari on 8/12/14.
 */
public class Logger {
    private static boolean ENABLED = false;

    public static void logDebug(String tag, String str) {
        if (ENABLED)
            Log.d(tag, str);
    }

    public static void logError(String tag, String str) {
        if (ENABLED)
            Log.e(tag, str);
    }

    public static void logInfo(String tag, String str) {
        if (ENABLED)
            Log.i(tag, str);
    }

}
