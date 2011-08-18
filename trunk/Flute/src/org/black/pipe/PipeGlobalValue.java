package org.black.pipe;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A class to save global variables
 * @author black
 *
 */
public class PipeGlobalValue {
    public static boolean PIPE_ON_WORKING = true;
    public static boolean PIPE_ON_PAUSE = false;

    public static int CURRENT_NOTE = 0;

    public static List<MediaPlayer> mediaPlayers = new Vector<MediaPlayer>();
    public static Map<Integer, String> noteFilePathPairs = new Hashtable<Integer, String>();

    private static MotionEvent motionEvent = null;
    private static Lock motionEventLock = new ReentrantLock();

    public static MotionEvent getMotionEvent() {
        motionEventLock.lock();
        try {
            return motionEvent;
        } finally {
            motionEventLock.unlock();
        }
    }

    public static void setMotionEvent(MotionEvent motionEvent) {
        motionEventLock.lock();
        try {
            PipeGlobalValue.motionEvent = null;
            PipeGlobalValue.motionEvent = motionEvent;
        } finally {
            motionEventLock.unlock();
        }
    }

    public static void addMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayers.add(mediaPlayer);
    }

    public static MediaPlayer removeMediaPlayer() {
        if (mediaPlayers != null && mediaPlayers.size() > 0) {
            return mediaPlayers.remove(0);
        }
        return null;
    }
    
    public static void resetMediaPlayers() {
        if (mediaPlayers != null) {
            try {
                mediaPlayers.clear();
            } catch (Exception e) {
                Log.e(PipeConstant.APP_TAG, "Clear MediaPlayers fail!", e);
            }
            mediaPlayers = null;
        }
        mediaPlayers = new Vector<MediaPlayer>();
        
    }
}
