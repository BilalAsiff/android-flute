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

    RecordAudio audio = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(FluteConstant.APP_TAG, "Create Flute Activity.");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        super.onCreate(savedInstanceState);

        FluteSurfaceView fluteView = new FluteSurfaceView(this);
        FluteOnTouchListener fluteOnTouchListener = new FluteOnTouchListener();
        fluteView.setOnTouchListener(fluteOnTouchListener);
        audio = new RecordAudio();
        audio.execute();

        this.setContentView(fluteView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FluteGlobalValue.FLUTE_ON_WORKING = false;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FluteGlobalValue.FLUTE_ON_WORKING = true;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {

        double max = -100;
        private final int sampleSize = 8196;
        
        @Override
        protected Void doInBackground(Void... params) {
            try {
                int sampleRateInHz = 44100;
                int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int audioSource = MediaRecorder.AudioSource.MIC;
                int bufferSizeInBytes = AudioRecord.getMinBufferSize(
                        sampleRateInHz, channelConfig, audioFormat);
                AudioRecord audioRecord = new AudioRecord(audioSource,
                        sampleRateInHz, channelConfig, audioFormat,
                        bufferSizeInBytes);
                int audioRecordStatus = audioRecord.getState();
                audioRecord.startRecording();
                Log.d(FluteConstant.APP_TAG, "AudioRecord status: "
                        + audioRecordStatus);
                short[] buffer = new short[this.sampleSize];
                double[] toTransform = new double[this.sampleSize];
                while (FluteGlobalValue.FLUTE_ON_WORKING == true) {
                    int bufferReadResult = audioRecord.read(buffer, 0, this.sampleSize);
                    for (int i = 0; i < this.sampleSize && i < bufferReadResult; i++) {
                        // toTransform[i] = Math.abs((double) buffer[i]) /
                        // 32767; // signed
                        double f = 20 * Math.log10(toTransform[i]);
                        if (buffer[i] > 0) {
                            // Log.i("max", buffer[i] + "");
                            // Log.i("max", 20 * Math.log(0.9d) + "");
                        }
                        // Log.i("hello", 20 * Math.log10(0.1 / 32767) + "");
                        if (f > max) {
                            max = f;
                            // Log.i("max", f + "");
                        }

                        // 16
                        // bit
                    }

                    this.publishProgress(toTransform);
                }

            } catch (Throwable t) {
                Log.e(FluteConstant.APP_TAG, "Recording Failed", t);
            }

            return null;
        }

        protected void onProgressUpdate(double[]... toTransform) {
            Log.i(FluteConstant.APP_TAG, "blow");
        }
    }

}