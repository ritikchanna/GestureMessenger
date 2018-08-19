package leotik.labs.gesturemessenger.Service;

import android.content.Intent;
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
        if (data.containsKey("type")) {
            String type = data.get("type");
            switch (type) {
                case "message":
                    Log.d("Ritik", "onMessageReceived: message");
                    String id = data.get("id");
                    RealtimeDB.getInstance(getApplicationContext()).displayGesture(id);
                    break;
                case "friend":
                    Log.d("Ritik", "onMessageReceived: message");
                    Log.d("Ritik", "onMessageReceived: " + data.get("user") + data.get("user2") + data.get("status") + data.get("time"));
                    break;
                default:
                    Log.e("Ritik", "onMessageReceived: invalid type value " + data.toString());
                    break;
            }

        } else
            Log.e("Ritik", "onMessageReceived: No type value " + data.toString());
    }


}
