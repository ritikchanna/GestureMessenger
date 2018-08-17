package leotik.labs.gesturemessenger.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Logging {


    public void logDebug(Class source, String Message) {
        Log.d(source.getSimpleName(), Message);
    }

    public void logError(Class source, Exception e) {
        Log.e(source.getSimpleName(), e.getMessage());
        e.printStackTrace();
    }

    public void logInfo(Class source, String Message) {
        Log.i(source.getSimpleName(), Message);
        Log.d(source.getSimpleName(), Message);
    }

    public void toastDebug(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }


}
