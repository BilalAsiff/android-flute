package org.black.flute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FluteView extends SurfaceView implements SurfaceHolder.Callback {

	SurfaceHolder holder;

	public FluteView(Context context) {
		super(context);
		holder = this.getHolder();
		holder.addCallback(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(new MyThread()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			Canvas canvas = holder.lockCanvas(null);
			Paint mPaint = new Paint();
			mPaint.setColor(Color.BLUE);

			canvas.drawRect(new RectF(40, 60, 80, 80), mPaint);
			holder.unlockCanvasAndPost(canvas);
		}

	}

}
