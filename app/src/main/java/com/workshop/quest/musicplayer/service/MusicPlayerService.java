package com.workshop.quest.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.service.musicmanager.MusicPlayer;

public class MusicPlayerService extends Service {

    private MusicBinder mMusicBinder = new MusicBinder();

    private MusicPlayer mMusicPlayer;

    @Override
    public void onCreate() {
        Loggy.entryLog();
        super.onCreate();
        mMusicPlayer = new MusicPlayer(this);
        Loggy.exitLog();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMusicBinder;
    }

    public class MusicBinder extends Binder {

        public MusicPlayer getMusicPlayer() {
            Loggy.entryLog();
            Loggy.exitLog();
            return mMusicPlayer;
        }

    }

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
