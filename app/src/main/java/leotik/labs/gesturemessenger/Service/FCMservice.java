package leotik.labs.gesturemessenger.Service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class FCMservice extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RealtimeDB.getInstance(getApplicationContext()).updatetokenonServer(s, "chnritik@gmail.com");

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }


}
