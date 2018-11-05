package com.workshop.quest.musicplayer.view.musicplayer;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.model.Song;

import java.util.TimerTask;

import static com.workshop.quest.musicplayer.view.musicplayer.MusicPlayerActivity_old.getTime;

public class MusicPlayerView implements IMusicPlayerView, SeekBar.OnSeekBarChangeListener {

    private MusicPlayerActivity mMusicPlayerActivity;

    private TextView mCurrentTime;

    private TextView mTotalTime;

    private TextView mTrackName;

    private TextView mArtistName;

    private SeekBar mSeekBar;

    private ImageView mAlbumArt;

    private ImageView mPlayButton;

    private ImageView mShuffleButton;

    private ImageView mRepeatButton;

    private ImageView mFavIcon;

    private IMusicPlayerPresenter mMusicPlayerPresenter;

    public MusicPlayerView(MusicPlayerActivity musicPlayerActivity) {
        mMusicPlayerActivity = musicPlayerActivity;
        initUI();
        mMusicPlayerPresenter = new MusicPlayerPresenter(musicPlayerActivity);
        mMusicPlayerPresenter.handleViewStart(musicPlayerActivity.getIntent());
    }

    @SuppressLint("ResourceType")
    private void initUI() {
        mMusicPlayerActivity.setContentView(R.layout.activity_music_player);
        mCurrentTime = mMusicPlayerActivity.findViewById(R.layout.activity_music_player);
        mPlayButton = mMusicPlayerActivity.findViewById(R.id.play);
        mAlbumArt = mMusicPlayerActivity.findViewById(R.id.album_art);
        mSeekBar = mMusicPlayerActivity.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mTrackName = mMusicPlayerActivity.findViewById(R.id.track_title);
        mArtistName = mMusicPlayerActivity.findViewById(R.id.artist);
        mTotalTime = mMusicPlayerActivity.findViewById(R.id.total_time);
        mCurrentTime = mMusicPlayerActivity.findViewById(R.id.current_time);
        mShuffleButton = mMusicPlayerActivity.findViewById(R.id.shuffle);
        mRepeatButton = mMusicPlayerActivity.findViewById(R.id.repeat);
        mFavIcon = mMusicPlayerActivity.findViewById(R.id.fav_icon);
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (fromUser) {
            mMusicPlayerPresenter.onSeekProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {

    }

    @Override
    public void updateNowPlayingSong(final Song song) {

    }

    @Override
    public void updateProgress(final String time, final int progressPercent) {
        mSeekBar.setProgress(progressPercent);
        mCurrentTime.setText(time);
    }

    public void onStart() {
        mMusicPlayerPresenter.onStart();
    }

    public void onStop() {
        mMusicPlayerPresenter.onStop();
    }
}
