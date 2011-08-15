package org.black.pipe;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class PipeActivity extends Activity {

    private PipeAudioInput audioInput = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(PipeConstant.APP_TAG, "Create PipeActivity.");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        for (int i = 0; i < PipeConstant.NOTE_VALUES.length; i++) {
            String fileName = PipeConstant.NOTE_VALUES[i] + ".mid";
            try {
                MidiFile midiFile = new MidiFile();
                midiFile.progChange(74);
                midiFile.noteOnOffNow(500, PipeConstant.NOTE_VALUES[i], 127);
                
                FileOutputStream fileOutputStream = openFileOutput(fileName,
                        Context.MODE_PRIVATE);
                midiFile.writeToFile(fileOutputStream);
                fileOutputStream.close();
                
                PipeGlobalValue.noteFilePathPairs.put(i, fileName);
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Create MidiFile Fail.", e);
            }
        }

        super.onCreate(savedInstanceState);

        PipeSurfaceView pipeView = new PipeSurfaceView(this);
        PipeOnTouchListener pipeOnTouchListener = new PipeOnTouchListener();
        pipeView.setOnTouchListener(pipeOnTouchListener);
        this.setContentView(pipeView);

        this.audioInput = new PipeAudioInput(pipeView);
        audioInput.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PipeGlobalValue.PIPE_ON_PAUSE = false;
        Log.i(PipeConstant.APP_TAG,
                "Assign PipeGlobalValue.PIPE_ON_PAUSE to true;");
    }

    @Override
    protected void onStart() {
        super.onStart();
        PipeGlobalValue.PIPE_ON_WORKING = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        PipeGlobalValue.PIPE_ON_PAUSE = true;
        Log.d(PipeConstant.APP_TAG,
                "Assign PipeGlobalValue.PIPE_ON_PAUSE to false");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PipeGlobalValue.PIPE_ON_WORKING = false;
        if (this.audioInput != null) {
            try {
                audioInput.cancel(true);
                audioInput.release();
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Release AudioInput Fail", e);
            }
        }
    }
}