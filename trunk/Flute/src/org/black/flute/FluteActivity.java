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

    private AudioInput audioInput = null;
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
            this.audioInput = new AudioInput();

            this.audioInputThread = new Thread(this.audioInput);
            this.audioInputThread.start();
        } else {
            State threadState = this.audioInputThread.getState();
            if (threadState.equals(State.TERMINATED) == true) {
                try {
                    this.audioInput = null;
                    this.audioInput = new AudioInput();

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

    private class AudioInput implements Runnable {
        double max = -100;
        private final int sampleSize = 8196;
        private AudioRecord audioRecord = null;

        public void release() {
            if (this.audioRecord != null) {
                try {
                    this.audioRecord.release();
                } catch (Exception e) {
                    Log.e(FluteConstant.APP_TAG,
                            "Fail to release AudioRelease!", e);
                }
            }
        }

        @Override
        public void run() {
            int sampleRateInHz = 44100;
            int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            int audioSource = MediaRecorder.AudioSource.MIC;

            try {
                int bufferSizeInBytes = AudioRecord.getMinBufferSize(
                        sampleRateInHz, channelConfig, audioFormat);
                this.audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                        channelConfig, audioFormat, bufferSizeInBytes);
                int audioRecordStatus = audioRecord.getState();
                Log.d(FluteConstant.APP_TAG, "AudioRecord status: "
                        + audioRecordStatus);
                audioRecord.startRecording();
                short[] audioData = new short[this.sampleSize];
                double[] toTransform = new double[this.sampleSize];
                while (FluteGlobalValue.FLUTE_ON_WORKING == true) {
                    int bufferReadResult = audioRecord.read(audioData, 0,
                            this.sampleSize);
                    for (int i = 0; i < this.sampleSize && i < bufferReadResult; i++) {
                        /*
                         * Know the PCM-Decibel transform formula from the
                         * following site:
                         * http://stackoverflow.com/questions/2917762
                         * /android-pcm-bytes But I am not sure this formula is
                         * right or wrong.
                         */

                        double f = 20 * Math.log10(toTransform[i]);
                        if (audioData[i] > 0 && i == 4000) {
                            Log.i(FluteConstant.APP_TAG, audioData[i] + "");
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

                    FluteGlobalValue.getMotionEvent();
                }

            } catch (Throwable t) {
                Log.e(FluteConstant.APP_TAG, "Recording Failed", t);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            if (this.audioRecord != null) {
                try {
                    this.audioRecord.release();
                } catch (Exception e) {
                    Log.e(FluteConstant.APP_TAG, "Release AudioRecored Fail!",
                            e);
                }
            }
        }

    }

}