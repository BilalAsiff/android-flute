package org.black.pipe;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PipeOnTouchListener implements OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PipeGlobalValue.setMotionEvent(event);
        Log.i(PipeConstant.APP_TAG, "Save MotionEvent to FluteGlobalValue.");
        return true;
    }
}
