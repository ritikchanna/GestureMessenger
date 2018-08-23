package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

public class Logging {
    private static Boolean debug = true;


    public static void logDebug(Class source, String Message) {
        if (debug)
            Log.d(source.getSimpleName(), Message);
    }

    public static void logError(Class source, Exception e) {
        if (debug) {
            Log.e(source.getSimpleName(), e.getMessage());
            e.printStackTrace();
        } else
            Crashlytics.logException(e);
    }

    public static void logInfo(Class source, String Message) {
        if (debug)
            Log.i(source.getSimpleName(), Message);
    }

    public static void toastDebug(Context context, String Message) {
        if (debug)
            Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }


}
