package com.workshop.quest.musicplayer.service.musicmanager;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.workshop.quest.musicplayer.generic.SharedUtil;
import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all the logic related to playing music and handles its callbacks
 */
public class MusicPlayer implements MediaPlayer.OnCompletionListener, IMusicPlayer {

    /**
     * Holds current playing song
     */
    private Song mNowPlayingSong;
    /**
     * {@link List<Song>} holding current playlist
     */
    private List<Song> mPlaylist;
    /**
     * Instance of application {@link Context}
     */
    private Context mContext;
    /**
     * Plays the music file
     */
    private MediaPlayer mMediaPlayer;
    /**
     * List of all instances of {@link MusicPlayerCallback}
     */
    private List<MusicPlayerCallback> mCallbackList = new ArrayList<>();
    /**
     * Instance of {@link MusicPlayerService}
     */
    private MusicPlayerService mMusicPlayerService;
    /**
     * Handles logic for showing music controls in status bar
     */
    private StatusBarPlayer mStatusBarPlayer;

    private boolean shuffleEnabled = false;

    /**
     * Initialises members of {@link MusicPlayer}
     *
     * @param musicPlayerService : Instance of {@link MusicPlayerService}
     */
    public MusicPlayer(MusicPlayerService musicPlayerService) {
        mContext = musicPlayerService;
        mMusicPlayerService = musicPlayerService;
        mStatusBarPlayer = new StatusBarPlayer(mContext, this);
        registerCallback(mStatusBarPlayer);
    }

    /**
     * Registers instances of {@link MusicPlayerCallback} for callback
     *
     * @param callback : Instance of {@link MusicPlayerCallback}
     * @return true if registration is successful
     */
    public boolean registerCallback(MusicPlayerCallback callback) {
        Loggy.entryLog();
        Loggy.exitLog();
        return mCallbackList.add(callback);
    }

    /**
     * Unregisters instances of {@link MusicPlayerCallback} for callback
     *
     * @param callback : Instance of {@link MusicPlayerCallback}
     * @return true if unregistering is successful
     */
    public boolean unRegisterCallback(MusicPlayerCallback callback) {
        Loggy.entryLog();
        Loggy.exitLog();
        return mCallbackList.remove(callback);
    }

