package leotik.labs.gesturemessenger.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
/**
 * Created by superuser on 4/6/18.
 */

public class GestureOverlayView extends View implements Runnable {
    private int mWidth;
    private int mHeight;
    private int pixelCounter = 0;
    private long delay;
    private Thread animator = null;
    private String mgesture;
    //=" x387y1122;x383y1091;x380y1063;x376y1034;x374y1009;x372y988;x370y968;x370y951;x370y937;x372y922;x374y910;x377y897;x381y883;x385y870;x391y858;x402y838;x411y822;x423y807;x433y798;x452y787;x474y779;x489y779;x503y782;x514y792;x522y803;x530y822;x538y850;x542y883;x541y910;x537y932;x530y946;x521y957;x509y966;x492y975;x477y982;x463y987;x449y991;x434y996;x422y998;x402y1001;x403y987;x425y979;x447y980;x461y984;x477y993;x495y1007;x505y1017;x515y1027;x523y1037;x538y1053;x553y1068;x571y1081;x593y1087;s53";
    private long size;

    public GestureOverlayView(Context context) {
        super(context);

    }

//    public GestureOverlayView(Context context, AttributeSet attribs,String gesture) {
//        super(context, attribs);
//        mgesture= gesture;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        canvas.drawLine(0, 0, mWidth, mHeight, paint);
        canvas.drawLine(mWidth, 0, 0, mHeight, paint);
        paint.setColor(Color.parseColor("#d3d3d3"));
        canvas.drawLine(0, 0, mWidth, 0, paint);
        //TODO skip some pixels to improve performance
        //TODO use bitmap to avoid redraw everytime ?
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeWidth(12);
        int x = 0;
        int y = 0;
        int end = 0;
        int pixelx;
        int pixely;
        for (int i = 0; pixelCounter > i; i++) {
            Log.d("Ritik", "onDraw: index" + x + "  " + y);
            x = mgesture.indexOf('x', x + 1);
            y = mgesture.indexOf('y', y + 1);
            end = mgesture.indexOf(';', end + 1);
            pixelx = Integer.parseInt(mgesture.substring(x + 1, y));
            pixely = Integer.parseInt(mgesture.substring(y + 1, end));
            paint.setColor(Color.RED);
            Log.d("Ritik", "onDraw: " + pixelx + "    " + pixely);
            canvas.drawPoint(pixelx, pixely, paint);
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawLine(0, 0, (mWidth / size) * pixelCounter, 0, paint);
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

    }

    @Override
    public void run() {
        Log.d("Ritik", "Delay " + delay);
        while (pixelCounter < size) {

            Log.d("Ritik", "Looping " + pixelCounter);


            // Move planet by dTheta and compute new X and Y

            pixelCounter++;

            // Must use postInvalidate() rather than invalidate() to request redraw since
            // this is invoked from different thread than the one that created the View

            postInvalidate();

            // Wait then execute it again
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {

            }
        }
    }

    // Method to start animation loop
    public void startIt(long delay, String gesture) {
        this.delay = delay;
        mgesture = gesture;
        size = Long.parseLong(mgesture.substring(mgesture.lastIndexOf('s') + 1));
        animator = new Thread(this);
        animator.start();
    }

//    // Method to stop animation loop
//    public void stopLooper() {
//        please_top = true;
//    }

//    // Method to resume animation loop
//    public void startLooper(long delay) {
//        please_stop = false;
//        if (animator == null) {
//            startIt(delay);
//        }
//    }


}
