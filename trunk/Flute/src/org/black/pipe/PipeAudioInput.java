package org.black.pipe;

import java.io.FileInputStream;

import android.content.Context;
import android.content.SharedPreferences;
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
public class PipeAudioInput extends AsyncTask<Void, Float, Void> {
	private MediaRecorder mediaRecorder = null;

	private PipeSurfaceView pipeSurfaceView;

	private long lastWorkTime = 0l;

	public PipeAudioInput(PipeSurfaceView pipeSurfaceView) {
		this.pipeSurfaceView = pipeSurfaceView;
	}

	/**
	 * To release internal using object.
	 */
	public void release() {
		if (this.mediaRecorder != null) {
			try {
				this.mediaRecorder.release();
				Log.d(PipeConstant.APP_TAG, "Release AudioRecored object.");
			} catch (Exception e) {
				Log.e(PipeConstant.APP_TAG, "Fail to release AudioRelease!", e);
			}
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			this.mediaRecorder = new MediaRecorder();
			this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			this.mediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			this.mediaRecorder
					.setAudioEncoder(MediaRecorder.OutputFormat.THREE_GPP);
			this.mediaRecorder.setOutputFile("/dev/null");
			this.mediaRecorder.prepare();
			this.mediaRecorder.start();
			while (true) {
				if (PipeGlobalValue.PIPE_ON_WORKING == false) {
					break;
				}
				if (PipeGlobalValue.PIPE_ON_PAUSE == false) {
					if (this.mediaRecorder != null) {
						/*
						 * Learn the PCM-Decibel transform formula from the
						 * following site:
						 * http://stackoverflow.com/questions/2917762
						 * /android-pcm-bytes But I know my implementation is
						 * wrong.
						 */
						float decibel = (float) (20.0D * Math
								.log10(this.mediaRecorder.getMaxAmplitude()));
						this.publishProgress(decibel);
					}
				}
				System.gc();
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
	protected synchronized void onProgressUpdate(Float... values) {
		super.onProgressUpdate(values);
		float inputDecible = values[0];
		Log.d(PipeConstant.APP_TAG, "inputeDecible: " + inputDecible);
		int noteValue = this.pipeSurfaceView.draw(inputDecible);

		if (System.currentTimeMillis() - this.lastWorkTime < 500l) {
			if (inputDecible > PipeConstant.DEFAULT_MIN_BLOW_PRESSURE && noteValue != 0) {
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
						Log.d(PipeConstant.APP_TAG,
								"Start mediaPlayer, fileName :" + fileName);
						mediaPlayer.setVolume(0.5f, 0.5f);
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
		this.lastWorkTime = System.currentTimeMillis();

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (this.mediaRecorder != null) {
			try {
				this.mediaRecorder.stop();
				this.mediaRecorder.release();
				this.mediaRecorder = null;
				Log.d(PipeConstant.APP_TAG, "Release MediaRecorder Object.");
			} catch (Exception e) {
				Log.e(PipeConstant.APP_TAG, "Release MediaRecorder Fail!", e);
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
