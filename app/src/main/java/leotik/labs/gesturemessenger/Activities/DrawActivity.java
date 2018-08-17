package leotik.labs.gesturemessenger.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;
import leotik.labs.gesturemessenger.Views.GestureDrawView;

public class DrawActivity extends AppCompatActivity implements DownloadListner {

    public GestureDrawView dv;
    private ImageButton send_btn;
    private Paint mPaint;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private RelativeLayout headerView;



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
        final String Phone = getIntent().getStringExtra("phone");
        final String Name = getIntent().getStringExtra("name");
        final String Photo = getIntent().getStringExtra("photo");
        if (Phone == null)
            finish();
        progressBar = new ProgressBar(this);
        alertDialog = new AlertDialog.Builder(this)
                //.setTitle("Sendin")
                .setMessage("Sending Message")
                .setView(progressBar)
                .create();
        headerView = findViewById(R.id.header_VIEW);
        headerView.findViewById(R.id.overlay_close_btn).setVisibility(View.GONE);
        if (Photo == null || Photo.equals("") || Photo.equals("null"))
            ((SimpleDraweeView) headerView.findViewById(R.id.overlay_photo)).setImageURI(Uri.parse("http://flathash.com/" + Phone + ".png"));
        else
            ((SimpleDraweeView) headerView.findViewById(R.id.overlay_photo)).setImageURI(Uri.parse(Photo));
        if (Name == null || Name.equals("") || Name.equals("null"))
            ((TextView) headerView.findViewById(R.id.overlay_name)).setText(Phone);
        else
            ((TextView) headerView.findViewById(R.id.overlay_name)).setText(Name);

        send_btn = headerView.findViewById(R.id.overlay_send_btn);
        send_btn.setVisibility(View.VISIBLE);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showsendDialog(true);
                RealtimeDB.getInstance(DrawActivity.this).sendMessage(Phone, dv.gesture + "s" + dv.size, DrawActivity.this);
            }
        });
    }

    public void showsendDialog(Boolean show) {
        if (show) {
            send_btn.setVisibility(View.GONE);
            alertDialog.show();
        } else {
            alertDialog.hide();
            send_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnDownloadResult(int ResponseCode, Object Response) {
        showsendDialog(false);

    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        showsendDialog(false);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Oops !!")
                .setMessage("Something went wrong, Message not sent")
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }
}