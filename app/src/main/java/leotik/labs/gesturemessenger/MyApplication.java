package leotik.labs.gesturemessenger;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.drawee.backends.pipeline.Fresco;

import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        RealtimeDB.getInstance(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}