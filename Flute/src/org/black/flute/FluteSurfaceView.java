package org.black.flute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FluteSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Integer screenWidth;
    private Integer screenHeight;

    private Float pointRadius;

    private Circle firstCircle, secondCircle, bottomSemiCircle;

    public FluteSurfaceView(Context context) {
        super(context);
        this.holder = this.getHolder();
        this.holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        this.pointRadius = screenWidth / 8f;

        Log.i(FluteConstant.APP_TAG, "Screen width: " + this.screenWidth);
        Log.i(FluteConstant.APP_TAG, "Screen height: " + this.screenHeight);

        this.firstCircle = new Circle(screenWidth * 0.75f,
                screenHeight * 0.25f, pointRadius);
        this.secondCircle = new Circle(screenWidth * 0.25f,
                screenHeight * 0.75f, pointRadius);
        this.bottomSemiCircle = new Circle(screenWidth / 2, screenHeight,
                screenWidth / 14);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(new MyThread()).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void draw() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(firstCircle.getX(), firstCircle.getY(),
                    firstCircle.getRadius(), mPaint);
            canvas.drawCircle(secondCircle.getX(), secondCircle.getY(),
                    secondCircle.getRadius(), mPaint);

            Path semiCirclePath = new Path();
            semiCirclePath.addCircle(bottomSemiCircle.getX(),
                    bottomSemiCircle.getY(), bottomSemiCircle.getRadius(),
                    Path.Direction.CW);
            canvas.drawPath(semiCirclePath, mPaint);
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private class Circle {
        private float x;
        private float y;
        private float radius;

        public Circle(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRadius() {
            return radius;
        }

        @Override
        public String toString() {
            return "Circle [x=" + x + ", y=" + y + ", radius=" + radius + "]";
        }

    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            draw();
        }

    }

}
