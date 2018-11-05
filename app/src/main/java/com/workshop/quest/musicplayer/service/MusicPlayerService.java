package com.workshop.quest.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.service.musicmanager.MusicPlayer;

/**
 * Service playing music
 */
public class MusicPlayerService extends Service {

    /**
     * Subclass of {@link IBinder} for service binding
     */
    private MusicBinder mMusicBinder = new MusicBinder();
    /**
     * Handles all logic related to playing music
     */
    private MusicPlayer mMusicPlayer;

    /**
     * Initialises {@link MusicPlayer} when {@link MusicPlayerService} is created
     */
    @Override
    public void onCreate() {
        Loggy.entryLog();
        super.onCreate();
        mMusicPlayer = new MusicPlayer(this);
        Loggy.exitLog();
    }

    /**
     * Invoked to bind to service
     * @param intent : Instance of {@link Intent}
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMusicBinder;
    }

    /**
     * Subclass of {@link Binder} for binding
     */
    public class MusicBinder extends Binder {

        public MusicPlayer getMusicPlayer() {
            Loggy.entryLog();
            Loggy.exitLog();
            return mMusicPlayer;
        }

    }

    /**
     * Invoked when service start events are triggered
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null && intent.getAction() != null) {
            mMusicPlayer.onStatusBarEvent(intent);
        } else {
            Loggy.log(Log.WARN, "intent is null");
        }
        return START_STICKY;
    }
}
