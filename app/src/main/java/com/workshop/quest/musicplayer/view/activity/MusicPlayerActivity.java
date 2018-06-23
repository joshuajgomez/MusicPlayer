package com.workshop.quest.musicplayer.view.activity;

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

import com.workshop.quest.musicplayer.generic.database.DatabaseManager;
import com.workshop.quest.musicplayer.service.MusicPlayerService;
import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.base.BaseActivity;
import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.fragment.PlayListFragment;
import com.workshop.quest.musicplayer.view.fragment.SongInfoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerActivity extends BaseActivity
        implements SeekBar.OnSeekBarChangeListener,
        MusicPlayerService.MusicServiceCallback,
        PlayListFragment.PlayListInteractor {

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
    MusicPlayerService musicPlayerService;
    ImageView favIcon;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) service;
            musicPlayerService = musicBinder.getMusicPlayerService();
            musicPlayerService.setServiceCallback(MusicPlayerActivity.this);
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
            musicPlayerService.playSong(song, songList);
            getIntent().removeExtra("song");
        } else {
            song = musicPlayerService.getCurrentSong();
            songList = musicPlayerService.getPlayList();
        }

        updateUI(song);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(musicPlayerService.getPercentValue());
                handler.post(() -> currentTime.setText(getTime(musicPlayerService.getCurrentPosition())));
            }
        }, 0, 500);

    }

    @Override
    public void updateUI(Song song) {

        if (song == null) return;

        playButton.setImageResource(musicPlayerService.isPlaying() ? ResUtil.getResId(R.attr.pauseIcon, this) : ResUtil.getResId(R.attr.playIcon, this));
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
        if (musicPlayerService.isPlaying()) {
            musicPlayerService.pauseSong();
            playButton.setImageResource(ResUtil.getResId(R.attr.playIcon, this));
        } else {
            musicPlayerService.resumeSong();
            playButton.setImageResource(ResUtil.getResId(R.attr.pauseIcon, this));
        }

    }

    public void nextButton(View view) {
        musicPlayerService.nextSong();
    }

    public void shuffleButton(View view) {
        if (shuffleSong) {
            shuffleSong = false;
            shuffleButton.setImageResource(ResUtil.getResId(R.attr.shuffleIconOff, this));
            musicPlayerService.playSong(songList.get(0), songList);
        } else {
            shuffleSong = true;
            shuffleButton.setImageResource(ResUtil.getResId(R.attr.shuffleIconOn, this));
            ArrayList<Song> playList = (ArrayList<Song>) songList.clone();
            Collections.shuffle(playList);
            musicPlayerService.playSong(playList.get(0), playList);
        }

        PlayListFragment fragment = (PlayListFragment) getSupportFragmentManager().findFragmentById(R.id.playlist_fragment);
        if (fragment != null && fragment.isInLayout())
            fragment.setSongList(musicPlayerService.getPlayList());
    }

    public void prevButton(View view) {
        musicPlayerService.previousSong();
    }

    public void repeatButton(View view) {
        if (musicPlayerService.isRepeatSong()) {
            repeatButton.setImageResource(ResUtil.getResId(R.attr.repeatIconOff, this));
            musicPlayerService.setRepeatSong(false);
        } else {
            repeatButton.setImageResource(ResUtil.getResId(R.attr.repeatIconOn, this));
            musicPlayerService.setRepeatSong(true);
        }
    }

    public void closeButton(View view) {
        this.finish();
    }

    public void playlistButton(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, PlayListFragment.newInstance(), PLAY_LIST_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            musicPlayerService.seekTo(progress);
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
        Song currentSong = musicPlayerService.getCurrentSong();
        Uri uri = Uri.parse(currentSong.getSongUrl());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share " + currentSong.getTrack()));
    }

    @Override
    public void playSong(Song song, ArrayList<Song> list) {
        musicPlayerService.playSong(song, list);
    }

    @Override
    public Song getCurrentSong() {
        if (musicPlayerService != null)
            return musicPlayerService.getCurrentSong();
        else return null;
    }

    @Override
    public ArrayList<Song> getCurrentSongList() {
        return musicPlayerService.getPlayList();
    }

    public static void play(Context context, ArrayList<Song> songList, Song song) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.putExtra("song", song);
        intent.putExtra("song_list", songList);
        context.startActivity(intent);
    }

    public void albumClick(View view) {
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.addSongToFavourites(musicPlayerService.getCurrentSong());
        showToast("Song added to favourites");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void favIconClick(View view) {
        DatabaseManager databaseManager = new DatabaseManager(this);
        Song currentSong = musicPlayerService.getCurrentSong();
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
                .replace(R.id.frame_layout, SongInfoFragment.newInstance(musicPlayerService.getCurrentSong()), "song_info_fragment")
                .addToBackStack(null)
                .commit();
    }
}
