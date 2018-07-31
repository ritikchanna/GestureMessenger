package leotik.labs.gesturemessenger.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

import leotik.labs.gesturemessenger.Pixel;

/**
 * Created by superuser on 4/6/18.
 */

public class GestureOverlayView extends View implements Runnable {
    private int mWidth;
    private int mHeight;
    private List<Pixel> pixelList;
    private int pixelCounter = 0;
    private Pixel pixel;
    private long delay;
    private Thread animator = null;

    public GestureOverlayView(Context context, List<Pixel> pixels) {
        super(context);
        pixelList = pixels;
    }

    public GestureOverlayView(Context context, AttributeSet attribs, List<Pixel> pixels) {
        super(context, attribs);
        pixelList = pixels;
    }

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
        paint.setStrokeWidth(4f);
        for (int i = 0; pixelCounter > i; i++) {
            pixel = pixelList.get(i);
            paint.setColor(pixel.getColor());
            canvas.drawPoint(pixel.getX(), pixel.getY(), paint);
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawLine(0, 0, (mWidth / pixelList.size()) * pixelCounter, 0, paint);

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
        while (pixelCounter < pixelList.size()) {

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
                ;
            }
        }
    }

    // Method to start animation loop
    public void startIt(long delay) {
        this.delay = delay;
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
