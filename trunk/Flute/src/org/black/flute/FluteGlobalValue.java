package org.black.flute;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.media.MediaPlayer;
import android.view.MotionEvent;

public class FluteGlobalValue {
    public static boolean FLUTE_ON_WORKING = true;
    public static boolean FLUTE_ON_PAUSE = false;

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
            FluteGlobalValue.motionEvent = null;
            FluteGlobalValue.motionEvent = motionEvent;
        } finally {
            motionEventLock.unlock();
        }
    }

    public static void addMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayers.add(mediaPlayer);
    }

    public static MediaPlayer remove() {
        if (mediaPlayers != null && mediaPlayers.size() > 0) {
            return mediaPlayers.remove(0);
        }
        return null;
    }

}
