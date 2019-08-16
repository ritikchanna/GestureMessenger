package leotik.labs.gesturemessenger.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.facebook.drawee.view.SimpleDraweeView;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Logging;
import leotik.labs.gesturemessenger.Views.GestureOverlayView;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;

public class OverlayService extends Service {
    private WindowManager mWindowManager;
    private GestureOverlayView mChatHeadView;
    private View HeaderView;
    private WindowManager.LayoutParams params;

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
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //Add the view to the window.
        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                //WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_PHONE,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);


        params1.gravity = Gravity.TOP;
        HeaderView = LayoutInflater.from(this).inflate(R.layout.overlay_header, null);
        HeaderView.findViewById(R.id.overlay_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        mWindowManager.addView(HeaderView, params1);

    }


    @Override
    public void onDestroy() {
        Logging.logDebug(OverlayService.class, "onDestroy");
        super.onDestroy();
        if (mChatHeadView != null) {
            mWindowManager.removeView(mChatHeadView);
            mWindowManager.removeView(HeaderView);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //todo service closes on ram clear, showing notification can be possible fix, doesnt work on oreo

        mWindowManager.removeView(mChatHeadView);
        mChatHeadView = new GestureOverlayView(getApplicationContext());
        mWindowManager.addView(mChatHeadView, params);

        startForeground(36, getNotification());
        String name = intent.getStringExtra("sender_name");
        ((TextView) HeaderView.findViewById(R.id.overlay_name)).setText(name);
        String url = intent.getStringExtra("sender_picture");
        if (url == null || url.equals("") || url.equals("null"))
            ((SimpleDraweeView) HeaderView.findViewById(R.id.overlay_photo)).setImageURI(Uri.parse("http://flathash.com/" + name + ".png"));
        else
            ((SimpleDraweeView) HeaderView.findViewById(R.id.overlay_photo)).setImageURI(Uri.parse(url));

        mChatHeadView.startIt(100, intent.getStringExtra("gesture"));
        return Service.START_STICKY;

    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = createChannel();
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mBuilder.setChannelId(getString(R.string.gesture_notification_channel));
        Notification notification = mBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
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
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(getString(R.string.gesture_notification_channel), getString(R.string.gesture_notification), importance);

        mChannel.enableLights(false);
        // mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return mChannel.getName().toString();
    }

}