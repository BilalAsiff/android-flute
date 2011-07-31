package org.black.flute;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class FluteActivity extends Activity {
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
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        double max = -100;

        @Override
        protected Void doInBackground(Void... params) {
            try {

                int freq = 44100;
                int chan = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
                int enc = AudioFormat.ENCODING_PCM_16BIT;
                int src = MediaRecorder.AudioSource.MIC;
                int buflen = AudioRecord.getMinBufferSize(freq, chan, enc);
                AudioRecord ar = new AudioRecord(src, freq, chan, enc, buflen);
                int test = ar.getState();
                ar.startRecording();
                Log.d("flute", "" + test);
                short[] buffer = new short[8196];
                double[] toTransform = new double[8196];
                while (true) {
                    int bufferReadResult = ar.read(buffer, 0, 8196);
                    for (int i = 0; i < 8196 && i < bufferReadResult; i++) {
                        // toTransform[i] = Math.abs((double) buffer[i]) /
                        // 32767; // signed
                        double f = 20 * Math.log10(toTransform[i]);
                        if (buffer[i] > 0) {
                            // Log.i("max", buffer[i] + "");
                            Log.i("max", 20 * Math.log(0.9d) + "");
                        }
                        // Log.i("hello", 20 * Math.log10(0.1 / 32767) + "");
                        if (f > max) {
                            max = f;
                            // Log.i("max", f + "");
                        }

                        // 16
                        // bit
                    }

                    // publishProgress(toTransform);
                }

            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }

            return null;
        }

        protected void onProgressUpdate(double[]... toTransform) {
            
        }
    }

}