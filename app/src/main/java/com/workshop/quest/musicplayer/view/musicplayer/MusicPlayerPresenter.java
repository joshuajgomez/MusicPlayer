package com.workshop.quest.musicplayer.view.musicplayer;

import android.content.Intent;
import android.os.Handler;

import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.model.Song;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.workshop.quest.musicplayer.view.musicplayer.MusicPlayerActivity_old.getTime;


public class MusicPlayerPresenter implements IMusicPlayerPresenter {

    private IMusicPlayerView mMusicPlayerView;

    private IMusicPlayerModel mMusicPlayerModel;

    private Handler mHandler;

    private Timer mTimer;

    public MusicPlayerPresenter(MusicPlayerActivity musicPlayerActivity) {
        Loggy.entryLog();
        mMusicPlayerView = (IMusicPlayerView) musicPlayerActivity;
        mMusicPlayerModel = new MusicPlayerModel(musicPlayerActivity, this);
        mHandler = new Handler();
        mTimer = new Timer();
        Loggy.exitLog();
    }

    @Override
    public void onClickEvent(final int viewId) {
        Loggy.entryLog();
        Loggy.exitLog();
    }

    @Override
    public void onSeekProgress(final int progress) {
        Loggy.entryLog();
        mMusicPlayerModel.onSeekProgress(progress);
        Loggy.exitLog();
    }

    @Override
    public void handleViewStart(Intent intent) {
        Loggy.entryLog();
        Song song;
        List<Song> songList;
        if (intent != null && intent.hasExtra("song")) {
            songList = intent.getParcelableArrayListExtra("song_list");
            song = intent.getParcelableExtra("song");
            intent.removeExtra("song");
            mMusicPlayerModel.initMusicPlayer(song, songList);
        } else {
            song = mMusicPlayerModel.getNowPlayingSong();
        }
        mMusicPlayerView.updateNowPlayingSong(song);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    int percentValue = mMusicPlayerModel.getCurrentPositionByPercentValue();
                    String time = getTime(mMusicPlayerModel.getCurrentPosition());
                    mMusicPlayerView.updateProgress(time, percentValue);
                });
            }
        }, 0, 500);
        Loggy.exitLog();
    }

    @Override
    public void onStart() {
        Loggy.entryLog();
        mMusicPlayerModel.onStart();
        Loggy.exitLog();
    }

    @Override
    public void onStop() {
        Loggy.entryLog();
        mMusicPlayerModel.onStop();
        Loggy.exitLog();
    }

    @Override
    public void notifySongChanged(final Song song) {
        Loggy.entryLog();
        mMusicPlayerView.updateNowPlayingSong(song);
        Loggy.exitLog();
    }
}
