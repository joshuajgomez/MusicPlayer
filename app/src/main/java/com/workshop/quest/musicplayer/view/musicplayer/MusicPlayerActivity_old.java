package com.workshop.quest.musicplayer.view.musicplayer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.workshop.quest.musicplayer.generic.SharedUtil;
import com.workshop.quest.musicplayer.generic.database.DatabaseManager;
import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.service.musicmanager.IMusicPlayer;
import com.workshop.quest.musicplayer.service.musicmanager.MusicPlayerCallback;
import com.workshop.quest.musicplayer.service.MusicPlayerService;
import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.base.BaseActivity;
import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.fragment.PlayListFragment;
import com.workshop.quest.musicplayer.view.fragment.SongInfoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerActivity_old extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
        MusicPlayerCallback, PlayListFragment.PlayListInteractor {

    private TextView currentTime;
    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;
    private SeekBar seekBar;
    private ImageView albumArt;
    private ImageView playButton;
    private ImageView shuffleButton;
    private ImageView repeatButton;
    private Handler handler;
    private Timer timer = new Timer();
    private String PLAY_LIST_FRAGMENT = "PLAY_LIST_FRAGMENT";
    IMusicPlayer mMusicPlayer;
    ImageView favIcon;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) service;
            mMusicPlayer = musicBinder.getMusicPlayer();
            mMusicPlayer.registerCallback(MusicPlayerActivity_old.this);
            initUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    private boolean isBound;
    private boolean shuffleSong;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        playButton = findViewById(R.id.play);
        albumArt = findViewById(R.id.album_art);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        trackName = findViewById(R.id.track_title);
        artistName = findViewById(R.id.artist);
        totalTime = findViewById(R.id.total_time);
        currentTime = findViewById(R.id.current_time);
        shuffleButton = findViewById(R.id.shuffle);
        repeatButton = findViewById(R.id.repeat);
        favIcon = findViewById(R.id.fav_icon);
        handler = new Handler();
    }

    private void initUI() {
        Song song;
        if (getIntent().hasExtra("song")) {
            songList = getIntent().getParcelableArrayListExtra("song_list");
            song = getIntent().getParcelableExtra("song");
            mMusicPlayer.initMusicPlayer(song, songList);
            getIntent().removeExtra("song");
        } else {
            song = mMusicPlayer.getNowPlayingSong();
            songList = (ArrayList<Song>) mMusicPlayer.getPlayList();
        }

        notifySongChanged(song);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(mMusicPlayer.getCurrentPositionByPercentValue());
                handler.post(() -> currentTime.setText(getTime(mMusicPlayer.getCurrentPosition())));
            }
        }, 0, 500);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, MusicPlayerService.class);
        bindService(service, musicServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(musicServiceConnection);
        }
    }

    public void playButtonClick(View view) {
        if (mMusicPlayer.isNowPlaying()) {
            mMusicPlayer.pauseSong();
            playButton.setImageResource(ResUtil.getResId(R.attr.playIcon, this));
        } else {
            mMusicPlayer.resumeSong();
            playButton.setImageResource(ResUtil.getResId(R.attr.pauseIcon, this));
        }

    }

    public void nextButton(View view) {
        mMusicPlayer.startNextSong();
    }

    public void shuffleButton(View view) {
        if (shuffleSong) {
            shuffleSong = false;
            shuffleButton.setImageResource(ResUtil.getResId(R.attr.shuffleIconOff, this));
            mMusicPlayer.initMusicPlayer(songList.get(0), songList);
        } else {
            shuffleSong = true;
            shuffleButton.setImageResource(ResUtil.getResId(R.attr.shuffleIconOn, this));

            List<Song> playList = (List<Song>) songList.clone();
            Collections.shuffle(playList);
            mMusicPlayer.initMusicPlayer(playList.get(0), playList);
        }

        PlayListFragment fragment = (PlayListFragment) getSupportFragmentManager().findFragmentById(R.id.playlist_fragment);
        if (fragment != null && fragment.isInLayout())
            fragment.setSongList(mMusicPlayer.getPlayList());
    }

    public void prevButton(View view) {
        mMusicPlayer.startPreviousSong();
    }

    public void repeatButton(View view) {
        if (SharedUtil.getRepeatSongPref()) {
            repeatButton.setImageResource(ResUtil.getResId(R.attr.repeatIconOff, this));
            SharedUtil.setRepeatSongPref(false);
        } else {
            repeatButton.setImageResource(ResUtil.getResId(R.attr.repeatIconOn, this));
            SharedUtil.setRepeatSongPref(true);
        }
    }

    public void closeButton(View view) {
        this.finish();
    }

    public void playlistButton(View view) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            mMusicPlayer.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @SuppressLint("DefaultLocale")
    public static String getTime(int millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }

    public void shareButton(View view) {
        Song currentSong = mMusicPlayer.getNowPlayingSong();
        Uri uri = Uri.parse(currentSong.getSongUrl());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share " + currentSong.getTrack()));
    }

    public static void play(Context context, ArrayList<Song> songList, Song song) {
        Intent intent = new Intent(context, MusicPlayerActivity_old.class);
        intent.putExtra("song", song);
        intent.putExtra("song_list", songList);
        context.startActivity(intent);
    }

    public void albumClick(View view) {
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.addSongToFavourites(mMusicPlayer.getNowPlayingSong());
        showToast("Song added to favourites");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void favIconClick(View view) {
        DatabaseManager databaseManager = new DatabaseManager(this);
        Song currentSong = mMusicPlayer.getNowPlayingSong();
        Log.println(Log.ASSERT, "favIconClick id", currentSong.getId() + "");
        if (databaseManager.isFavourite(currentSong)) {
            databaseManager.removeSongFromFavourites(currentSong);
            favIcon.setImageResource(R.mipmap.fav_off);
        } else {
            databaseManager.addSongToFavourites(currentSong);
            favIcon.setImageResource(R.mipmap.fav_on);
        }
    }

    public void infoButton(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, SongInfoFragment.newInstance(mMusicPlayer.getNowPlayingSong()), "song_info_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void notifySongChanged(final Song song) {
        if (song != null) {
        playButton.setImageResource(mMusicPlayer.isNowPlaying() ? ResUtil.getResId(R.attr
                .pauseIcon, this) : ResUtil.getResId(R.attr.playIcon, this));
        trackName.setText(song.getTrack());
        trackName.setSelected(true);
        artistName.setSelected(true);
        artistName.setText(song.getArtist());
        currentTime.setText(getTime(0));
        totalTime.setText(getTime(song.getDuration()));
        albumArt.setImageBitmap(song.getCoverArt(this));
        albumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);
        PlayListFragment fragment = (PlayListFragment) getSupportFragmentManager().findFragmentById(R.id.playlist_fragment);
        if (fragment != null && fragment.isInLayout())
            fragment.setCurrentSong(song);
        else {
            PlayListFragment fragment2 = (PlayListFragment) getSupportFragmentManager()
                    .findFragmentByTag(PLAY_LIST_FRAGMENT);
            if (fragment2 != null) {
                fragment2.setCurrentSong(song);
            }
        }
        DatabaseManager databaseManager = new DatabaseManager(this);
        favIcon.setImageResource(databaseManager.isFavourite(song) ? R.mipmap.fav_on : R.mipmap.fav_off);
        } else {
            Loggy.log(Log.WARN, "song is null");
        }
    }

    @Override
    public void onPlaylistClick(Song song, List<Song> list) {

    }
}
