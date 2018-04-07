package leotik.labs.gesturemessenger.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import leotik.labs.gesturemessenger.CanvasView;
import leotik.labs.gesturemessenger.Pixel;
import leotik.labs.gesturemessenger.R;

public class OverlayService extends Service {
    private WindowManager mWindowManager;
    private CanvasView mChatHeadView;
    private ImageButton btnClose;


    private ArrayList<Pixel> pixelList;

    public OverlayService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        pixelList=new ArrayList<Pixel>();
        for(int i=0;i<1000;i++)
        pixelList.add(new Pixel(50,i, Color.RED,4));
        mChatHeadView = new CanvasView(getApplicationContext(),pixelList);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);




        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);
        mChatHeadView.startIt(10);

        btnClose = new ImageButton(this);
        btnClose.setBackground(getDrawable(R.drawable.cancel));
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               stopSelf();
            }
        });
        mWindowManager.addView(btnClose,params1);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null)
        { mWindowManager.removeView(mChatHeadView);
        mWindowManager.removeView(btnClose);}
    }
}
