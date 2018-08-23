package leotik.labs.gesturemessenger.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import leotik.labs.gesturemessenger.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;

public class NotificationUtils {

    private Context mcontext;
    private NotificationManager notificationManager;


    private String CHANNEL_ID;
    private String CHANNEL_NAME;
    private int CHANNEL_IMPORTANCE;
    private String CHANNEL_DESCRIPTION;


    private Uri SOUND;
    private int LIGHT_COLOR = Color.GREEN;
    private int NOTIFICATION_ICON = R.mipmap.ic_launcher;


    private int NOTIFICATION_ID;
    private String CONTENT_TITLE;
    private String CONTENT_TEXT;


    private Boolean enableSound;
    private Boolean enableVibration;
    private Boolean enableLight;


    public NotificationUtils(Context context) {
        mcontext = context;
        notificationManager = (NotificationManager) mcontext.getSystemService(NOTIFICATION_SERVICE);
        SOUND = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mcontext.getPackageName() + "/" + R.raw.notification);
    }


    public Notification getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationChannel.enableLights(enableLight);
            notificationChannel.setLightColor(LIGHT_COLOR);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000});
            notificationChannel.enableVibration(enableVibration);
            if (enableSound)
                notificationChannel.setSound(SOUND, attributes);
            notificationChannel.setLockscreenVisibility(VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, CHANNEL_ID);
        if (enableVibration)
            builder.setVibrate(new long[]{100, 200});
        if (enableSound)
            builder.setSound(SOUND);
        if (enableLight)
            builder.setLights(LIGHT_COLOR, 500, 500);

        builder.setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(CONTENT_TEXT);


        return builder.build();

    }


    public void show() {


        notificationManager.notify(NOTIFICATION_ID, getNotification());


    }

    public String getChannelId() {
        return CHANNEL_ID;
    }

    public void setChannelId(String channelId) {
        CHANNEL_ID = channelId;
    }

    public String getChannelName() {
        return CHANNEL_NAME;
    }

    public void setChannelName(String channelName) {
        CHANNEL_NAME = channelName;
    }

    public int getChannelImportance() {
        return CHANNEL_IMPORTANCE;
    }

    public void setChannelImportance(int channelImportance) {
        CHANNEL_IMPORTANCE = channelImportance;
    }

    public String getChannelDescription() {
        return CHANNEL_DESCRIPTION;
    }

    public void setChannelDescription(String channelDescription) {
        CHANNEL_DESCRIPTION = channelDescription;
    }

    public Uri getSOUND() {
        return SOUND;
    }

    public void setSOUND(Uri SOUND) {
        this.SOUND = SOUND;
    }

    public int getNotificationIcon() {
        return NOTIFICATION_ICON;
    }

    public void setNotificationIcon(int notificationIcon) {
        NOTIFICATION_ICON = notificationIcon;
    }

    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public void setNotificationId(int notificationId) {
        NOTIFICATION_ID = notificationId;
    }

    public String getContentTitle() {
        return CONTENT_TITLE;
    }

    public void setContentTitle(String contentTitle) {
        CONTENT_TITLE = contentTitle;
    }

    public String getContentText() {
        return CONTENT_TEXT;
    }

    public void setContentText(String contentText) {
        CONTENT_TEXT = contentText;
    }

    public int getLightColor() {
        return LIGHT_COLOR;
    }

    public void setLightColor(int lightColor) {
        LIGHT_COLOR = lightColor;
    }

    public Boolean getEnableSound() {
        return enableSound;
    }

    public void setEnableSound(Boolean enableSound) {
        this.enableSound = enableSound;
    }

    public Boolean getEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(Boolean enableVibration) {
        this.enableVibration = enableVibration;
    }

    public Boolean getEnableLight() {
        return enableLight;
    }

    public void setEnableLight(Boolean enableLight) {
        this.enableLight = enableLight;
    }
}
