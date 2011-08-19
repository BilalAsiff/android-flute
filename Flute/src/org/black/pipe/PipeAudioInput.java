package org.black.pipe;

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
public class PipeAudioInput extends AsyncTask<Void, Double, Void> {
    private final int sampleSize = 8196;

    private AudioRecord audioRecord = null;

    private PipeSurfaceView pipeSurfaceView;

    public PipeAudioInput(PipeSurfaceView pipeSurfaceView) {
        this.pipeSurfaceView = pipeSurfaceView;
    }

    public void release() {
        if (this.audioRecord != null) {
            try {
                this.audioRecord.release();
                Log.d(PipeConstant.APP_TAG, "Release AudioRecored object.");
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Fail to release AudioRelease!", e);
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        int sampleRateInHz = 16000;
        int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int audioSource = MediaRecorder.AudioSource.MIC;

        try {
            int bufferSizeInBytes = AudioRecord.getMinBufferSize(
                    sampleRateInHz, channelConfig, audioFormat);
            this.audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);
            int audioRecordStatus = audioRecord.getState();
            Log.d(PipeConstant.APP_TAG, "AudioRecord status: "
                    + audioRecordStatus);
            audioRecord.startRecording();
            short[] audioData = new short[this.sampleSize];
            double[] transform = new double[this.sampleSize];
            while (true) {
                if (PipeGlobalValue.PIPE_ON_WORKING == false) {
                    break;
                }
                if (PipeGlobalValue.PIPE_ON_PAUSE == false) {
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
                                .log10((Math.abs(audioData[i])));
                    }
                    double averageDecibel = 0.0d;
                    if (transform != null) {
                        for (int i = 0; i < this.sampleSize; i++) {
                            averageDecibel += transform[i];
                        }
                        averageDecibel = averageDecibel / transform.length;
                        this.publishProgress(averageDecibel);
                    }
                }
            }

            Log.i(PipeConstant.APP_TAG, "Leave recording status");

        } catch (Throwable t) {
            Log.e(PipeConstant.APP_TAG, "Recording Failed", t);
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
        Log.d(PipeConstant.APP_TAG, "inputeDEcible: " + inputDecible);
        int noteValue = this.pipeSurfaceView.draw(inputDecible);
        if (inputDecible > PipeConstant.MIN_AUDIO_PRESSURE && noteValue != 0) {
            try {
                if (noteValue != PipeGlobalValue.CURRENT_NOTE) {
                    closeOldNote();
                    String fileName = noteValue + ".mid";
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    FileInputStream fis = this.pipeSurfaceView.getContext()
                            .openFileInput(fileName);
                    PipeGlobalValue.CURRENT_NOTE = noteValue;
                    PipeGlobalValue.addMediaPlayer(mediaPlayer);
                    mediaPlayer.setDataSource(fis.getFD());
                    fis.close();

                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Log.d(PipeConstant.APP_TAG, "Start mediaPlayer, fileName :"
                            + fileName);
                    mediaPlayer.setVolume(0.5f, 0.5f);

                    Thread.sleep(50l);

                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Unable to play Midi file.", e);
            }
        } else {
            closeOldNote();
            PipeGlobalValue.CURRENT_NOTE = 0;
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.audioRecord != null) {
            try {
                this.audioRecord.release();
                Log.d(PipeConstant.APP_TAG, "Release AudioRecord Object.");
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Release AudioRecored Fail!", e);
            }
        }
    }

    private void closeOldNote() {
        MediaPlayer oldMediaPlayer = PipeGlobalValue.removeMediaPlayer();
        Log.d(PipeConstant.APP_TAG, "Retrieve the last MediaPlayer.");
        if (oldMediaPlayer != null) {
            try {
                oldMediaPlayer.setVolume(0.5f, 0.5f);
                oldMediaPlayer.setVolume(0.2f, 0.2f);
                oldMediaPlayer.pause();
                oldMediaPlayer.stop();
                oldMediaPlayer.release();
                oldMediaPlayer = null;
                Log.d(PipeConstant.APP_TAG, "Release media player.");
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Unable to realease MediaPlayer.",
                        e);
            }
        }
    }
}
