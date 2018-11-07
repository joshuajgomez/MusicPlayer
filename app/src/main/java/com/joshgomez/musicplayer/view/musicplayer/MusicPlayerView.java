package com.joshgomez.musicplayer.view.musicplayer;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.DateUtil;
import com.joshgomez.musicplayer.generic.ResUtil;
import com.joshgomez.musicplayer.generic.log.Loggy;
import com.joshgomez.musicplayer.model.Song;
import com.joshgomez.musicplayer.view.fragment.PlayListFragment;

import java.util.List;

/**
 * Handles all UI operations of {@link MusicPlayerActivity}
 */
public class MusicPlayerView implements IMusicPlayerView, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener{

    /**
     * String constant to be used as tag when building Play list fragment
     */
    private static final String TAG_PLAY_LIST_FRAGMENT = "TAG_PLAY_LIST_FRAGMENT";
    /**
     * Instance of {@link MusicPlayerActivity}
     */
    private MusicPlayerActivity mMusicPlayerActivity;
    /**
     * TextView showing current time
     */
    private TextView mCurrentTime;
    /**
     * TextView showing total time
     */
    private TextView mTotalTime;
    /**
     * TextView showing track name
     */
    private TextView mTrackName;
    /**
     * TextView showing artist name
     */
    private TextView mArtistName;
    /**
     * Seekbar for showing music playing progress
     */
    private SeekBar mSeekBar;
    /**
     * ImageView showing Album Art
     */
    private ImageView mAlbumArt;
    /**
     * ImageView showing Play/Pause button
     */
    private ImageView mPlayButton;
    /**
     * Instance of {@link IMusicPlayerPresenter}
     */
    private IMusicPlayerPresenter mMusicPlayerPresenter;

    /**
     * Constructor for {@link MusicPlayerView}. Initialises {@link MusicPlayerPresenter} and
     * UI elements
     * @param musicPlayerActivity Instance of {@link MusicPlayerActivity}
     */
    MusicPlayerView(MusicPlayerActivity musicPlayerActivity) {
        Loggy.entryLog();
        mMusicPlayerActivity = musicPlayerActivity;
        initUI();
        mMusicPlayerPresenter = new MusicPlayerPresenter(this, musicPlayerActivity);
        Loggy.exitLog();
    }

    /**
     * Initialises UI elements
     */
    @SuppressLint("ResourceType")
    private void initUI() {
        Loggy.entryLog();
        mMusicPlayerActivity.setContentView(R.layout.activity_music_player);
        mCurrentTime = mMusicPlayerActivity.findViewById(R.layout.activity_music_player);
        mPlayButton = mMusicPlayerActivity.findViewById(R.id.play);
        mAlbumArt = mMusicPlayerActivity.findViewById(R.id.album_art);
        mSeekBar = mMusicPlayerActivity.findViewById(R.id.seek_bar);
        mTrackName = mMusicPlayerActivity.findViewById(R.id.track_title);
        mArtistName = mMusicPlayerActivity.findViewById(R.id.artist);
        mTotalTime = mMusicPlayerActivity.findViewById(R.id.total_time);
        mCurrentTime = mMusicPlayerActivity.findViewById(R.id.current_time);

        mSeekBar.setOnSeekBarChangeListener(this);
        mPlayButton.setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.next).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.prev).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.info).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.share).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.playlist).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.fav_icon).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.repeat).setOnClickListener(this);
        mMusicPlayerActivity.findViewById(R.id.shuffle).setOnClickListener(this);
        Loggy.exitLog();
    }

    /**
     * Invoked with interaction of Seek bar.
     * @param seekBar Instance of {@link SeekBar}
     * @param progress Integer value showing progress
     * @param fromUser boolean : true if triggered from user
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mMusicPlayerPresenter.onSeekProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        Loggy.entryLog();
        Loggy.exitLog();
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        Loggy.entryLog();
        Loggy.exitLog();
    }

    @Override
    public void updateNowPlayingSong(Song song, boolean isNowPlaying, boolean isFavourite) {
        Loggy.entryLog();
        if (song != null) {
            mPlayButton.setImageResource(isNowPlaying
                    ? ResUtil.getResId(R.attr.pauseIcon, mMusicPlayerActivity)
                    : ResUtil.getResId(R.attr.playIcon, mMusicPlayerActivity));
            mTrackName.setText(song.getTrack());
            mTrackName.setSelected(true);
            mArtistName.setSelected(true);
            mArtistName.setText(song.getArtist());
            mCurrentTime.setText(DateUtil.getPrettyTime(0));
            mTotalTime.setText(DateUtil.getPrettyTime(song.getDuration()));
            mAlbumArt.setImageBitmap(song.getCoverArt(mMusicPlayerActivity));
            mAlbumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);

            PlayListFragment fragment = (PlayListFragment) mMusicPlayerActivity
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.playlist_fragment);
            if (fragment != null && fragment.isInLayout())
                fragment.setCurrentSong(song);
            else {
                PlayListFragment fragment2 = (PlayListFragment) mMusicPlayerActivity
                        .getSupportFragmentManager()
                        .findFragmentByTag(TAG_PLAY_LIST_FRAGMENT);
                if (fragment2 != null) {
                    fragment2.setCurrentSong(song);
                }
            }
            updateViewImage(R.id.fav_icon, isFavourite ? R.mipmap.fav_on : R.mipmap.fav_off);
        } else {
            Loggy.log(Log.WARN, "song is null");
        }
        Loggy.exitLog();
    }

    @Override
    public void updateProgress(final String time, final int progressPercent) {
        mSeekBar.setProgress(progressPercent);
        mCurrentTime.setText(time);
    }

    @Override
    public void updateViewImage(final int viewId, final int imageRes) {
        Loggy.entryLog();
        ImageView imageView = mMusicPlayerActivity.findViewById(viewId);
        imageView.setImageResource(imageRes);
        Loggy.exitLog();
    }

    @Override
    public void updatePlayList(List<Song> playList) {
        Loggy.entryLog();
        PlayListFragment fragment = (PlayListFragment) mMusicPlayerActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.playlist_fragment);
        if (fragment != null && fragment.isInLayout())
            fragment.setSongList(playList);
        Loggy.exitLog();
    }

    @Override
    public void stopActivity() {
        Loggy.entryLog();
        mMusicPlayerActivity.finish();
        Loggy.exitLog();
    }

    @Override
    public void showPlaylist(Song nowPlayingSong, List<Song> songList) {
        Loggy.entryLog();
        mMusicPlayerActivity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, PlayListFragment.newInstance(nowPlayingSong, songList),
                        TAG_PLAY_LIST_FRAGMENT)
                .addToBackStack(null)
                .commit();
        Loggy.exitLog();
    }

    void onStart() {
        Loggy.entryLog();
        mMusicPlayerPresenter.onStart();
        Loggy.exitLog();
    }

    void onStop() {
        Loggy.entryLog();
        mMusicPlayerPresenter.onStop();
        Loggy.exitLog();
    }

    @Override
    public void onClick(View view) {
        Loggy.entryLog();
        mMusicPlayerPresenter.onClickEvent(view.getId());
        Loggy.exitLog();
    }

    public void onPlaylistClick(Song song, List<Song> list) {
        Loggy.entryLog();
        Loggy.log(Log.INFO, "song = [" + song + "], list = [" + list + "]");
        mMusicPlayerPresenter.onPlaylistClick(song, list);
        Loggy.exitLog();
    }

}
