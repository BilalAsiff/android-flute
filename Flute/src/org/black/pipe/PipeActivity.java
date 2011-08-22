package org.black.pipe;

import java.io.FileOutputStream;

import org.black.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main Activity.
 * 
 * @author black
 * 
 */
public class PipeActivity extends Activity {

    private PipeAudioInput audioInput = null;

    private final static int MENU_SET_BLOW = Menu.FIRST;
    private final static int MENU_SET_HOLE = MENU_SET_BLOW + 1;
    private final static int MENU_SET_INSTRUMENT = MENU_SET_HOLE + 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(PipeConstant.APP_TAG, "Create PipeActivity.");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

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

        SharedPreferences sharedPreferences = getSharedPreferences(
                PipeConstant.SHARED_PERFERENCE, Context.MODE_PRIVATE);
        // Retrieve instrument number
        int instrumentNumber = sharedPreferences.getInt(
                PipeConstant.INSTRUMENT_NUMBER,
                PipeConstant.DEFAULT_MIDI_PIPE_INSTRUMENT_NUMBERT);
        Log.i(PipeConstant.APP_TAG, "Instrument: " + instrumentNumber);
        // Create Midi files
        for (int i = 0; i < PipeConstant.NOTE_VALUES.length; i++) {
            String fileName = PipeConstant.NOTE_VALUES[i] + ".mid";
            try {
                MidiMaker midiMaker = new MidiMaker();
                midiMaker.programChange(instrumentNumber);
                midiMaker.noteOn(0, PipeConstant.NOTE_VALUES[i], 127);

                midiMaker.noteOff(100, PipeConstant.NOTE_VALUES[i]);
                FileOutputStream fileOutputStream = openFileOutput(fileName,
                        Context.MODE_WORLD_WRITEABLE);

                midiMaker.write(fileOutputStream);
                fileOutputStream.close();

                PipeGlobalValue.noteFilePathPairs.put(i, fileName);
                Log.i(PipeConstant.APP_TAG, "Create :" + fileName);
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Create MidiFile Fail.", e);
            }
        }

        PipeGlobalValue.resetMediaPlayers();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        //Three menu selections.
        menu.add(0, MENU_SET_BLOW, 0, R.string.BLOW_PRESSURE);
        menu.add(0, MENU_SET_HOLE, 0, R.string.CHANGE_HOLE_NUMBER);
        menu.add(0, MENU_SET_INSTRUMENT, 0, R.string.CHANGE_INSTRUMENT);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Intent intent = new Intent();
        Bundle extras = new Bundle();

        switch (item.getItemId()) {
        case MENU_SET_BLOW:
            intent.setClass(PipeActivity.this, ChangeBlowPressureActivity.class);

            intent.putExtras(extras);
            startActivity(intent);
            Log.i(PipeConstant.APP_TAG, "Switch ChangeBlowPressure.");
            break;
        case MENU_SET_HOLE:
            intent.setClass(PipeActivity.this, ChangeHoleActivity.class);

            intent.putExtras(extras);
            startActivity(intent);
            Log.i(PipeConstant.APP_TAG, "Switch ChangeHole.");
            break;
        case MENU_SET_INSTRUMENT:
            intent.setClass(PipeActivity.this, ChangeInstrumentActivity.class);

            intent.putExtras(extras);
            startActivity(intent);
            Log.i(PipeConstant.APP_TAG, "Switch ChangeInstrument.");
            break;
        }
        return true;
    }

}