    /**
     * Invoked when current playing song is finished
     *
     * @param activity : Instance of {@link Activity} to be notified
     */
    @Override
    public void setCompletionListener(final Activity activity) {
        Loggy.entryLog();
        if (activity instanceof MediaPlayer.OnCompletionListener) {
            mMediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) activity);
        } else {
            Loggy.log(Log.WARN, activity.getLocalClassName()
                    + " does not implement MediaPlayer.OnCompletionListener");
        }
        Loggy.exitLog();
    }

    @Override
    public boolean isShuffleEnabled() {
        Loggy.entryLog();
        Loggy.exitLog();
        return shuffleEnabled;
    }

    @Override
    public void setShuffleEnabled(boolean shuffleSong) {
        Loggy.entryLog();
        shuffleEnabled = shuffleSong;
        Loggy.exitLog();
    }

    /**
     * Initialises the mNowPlayingSong and mPlaylist with values and starts playing
     *
     * @param song     : Instance of {@link Song}
     * @param playlist : {@link List} holding playlist of songs
     */
    public void initMusicPlayer(Song song, List<Song> playlist) {
        Loggy.entryLog();
        Loggy.log(Log.INFO, "song = [" + song + "], playlist = [" + playlist + "]");
        mNowPlayingSong = song;
        mPlaylist = playlist;
        startMusic();
        Loggy.exitLog();
    }

    /**
     * Starts playing the mNowPlayingSong
     */
    private void startMusic() {
        Loggy.entryLog();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(mNowPlayingSong.getSongUrl()));
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(this);
        notifySongChanged();
        Loggy.exitLog();
    }

    /**
     * Pauses the current playing song
     */
    public void pauseSong() {
        mMediaPlayer.pause();
        notifySongChanged();
    }

    /**
     * Resumes the current paused song
     */
    public void resumeSong() {
        mMediaPlayer.start();
        notifySongChanged();
    }

    /**
     * Returns the playing status of mNowPlayingSong
     *
     * @return true or false
     */
    public boolean isNowPlaying() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * Notifies all instance of {@link MusicPlayerCallback} about song change
     */
    private void notifySongChanged() {
        Loggy.entryLog();
        Loggy.log(Log.INFO, "mCallbackList.size() = " + mCallbackList.size());
        if (!mCallbackList.isEmpty()) {
            for (MusicPlayerCallback musicPlayerCallback : mCallbackList) {
                musicPlayerCallback.notifySongChanged(mNowPlayingSong);
            }
        }
        Loggy.exitLog();
    }

    /**
     * Invokes when current playing song is complete
     *
     * @param mediaPlayer : Instance f {@link MediaPlayer}
     */
    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        Loggy.entryLog();
        boolean repeatSong = SharedUtil.getRepeatSongPref();
        Loggy.log(Log.VERBOSE, "repeatSong = " + repeatSong);
        if (repeatSong) {
            startMusic();
        } else {
            startNextSong();
        }
        Loggy.exitLog();
    }

    /**
     * Starts playing next song is playlist, if it has any.
     */
    public void startNextSong() {
        Loggy.entryLog();
        int nextPosition = -1;
        for (int i = 0; i < mPlaylist.size() - 1; i++) {
            if (mPlaylist.get(i).getId() == mNowPlayingSong.getId()) {
                nextPosition = i + 1;
                break;
            }
        }
        if (nextPosition != -1) {
            mNowPlayingSong = mPlaylist.get(nextPosition);
            startMusic();
        }
        Loggy.exitLog();
    }

    /**
     * Starts playing previous song, if it has any
     */
    public void startPreviousSong() {
        Loggy.entryLog();
        int prevPosition = -1;
        for (int i = 0; i < mPlaylist.size(); i++) {
            if (mPlaylist.get(i).getId() == mNowPlayingSong.getId()) {
                prevPosition = i - 1;
                break;
            }
        }
        if (prevPosition != -1) {
            mNowPlayingSong = mPlaylist.get(prevPosition);
            startMusic();
        }
        Loggy.exitLog();
    }

    /**
     * Returns current playing position in percent values
     *
     * @return percent value
     */
    @Override
    public int getCurrentPositionByPercentValue() {
        int currentPosition = 0;
        if (mMediaPlayer != null) {
            try {
                currentPosition = (mMediaPlayer.getCurrentPosition() * 100) / mNowPlayingSong
                        .getDuration();
            } catch (IllegalStateException exception) {
                Loggy.exceptionLog(exception);
            }
        }
        return currentPosition;
    }

    /**
     * Returns current playing position
     *
     * @return
     */
    @Override
    public int getCurrentPosition() {
        int position = 0;
        if (mMediaPlayer != null) {
            position = mMediaPlayer.getCurrentPosition();
        }
        return position;
    }

    /**
     * Seek {@link MediaPlayer} to a specific position
     *
     * @param progress : position to seek to
     */
    @Override
    public void seekTo(final int progress) {
        Loggy.entryLog();
        mMediaPlayer.seekTo((mNowPlayingSong.getDuration() * progress) / 100);
        Loggy.exitLog();
    }

    /**
     * Returns current playlist
     *
     * @return List<Song>
     */
    @Override
    public List<Song> getPlayList() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mPlaylist;
    }

    /**
     * Returns instance of now playing song
     *
     * @return
     */
    @Override
    public Song getNowPlayingSong() {
        Loggy.entryLog();
        Loggy.exitLog();
        return mNowPlayingSong;
    }

    /**
     * Starts status bar player
     *
     * @param id           : Id for notification
     * @param notification : Instance of {@link Notification}
     */
    public void startStatusBarPlayer(final int id, final Notification notification) {
        Loggy.entryLog();
        mMusicPlayerService.startForeground(id, notification);
        Loggy.exitLog();
    }

    /**
     * Invoked when status bar controls are triggered
     *
     * @param intent : Instance of {@link Intent}
     */
    public void onStatusBarEvent(final Intent intent) {
        Loggy.entryLog();
        mStatusBarPlayer.onStatusBarEvent(intent);
        Loggy.exitLog();
    }

    /**
     * Stops status bar player
     */
    public void stopStatusBarPlayer() {
        Loggy.entryLog();
        mMediaPlayer.stop();
        notifySongChanged();
        mMusicPlayerService.stopForeground(true);
        mMusicPlayerService.stopSelf();
        Loggy.exitLog();
    }

}
