package leotik.labs.gesturemessenger.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Views.GestureDrawView;

public class DrawActivity extends AppCompatActivity implements DownloadListner {

    public GestureDrawView dv;
    private FloatingActionButton send_fab;
    private Paint mPaint;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;



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
        final String Email = getIntent().getStringExtra("email");
        if (Email == null)
            finish();
        send_fab = findViewById(R.id.send_fab);
        send_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showaddDialog(true);
                RealtimeDB.getInstance(DrawActivity.this).sendMessage(Email, dv.gesture + "s" + dv.size, DrawActivity.this);
            }
        });
        progressBar = new ProgressBar(this);
        alertDialog = new AlertDialog.Builder(this)
                //.setTitle("Sendin")
                .setMessage("Sending Message")
                .setView(progressBar)
                .create();
    }

    public void showaddDialog(Boolean show) {
        if (show) {
            send_fab.hide();
            alertDialog.show();
        } else {
            alertDialog.hide();
            send_fab.show();
        }
    }

    @Override
    public void OnDownloadResult(int ResponseCode, Object Response) {
        showaddDialog(false);

    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        showaddDialog(false);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Oops !!")
                .setMessage("Something went wrong, Message not sent")
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }
}