package org.black.flute;

import android.view.MotionEvent;

public class FluteGlobalValue {
    public static boolean FLUTE_ON_WORKING = false;
    
    private static MotionEvent motionEvent;

    public synchronized static MotionEvent getMotionEvent() {
        return motionEvent;
    }

    public synchronized static void setMotionEvent(MotionEvent motionEvent) {
        FluteGlobalValue.motionEvent = null;
        FluteGlobalValue.motionEvent = motionEvent;
    }
    
    
}
