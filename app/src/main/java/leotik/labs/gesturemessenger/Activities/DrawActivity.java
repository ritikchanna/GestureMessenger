package leotik.labs.gesturemessenger.Activities;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Views.GestureDrawView;

public class DrawActivity extends AppCompatActivity {

    public GestureDrawView dv;
    private FloatingActionButton send_fab;
    private Paint mPaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        dv = findViewById(R.id.draw_view);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        dv.setPaint(mPaint);
        // RealtimeDB.getInstance(DrawActivity.this).setValueEventListner("chnritik@gmail.com");
        send_fab = findViewById(R.id.send_fab);
        send_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealtimeDB.getInstance(DrawActivity.this).sendMessage("android studio", "chnritik@gmail.com", dv.gesture + "s" + dv.size);
            }
        });
    }


}