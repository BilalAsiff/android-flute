package org.black.flute;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FluteOnTouchListener implements OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        FluteGlobalValue.setMotionEvent(event);
        Log.i(FluteConstant.APP_TAG, "Save MotionEvent to FluteGlobalValue.");
        return true;
    }
}
