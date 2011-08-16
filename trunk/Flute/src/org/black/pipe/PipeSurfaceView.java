package org.black.pipe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PipeSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Integer screenWidth;
    private Integer screenHeight;

    private Float pointRadius;

    private Circle firstCircle, secondCircle, bottomSemiCircle;

    public PipeSurfaceView(Context context) {
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

        Log.i(PipeConstant.APP_TAG, "Screen width: " + this.screenWidth);
        Log.i(PipeConstant.APP_TAG, "Screen height: " + this.screenHeight);

        this.firstCircle = new Circle(screenWidth * 0.75f,
                screenHeight * 0.25f, pointRadius);
        this.secondCircle = new Circle(screenWidth * 0.25f,
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
    public int draw(double audioVelocity) {
        Canvas canvas = null;
        int result = 0;
        try {
            canvas = holder.lockCanvas(null);

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            // Clear canvas
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, this.screenWidth, this.screenHeight, paint);

            boolean touchOnFirstCircle = false;
            boolean touchOnSecondCircle = false;

            if (audioVelocity > PipeConstant.MIN_AUDIO_PRESSURE) {
                paint.setColor(Color.argb(255, 102, 171, 255));
            } else {
                paint.setColor(Color.argb(255, 0, 113, 255));
            }
            Path innerCirclePath = new Path();
            Path outerCirclePath = new Path();
            innerCirclePath.moveTo(
                    bottomSemiCircle.getX() - bottomSemiCircle.getRadius(),
                    bottomSemiCircle.getY());
            innerCirclePath.quadTo(bottomSemiCircle.getX(),
                    bottomSemiCircle.getY() - bottomSemiCircle.getRadius() * 1.5f,
                    bottomSemiCircle.getX() + bottomSemiCircle.getRadius(),
                    bottomSemiCircle.getY());

            outerCirclePath.moveTo(
                    bottomSemiCircle.getX() - (bottomSemiCircle.getRadius() * 2),
                    bottomSemiCircle.getY());
            outerCirclePath.quadTo(bottomSemiCircle.getX(),
                    bottomSemiCircle.getY() - (bottomSemiCircle.getRadius() * 3),
                    bottomSemiCircle.getX() + (bottomSemiCircle.getRadius() * 2),
                    bottomSemiCircle.getY());
            
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawPath(innerCirclePath, paint);
            canvas.drawPath(outerCirclePath, paint);

            paint.setStyle(Style.FILL);
            MotionEvent motionEvent = PipeGlobalValue.getMotionEvent();
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
                                && firstDistance < firstCircle.getRadius()) {
                            touchOnFirstCircle = true;
                        }

                        double secondDistance = Math.sqrt(Math.pow(pointX
                                - secondCircle.getX(), 2)
                                + Math.pow(pointY - secondCircle.getY(), 2));
                        if (secondDistance >= 0
                                && secondDistance < secondCircle.getRadius()) {
                            touchOnSecondCircle = true;
                        }

                    }
                }
            }

            // Draw circle and border
            if (touchOnFirstCircle == true) {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(firstCircle.getX(), firstCircle.getY(),
                        firstCircle.getRadius() + 5, paint);

                paint.setColor(Color.argb(255, 102, 171, 255));
            } else {
                paint.setColor(Color.argb(255, 0, 113, 255));
            }
            canvas.drawCircle(firstCircle.getX(), firstCircle.getY(),
                    firstCircle.getRadius(), paint);

            if (touchOnSecondCircle == true) {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(secondCircle.getX(), secondCircle.getY(),
                        secondCircle.getRadius() + 5, paint);

                paint.setColor(Color.argb(255, 102, 171, 255));
            } else {
                paint.setColor(Color.argb(255, 0, 113, 255));
            }
            canvas.drawCircle(secondCircle.getX(), secondCircle.getY(),
                    secondCircle.getRadius(), paint);

            // Compute note value
            if (touchOnFirstCircle == true && touchOnSecondCircle == true) {
                result = PipeConstant.NOTE_VALUES[0];
            } else if (touchOnFirstCircle == false
                    && touchOnSecondCircle == true) {
                result = PipeConstant.NOTE_VALUES[1];
            } else if (touchOnFirstCircle == true
                    && touchOnSecondCircle == false) {
                result = PipeConstant.NOTE_VALUES[2];
            } else {
                result = PipeConstant.NOTE_VALUES[3];
            }
        } catch (Exception e) {
            Log.e(PipeConstant.APP_TAG, "Detect touch error.", e);
            result = 0;
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
        return result;
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
