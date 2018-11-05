package com.workshop.quest.musicplayer.view.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.service.MusicPlayerService;
import com.workshop.quest.musicplayer.service.musicmanager.IMusicPlayer;
import com.workshop.quest.musicplayer.service.musicmanager.MusicPlayerCallback;

import java.util.List;

public class MusicPlayerModel implements IMusicPlayerModel {

    private Context mContext;

    private IMusicPlayerPresenter mMusicPlayerPresenter;

    private IMusicPlayer mMusicPlayer;

    private MusicServiceConnection mMusicServiceConnection;

    private boolean isBound = false;

    private List<Song> mSongList;

    public MusicPlayerModel(Context context, IMusicPlayerPresenter musicPlayerPresenter) {
        Loggy.entryLog();
        mContext = context;
        mMusicPlayerPresenter = musicPlayerPresenter;
        onStart();
        Loggy.exitLog();
    }

    @Override
    public void initMusicPlayer(final Song song, final List<Song> songList) {
        Loggy.entryLog();
        mMusicPlayer.initMusicPlayer(song, songList);
        Loggy.exitLog();
    }

    @Override
    public Song getNowPlayingSong() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMusicPlayer.getNowPlayingSong();
    }

    @Override
    public int getCurrentPositionByPercentValue() {
        return mMusicPlayer.getCurrentPositionByPercentValue();
    }

    @Override
    public int getCurrentPosition() {
        return mMusicPlayer.getCurrentPosition();
    }

    @Override
    public void onSeekProgress(final int progress) {
        Loggy.entryLog();
        mMusicPlayer.seekTo(progress);
        Loggy.exitLog();
    }

    @Override
    public void onStop() {
        Loggy.entryLog();
        if (isBound) {
            mMusicPlayer.unRegisterCallback(this);
            mContext.unbindService(mMusicServiceConnection);
        }
        Loggy.exitLog();
    }

    @Override
    public void onStart() {
        Loggy.entryLog();
        mMusicServiceConnection = new MusicServiceConnection();
        Intent serviceIntent = new Intent(mContext, MusicPlayerService.class);
        mContext.bindService(serviceIntent, mMusicServiceConnection, Context.BIND_AUTO_CREATE);
        Loggy.exitLog();
    }

    @Override
    public boolean isNowPlaying() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMusicPlayer.isNowPlaying();
    }

    @Override
    public void pauseSong() {
        Loggy.entryLog();
        mMusicPlayer.pauseSong();
        Loggy.exitLog();
    }

    @Override
    public void resumeSong() {
        Loggy.entryLog();
        mMusicPlayer.resumeSong();
        Loggy.exitLog();
    }

    @Override
    public void startNextSong() {
        Loggy.entryLog();
        mMusicPlayer.startNextSong();
        Loggy.exitLog();
    }

    @Override
    public boolean isShuffleEnabled() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMusicPlayer.isShuffleEnabled();
    }

    @Override
    public void setShuffleEnabled(boolean shuffleSong) {
        Loggy.entryLog();
        mMusicPlayer.setShuffleEnabled(shuffleSong);
        Loggy.exitLog();
    }

    @Override
    public List<Song> getSongList() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mSongList;
    }

    @Override
    public void startPreviousSong() {
        Loggy.entryLog();
        mMusicPlayer.startPreviousSong();
        Loggy.exitLog();
    }

    @Override
    public void setPlaylist(List<Song> songList) {
        Loggy.entryLog();
        mSongList = songList;
        Loggy.exitLog();
    }

    @Override
    public void notifySongChanged(final Song song) {
        Loggy.entryLog();
        Loggy.log(Log.INFO, "song = [" + song + "]");
        mMusicPlayerPresenter.notifySongChanged(song);
        Loggy.exitLog();
    }

    private class MusicServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            Loggy.entryLog();
            MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) service;
            mMusicPlayer = musicBinder.getMusicPlayer();
            mMusicPlayer.registerCallback(MusicPlayerModel.this);
            isBound = true;
            mMusicPlayerPresenter.onBindComplete();
            Loggy.exitLog();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            Loggy.entryLog();
            mMusicPlayer = null;
            isBound = false;
            Loggy.exitLog();
        }
    }
}
