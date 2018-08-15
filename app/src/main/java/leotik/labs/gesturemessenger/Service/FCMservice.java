package leotik.labs.gesturemessenger.Service;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class FCMservice extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RealtimeDB.getInstance(getApplicationContext()).updatetokenonServer(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);

            startService(new Intent(getApplicationContext(), BackgroundService.class));


        Map<String, String> data = remoteMessage.getData();
        Log.d("Ritik", "onMessageReceived: +" + data.toString());
        if (data.containsKey("id")) {
            Log.d("Ritik", "onMessageReceived: id found");
            String id = data.get("id");
            RealtimeDB.getInstance(getApplicationContext()).displayGesture(id);

        }
    }


}
