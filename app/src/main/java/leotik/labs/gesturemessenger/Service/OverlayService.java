package leotik.labs.gesturemessenger.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Views.GestureOverlayView;

import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;

public class OverlayService extends Service {
    private WindowManager mWindowManager;
    private GestureOverlayView mChatHeadView;
    private ImageButton btnClose;


    public OverlayService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mChatHeadView = new GestureOverlayView(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= 23)
            Log.e("Ritik", "Perm: " + Settings.canDrawOverlays(this));
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                //WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        params1.gravity = Gravity.TOP | Gravity.RIGHT;
        btnClose = new ImageButton(this);
        btnClose.setBackground(getDrawable(R.drawable.cancel));
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        mWindowManager.addView(btnClose, params1);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) {
            mWindowManager.removeView(mChatHeadView);
            mWindowManager.removeView(btnClose);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo service closes on ram clear, showing notification can be possible fix, doesnt work on oreo
        startForeground(36, getNotification());
        mChatHeadView.startIt(100, intent.getStringExtra("gesture"));
        return super.onStartCommand(intent, flags, startId);
    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("snap map fake location");
        Notification notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //todo change these string
        String name = "Notification Body";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("Channel name", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "Channel Name";
    }
}