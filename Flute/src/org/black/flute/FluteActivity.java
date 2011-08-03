package org.black.flute;

import java.lang.Thread.State;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

public class FluteActivity extends Activity {

    private FluteAudioInput audioInput = null;
    private Thread audioInputThread = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(FluteConstant.APP_TAG, "Create Flute Activity.");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        super.onCreate(savedInstanceState);

        FluteSurfaceView fluteView = new FluteSurfaceView(this);
        FluteOnTouchListener fluteOnTouchListener = new FluteOnTouchListener();
        fluteView.setOnTouchListener(fluteOnTouchListener);
        this.setContentView(fluteView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FluteGlobalValue.FLUTE_ON_WORKING = true;
        Log.i(FluteConstant.APP_TAG,
                "Assign FluteGlobalValue.FLUTE_ON_WORKING to true;");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.audioInputThread == null) {
            this.audioInput = new FluteAudioInput();

            this.audioInputThread = new Thread(this.audioInput);
            this.audioInputThread.start();
        } else {
            State threadState = this.audioInputThread.getState();
            if (threadState.equals(State.TERMINATED) == true) {
                try {
                    this.audioInput = null;
                    this.audioInput = new FluteAudioInput();

                    this.audioInputThread = null;
                    this.audioInputThread = new Thread(this.audioInput);
                    this.audioInputThread.start();
                    System.gc();
                } catch (Exception e) {
                    Log.e(FluteConstant.APP_TAG, "Fail to re-launch thread", e);
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        FluteGlobalValue.FLUTE_ON_WORKING = false;
        Log.d(FluteConstant.APP_TAG,
                "Assign FluteGlobalValue.FLUTE_ON_WORKING to false");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.audioInput != null) {
            try {
                audioInput.release();
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG, "Release AudioInput Fail", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}