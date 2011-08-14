package org.black.flute;

import java.io.FileInputStream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Class to accept record input and notify canvas to draw or not.
 * 
 * @author black
 * 
 */
public class FluteAudioInput extends AsyncTask<Void, Double, Void> {
    private final int sampleSize = 8196;
    private final double DECIBEL_ADJUST = 96;
    private final double MAX_ABSOLUTE_PCM_VALUE = 32768;

    private AudioRecord audioRecord = null;

    private FluteSurfaceView fluteSurfaceView;

    public FluteAudioInput(FluteSurfaceView fluteSurfaceView) {
        this.fluteSurfaceView = fluteSurfaceView;
    }

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
    protected Void doInBackground(Void... params) {
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
            double[] transform = new double[this.sampleSize];
            while (true) {
                if (FluteGlobalValue.FLUTE_ON_WORKING == false) {
                    break;
                }
                if (FluteGlobalValue.FLUTE_ON_PAUSE == false) {
                    int bufferReadResult = audioRecord.read(audioData, 0,
                            this.sampleSize);
                    for (int i = 0; i < this.sampleSize && i < bufferReadResult; i++) {
                        /*
                         * Learn the PCM-Decibel transform formula from the
                         * following site:
                         * http://stackoverflow.com/questions/2917762
                         * /android-pcm-bytes But I know my implementation is
                         * wrong.
                         */

                        transform[i] = 20d * Math
                                .log10((Math.abs(audioData[i]) / MAX_ABSOLUTE_PCM_VALUE));
                    }
                    double averageDecibel = 0.0;
                    if (transform != null) {
                        for (int i = 0; i < transform.length; i++) {
                            averageDecibel += transform[i];
                        }
                        averageDecibel = this.DECIBEL_ADJUST
                                + (averageDecibel / transform.length);
                        this.publishProgress(averageDecibel);
                    }
                }
            }

            Log.i(FluteConstant.APP_TAG, "Leave recording status");

        } catch (Throwable t) {
            Log.e(FluteConstant.APP_TAG, "Recording Failed", t);
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected synchronized void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        double inputDecible = values[0];
        Log.d(FluteConstant.APP_TAG, "" + inputDecible);

        int noteValue = this.fluteSurfaceView.draw(inputDecible);
        if (inputDecible > 90 && noteValue != 0) {
            try {
                if (noteValue != FluteGlobalValue.CURRENT_NOTE) {
                    closeOldNote();

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    FileInputStream fis = this.fluteSurfaceView.getContext()
                            .openFileInput(noteValue + ".mid");
                    FluteGlobalValue.CURRENT_NOTE = noteValue;
                    FluteGlobalValue.addMediaPlayer(mediaPlayer);
                    mediaPlayer.setDataSource(fis.getFD());
                    fis.close();
                    
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setVolume(0.5f, 0.5f);

                    Thread.sleep(50l);

                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG, "Unable to play Midi file.", e);
            }
        } else {
            closeOldNote();
            FluteGlobalValue.CURRENT_NOTE = 0;
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
    
    private void closeOldNote() {
        MediaPlayer oldMediaPlayer = FluteGlobalValue.remove();

        if (oldMediaPlayer != null) {
            try {
                oldMediaPlayer.setVolume(0.5f, 0.5f);
                oldMediaPlayer.pause();
                oldMediaPlayer.stop();
                oldMediaPlayer.release();
                oldMediaPlayer = null;
            } catch (Exception e) {
                Log.e(FluteConstant.APP_TAG,
                        "Unable to realease MediaPlayer.", e);
            }
        }
    }

}
