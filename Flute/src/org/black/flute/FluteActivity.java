package org.black.flute;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FluteActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FluteView fluteView = new FluteView(this);
        FluteOnTouchListener fluteOnTouchListener = new FluteOnTouchListener();
        fluteView.setOnTouchListener(fluteOnTouchListener);
        setContentView(fluteView);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MidiFile midiFile = new MidiFile();
    }
}