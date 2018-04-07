package leotik.labs.gesturemessenger;

import android.graphics.Color;

/**
 * Created by superuser on 4/7/18.
 */

public class Pixel {
    private float x,y;
    private int color;
    private float radius;
    public Pixel(float x,float y, int color,float radius){
        this.x=x;
        this.y=y;
        this.color=color;
        this.radius=radius;
    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
