package org.black.flute;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class FluteAudioInput implements Runnable {
    double max = -100;
    private final int sampleSize = 8196;
    private AudioRecord audioRecord = null;

    public void release() {
        if (this.audioRecord != null) {
            try {
                this.audioRecord.release();
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG, "Fail to release AudioRelease!", e);
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
                     * Know the PCM-Decibel transform formula from the following
                     * site: http://stackoverflow.com/questions/2917762
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
                Log.e(FluteConstant.APP_TAG, "Release AudioRecored Fail!", e);
            }
        }
    }

}
