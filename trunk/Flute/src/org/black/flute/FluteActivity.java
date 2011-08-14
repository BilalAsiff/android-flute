package org.black.flute;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class FluteActivity extends Activity {

    private FluteAudioInput audioInput = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(FluteConstant.APP_TAG, "Create Flute Activity.");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        for (int i = 0; i < FluteConstant.NOTE_VALUES.length; i++) {
            String fileName = FluteConstant.NOTE_VALUES[i] + ".mid";
            try {
                MidiFile midiFile = new MidiFile();
                midiFile.progChange(73);
                midiFile.noteOnOffNow(500, FluteConstant.NOTE_VALUES[i], 127);
                
                FileOutputStream fileOutputStream = openFileOutput(fileName,
                        Context.MODE_PRIVATE);
                midiFile.writeToFile(fileOutputStream);
                fileOutputStream.close();
                
                FluteGlobalValue.noteFilePathPairs.put(i, fileName);
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG, "Create MidiFile Fail.", e);
            }
        }

        super.onCreate(savedInstanceState);

        FluteSurfaceView fluteView = new FluteSurfaceView(this);
        FluteOnTouchListener fluteOnTouchListener = new FluteOnTouchListener();
        fluteView.setOnTouchListener(fluteOnTouchListener);
        this.setContentView(fluteView);

        this.audioInput = new FluteAudioInput(fluteView);
        audioInput.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FluteGlobalValue.FLUTE_ON_PAUSE = false;
        Log.i(FluteConstant.APP_TAG,
                "Assign FluteGlobalValue.FLUTE_ON_PAUSE to true;");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FluteGlobalValue.FLUTE_ON_WORKING = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FluteGlobalValue.FLUTE_ON_PAUSE = true;
        Log.d(FluteConstant.APP_TAG,
                "Assign FluteGlobalValue.FLUTE_ON_PAUSE to false");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FluteGlobalValue.FLUTE_ON_WORKING = false;
        if (this.audioInput != null) {
            try {
                audioInput.cancel(true);
                audioInput.release();
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG, "Release AudioInput Fail", e);
            }
        }
    }
}