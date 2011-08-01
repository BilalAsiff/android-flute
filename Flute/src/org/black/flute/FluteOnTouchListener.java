package org.black.flute;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FluteOnTouchListener implements OnTouchListener {
    private MotionEvent motionEvent;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // MotionEvent.ACTION_UP when gesture finish
        Log.i(FluteConstant.APP_TAG, "" + event.getPointerCount());
        this.motionEvent = event;
        return true;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }
}
