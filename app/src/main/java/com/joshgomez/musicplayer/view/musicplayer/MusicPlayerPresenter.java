package com.joshgomez.musicplayer.view.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.Constants;
import com.joshgomez.musicplayer.generic.DateUtil;
import com.joshgomez.musicplayer.generic.ResUtil;
import com.joshgomez.musicplayer.generic.SharedUtil;
import com.joshgomez.musicplayer.generic.database.DatabaseManager;
import com.joshgomez.musicplayer.generic.log.Loggy;
import com.joshgomez.musicplayer.model.Song;
import com.joshgomez.musicplayer.view.fragment.SongInfoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.joshgomez.musicplayer.generic.Constants.EXTRA_SONG;
import static com.joshgomez.musicplayer.generic.Constants.EXTRA_SONG_LIST;


/**
 * Presenter class for Music Player Screen
 */
public class MusicPlayerPresenter implements IMusicPlayerPresenter {

    private static final String TAG_INFO_FRAGMENT = "TAG_INFO_FRAGMENT";
    /**
     * Instance of {@link IMusicPlayerView}
     */
    private IMusicPlayerView mMusicPlayerView;
    /**
     * Instance of {@link IMusicPlayerModel}
     */
    private IMusicPlayerModel mMusicPlayerModel;
    /**
     * Handler for updating UI from worker thread
     */
    private Handler mHandler;
    /**
     * Timer for continuously updating current time status
     */
    private Timer mTimer;
    /**
     * Instance of {@link Context}
     */
    private Context mContext;
    /**
     * Instance of {@link MusicPlayerActivity}
     */
    private MusicPlayerActivity mMusicPlayerActivity;

    /**
     * Constructor for {@link MusicPlayerPresenter}. Initialises {@link MusicPlayerModel} class
     * and other class members.
     *
     * @param musicPlayerView     Instance of {@link IMusicPlayerView}
     * @param musicPlayerActivity Instance of {@link MusicPlayerActivity}
     */
    MusicPlayerPresenter(IMusicPlayerView musicPlayerView, MusicPlayerActivity
            musicPlayerActivity) {
        Loggy.entryLog();
        mContext = musicPlayerActivity;
        mMusicPlayerActivity = musicPlayerActivity;
        mMusicPlayerView = musicPlayerView;
        mMusicPlayerModel = new MusicPlayerModel(musicPlayerActivity, this);
        mHandler = new Handler();
        mTimer = new Timer();
        Loggy.exitLog();
    }

