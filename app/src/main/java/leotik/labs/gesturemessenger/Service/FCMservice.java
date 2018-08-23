package leotik.labs.gesturemessenger.Service;

import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Constants;
import leotik.labs.gesturemessenger.Util.DatabaseHelper;
import leotik.labs.gesturemessenger.Util.NotificationUtils;
import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class FCMservice extends FirebaseMessagingService implements DownloadListner {
    private DatabaseHelper databaseHelper;
    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RealtimeDB.getInstance(getApplicationContext()).updatetokenonServer(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        if (notificationUtils == null) {
            notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.setEnableLight(true);
            notificationUtils.setEnableSound(true);
            notificationUtils.setEnableVibration(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationUtils.setChannelId(getString(R.string.friend_notification_channel_id));
                notificationUtils.setChannelName(getString(R.string.friend_notification_channel));
                notificationUtils.setChannelImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationUtils.setChannelDescription(getString(R.string.friend_notification_channel_description));
            }

        }
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(getApplicationContext());
        RealtimeDB realtimeDB = RealtimeDB.getInstance(getApplicationContext());

//        startService(new Intent(getApplicationContext(), BackgroundService.class));


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
                    Log.d("Ritik", "onMessageReceived: friend");
                    String user = data.get("user");
                    String user2 = data.get("user2");
                    String newstatus = data.get("newStatus");
                    String oldstatus = data.get("oldStatus");
                    if (newstatus.equals("0")) {
                        databaseHelper.deleteUser(user2);
                    } else if (newstatus.equals("r")) {
                        //new friend request recieved
                        realtimeDB.getUser(this, Constants.GET_USER, user2, "r");

                    } else if (oldstatus.equals("s") && newstatus.equals("f")) {
                        notificationUtils.setContentTitle(getString(R.string.notification_title_friend_request_accept));
                        UserPOJO friend = databaseHelper.getUser(user2);
                        String name;
                        if (friend == null)
                            name = user2;
                        else
                            name = friend.getN();
                        notificationUtils.setContentText(getString(R.string.notification_friend_request_accept, name));
                        notificationUtils.show();
                    }


                    break;
                default:
                    Log.e("Ritik", "onMessageReceived: invalid type value " + data.toString());
                    break;
            }

        } else
            Log.e("Ritik", "onMessageReceived: No type value " + data.toString());
    }

    @Override
    public void OnDownloadResult(int ResponseCode, Object Response) {
        if (ResponseCode == Constants.GET_USER) {
            UserPOJO user = (UserPOJO) Response;
            databaseHelper.insertUser(user, null);
            notificationUtils.setContentTitle(getString(R.string.notification_title_new_request));
            notificationUtils.setContentText(getString(R.string.notification_new_request, user.getN()));
            notificationUtils.show();
        }
    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {

    }
}