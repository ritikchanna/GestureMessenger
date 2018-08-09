package leotik.labs.gesturemessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import leotik.labs.gesturemessenger.Service.BackgroundService;

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Ritik", "onReceive: Starting Service");
        context.startService(new Intent(context, BackgroundService.class));
    }
}