    /**
     * Invoked when UI elements in Music Player Screen is clicked
     *
     * @param viewId ID of clicked view
     */
    @Override
    public void onClickEvent(int viewId) {
        Loggy.entryLog();
        switch (viewId) {
            case R.id.play: {
                int playIcon;
                if (mMusicPlayerModel.isNowPlaying()) {
                    mMusicPlayerModel.pauseSong();
                    playIcon = ResUtil.getResId(R.attr.playIcon, mContext);
                } else {
                    mMusicPlayerModel.resumeSong();
                    playIcon = ResUtil.getResId(R.attr.pauseIcon, mContext);
                }
                mMusicPlayerView.updateViewImage(viewId, playIcon);
                break;
            }
            case R.id.next: {
                mMusicPlayerModel.startNextSong();
                break;
            }
            case R.id.prev: {
                mMusicPlayerModel.startPreviousSong();
                break;
            }
            case R.id.shuffle: {
                boolean shuffleSong;
                int shuffleIcon;
                ArrayList<Song> songList = (ArrayList<Song>) mMusicPlayerModel.getSongList();
                List<Song> playList;
                if (mMusicPlayerModel.isShuffleEnabled()) {
                    shuffleSong = false;
                    shuffleIcon = ResUtil.getResId(R.attr.shuffleIconOff, mContext);
                    playList = songList;
                } else {
                    shuffleSong = true;
                    shuffleIcon = ResUtil.getResId(R.attr.shuffleIconOn, mContext);
                    List<Song> buffer = (List<Song>) songList.clone();
                    Collections.shuffle(buffer);
                    playList = buffer;
                }
                mMusicPlayerView.updateViewImage(viewId, shuffleIcon);
                mMusicPlayerModel.initMusicPlayer(playList.get(0), playList);
                mMusicPlayerModel.setShuffleEnabled(shuffleSong);
                mMusicPlayerView.updatePlayList(playList);
                break;
            }
            case R.id.repeat: {
                int repeatIcon;
                if (SharedUtil.getRepeatSongPref()) {
                    repeatIcon = ResUtil.getResId(R.attr.repeatIconOff, mContext);
                    SharedUtil.setRepeatSongPref(false);
                } else {
                    repeatIcon = ResUtil.getResId(R.attr.repeatIconOn, mContext);
                    SharedUtil.setRepeatSongPref(true);
                }
                mMusicPlayerView.updateViewImage(viewId, repeatIcon);
                break;
            }
            case R.id.close: {
                mMusicPlayerView.stopActivity();
                break;
            }
            case R.id.playlist: {
                Song nowPlayingSong = mMusicPlayerModel.getNowPlayingSong();
                List<Song> songList = mMusicPlayerModel.getSongList();
                mMusicPlayerView.showPlaylist(nowPlayingSong, songList);
                break;
            }
            case R.id.share: {
                Song nowPlayingSong = mMusicPlayerModel.getNowPlayingSong();
                Uri uri = Uri.parse(nowPlayingSong.getSongUrl());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                mContext.startActivity(Intent.createChooser(share, Constants.SHARE_MUSIC_TITLE));
                break;
            }
            case R.id.fav_icon: {
                int favIcon;
                DatabaseManager databaseManager = new DatabaseManager(mContext);
                Song nowPlayingSong = mMusicPlayerModel.getNowPlayingSong();
                if (databaseManager.isFavourite(nowPlayingSong)) {
                    databaseManager.removeSongFromFavourites(nowPlayingSong);
                    favIcon = R.mipmap.fav_off;
                } else {
                    databaseManager.addSongToFavourites(nowPlayingSong);
                    favIcon = R.mipmap.fav_on;
                }
                mMusicPlayerView.updateViewImage(viewId, favIcon);
                break;
            }
            case R.id.info: {
                Song nowPlayingSong = mMusicPlayerModel.getNowPlayingSong();
                mMusicPlayerActivity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, SongInfoFragment.newInstance(nowPlayingSong),
                                TAG_INFO_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            default: {
                Loggy.log(Log.WARN, "Invalid viewId = " + viewId);
                break;
            }

        }
        Loggy.exitLog();
    }

    /**
     * Invoked at interaction of progress seek bar
     *
     * @param progress Integer value of progress
     */
    @Override
    public void onSeekProgress(final int progress) {
        Loggy.entryLog();
        mMusicPlayerModel.onSeekProgress(progress);
        Loggy.exitLog();
    }

    /**
     * Invoked when {@link com.joshgomez.musicplayer.service.MusicPlayerService}
     * is successfully binded to {@link MusicPlayerModel}
     */
    @Override
    public void onBindComplete() {
        Loggy.entryLog();
        Song song;
        Intent intent = mMusicPlayerActivity.getIntent();
        List<Song> songList;
        if (intent != null && intent.hasExtra(EXTRA_SONG)) {
            songList = intent.getParcelableArrayListExtra(EXTRA_SONG_LIST);
            song = intent.getParcelableExtra(EXTRA_SONG);
            intent.removeExtra(EXTRA_SONG);
            mMusicPlayerModel.initMusicPlayer(song, songList);
            mMusicPlayerModel.setPlaylist(songList);
        } else {
            song = mMusicPlayerModel.getNowPlayingSong();
        }
        notifySongChanged(song);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    int percentValue = mMusicPlayerModel.getCurrentPositionByPercentValue();
                    String time = DateUtil.getPrettyTime(mMusicPlayerModel.getCurrentPosition());
                    mMusicPlayerView.updateProgress(time, percentValue);
                });
            }
        }, 0, 500);
        Loggy.exitLog();
    }

    /**
     * Invoked when {@link MusicPlayerActivity} is started
     */
    @Override
    public void onStart() {
        Loggy.entryLog();
        mMusicPlayerModel.onStart();
        Loggy.exitLog();
    }

    /**
     * Invoked when {@link MusicPlayerActivity} is stopped
     */
    @Override
    public void onStop() {
        Loggy.entryLog();
        mMusicPlayerModel.onStop();
        Loggy.exitLog();
    }

    /**
     * Notifies {@link IMusicPlayerView} when now playing song is changed
     *
     * @param song Now playing song
     */
    @Override
    public void notifySongChanged(Song song) {
        Loggy.entryLog();
        boolean isNowPlaying = mMusicPlayerModel.isNowPlaying();
        DatabaseManager databaseManager = new DatabaseManager(mContext);
        boolean isFavourite = databaseManager.isFavourite(song);
        mMusicPlayerView.updateNowPlayingSong(song, isNowPlaying, isFavourite);
        Loggy.exitLog();
    }

    @Override
    public void onPlaylistClick(Song song, List<Song> list) {
        Loggy.entryLog();
        mMusicPlayerModel.initMusicPlayer(song, list);
        Loggy.exitLog();
    }
}
