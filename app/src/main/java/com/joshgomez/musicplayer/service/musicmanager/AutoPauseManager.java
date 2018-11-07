package com.joshgomez.musicplayer.service.musicmanager;

import android.util.Log;

import com.joshgomez.musicplayer.generic.log.Loggy;

/**
 * The type Auto pause manager.
 */
public class AutoPauseManager {

    private static AutoPauseManager sAutoPauseManager;
    private IMusicPlayer mMusicPlayer;
    private boolean songPausedByMe = false;

    public static AutoPauseManager getInstance() {
        Loggy.entryLog();
        if (sAutoPauseManager == null) {
            sAutoPauseManager = new AutoPauseManager();
        }
        Loggy.exitLog();
        return sAutoPauseManager;
    }

    public void registerCallback(IMusicPlayer musicPlayer) {
        mMusicPlayer = musicPlayer;
    }

    public void pauseSong() {
        Loggy.entryLog();
        if (mMusicPlayer != null && mMusicPlayer.isNowPlaying()) {
            songPausedByMe = true;
            mMusicPlayer.pauseSong();
        } else {
            Loggy.log(Log.WARN, "mMusicPlayer is null or mMusicPlayer.isNowPlaying() is "
                    + "true");
        }
        Loggy.exitLog();
    }

    public void resumeSong() {
        Loggy.entryLog();
        if (mMusicPlayer != null && songPausedByMe) {
            mMusicPlayer.resumeSong();
            songPausedByMe = false;
        } else {
            Loggy.log(Log.WARN, "mMusicPlayer is null or songPausedByMe is false");
        }
        Loggy.exitLog();
    }
}
