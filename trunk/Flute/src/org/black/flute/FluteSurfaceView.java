package org.black.flute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
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

        this.firstCircle = new Circle(screenWidth * 0.67f,
                screenHeight * 0.33f, pointRadius);
        this.secondCircle = new Circle(screenWidth * 0.33f,
                screenHeight * 0.67f, pointRadius);
        this.bottomSemiCircle = new Circle(screenWidth / 2, screenHeight,
                screenWidth / 14);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * A public method for audio input to draw.
     */
    public void draw(double audioVelocity) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);

            boolean touchOnFirstCircle = false;
            boolean touchOnSecondCircle = false;

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

            MotionEvent motionEvent = FluteGlobalValue.getMotionEvent();
            if (motionEvent != null
                    && motionEvent.getAction() != MotionEvent.ACTION_UP) {
                int pointCount = motionEvent.getPointerCount();
                if (pointCount > 0) {
                    for (int i = 0; i < pointCount; i++) {
                        float pointX = motionEvent.getX(i);
                        float pointY = motionEvent.getY(i);

                        double firstDistance = Math.sqrt(Math.pow(pointX
                                - firstCircle.getX(), 2)
                                + Math.pow(pointY - firstCircle.getY(), 2));
                        if (firstDistance >= 0
                                && firstDistance < firstCircle.getRadius() - 5) {
                            touchOnFirstCircle = true;
                        }

                        double secondDistance = Math.sqrt(Math.pow(pointX
                                - secondCircle.getX(), 2)
                                + Math.pow(pointY - secondCircle.getY(), 2));
                        if (secondDistance >= 0
                                && secondDistance < secondCircle.getRadius() - 5) {
                            touchOnSecondCircle = true;
                        }

                    }
                }
            }

            if (touchOnFirstCircle == true && touchOnSecondCircle == true) {
                // do
                Log.i(FluteConstant.APP_TAG, "do");
            } else if (touchOnFirstCircle == false
                    && touchOnSecondCircle == true) {
                // re
                Log.i(FluteConstant.APP_TAG, "re");
            } else if (touchOnFirstCircle == true
                    && touchOnSecondCircle == false) {
                // me
                Log.i(FluteConstant.APP_TAG, "me");
            } else {
                // fa
                Log.i(FluteConstant.APP_TAG, "fa");
            }
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

}
