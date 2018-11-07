package com.joshgomez.musicplayer.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.model.Song;
import com.joshgomez.musicplayer.service.musicmanager.IMusicPlayer;
import com.joshgomez.musicplayer.service.MusicPlayerService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class IntentMusicActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener {

    private TextView trackName;
    private TextView artistName;
    private SeekBar seekBar;
    private ImageView albumArt;
    private ImageView playButton;
    private Song song;
    private CardView cardView;
    private boolean isBound;
    private IMusicPlayer mMusicPlayer;
    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) service;
            mMusicPlayer = musicBinder.getMusicPlayer();


            song = getSong();
            ArrayList<Song> songs = new ArrayList<Song>();
            songs.add(song);
            mMusicPlayer.initMusicPlayer(song, songs);
            mMusicPlayer.setCompletionListener(IntentMusicActivity.this);
            albumArt.setImageBitmap(getCoverArt(song));
            try {
                trackName.setText(song.getTrack());
            } catch (Exception e) {
                e.printStackTrace();
                trackName.setText("Unknown track");
            }
            artistName.setText(song.getArtist());

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        seekBar.setProgress(mMusicPlayer.getCurrentPositionByPercentValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 500);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_intent);

        playButton = findViewById(R.id.play);
        albumArt = findViewById(R.id.album_art);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        trackName = findViewById(R.id.track_title);
        artistName = findViewById(R.id.artist);
        cardView = findViewById(R.id.container);
        cardView.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        playButton.setOnClickListener(v -> playButtonClick());

        startService(new Intent(this, MusicPlayerService.class));
    }

    private Bitmap getCoverArt(Song song) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(song.getSongUrl());
        byte[] art = metaRetriver.getEmbeddedPicture();
        return art != null
                ? BitmapFactory.decodeByteArray(art, 0, art.length)
                : BitmapFactory.decodeResource(getResources(), R.drawable.no_cover);
    }

    private void playButtonClick() {
        if (mMusicPlayer.isNowPlaying()) {
            mMusicPlayer.pauseSong();
            playButton.setImageResource(R.mipmap.play_white);
        } else {
            mMusicPlayer.resumeSong();
            playButton.setImageResource(R.mipmap.pause_white);
        }
    }

    private Song getSong() {
        String dataString = getIntent().getDataString();
        dataString = dataString.replace("file:///", "");
        try {
            dataString = URLDecoder.decode(dataString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(dataString);
        String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String fileName = new File(dataString).getName();
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Song song = new Song();
        song.setArtist(artist);
        song.setTrack(title == null ? fileName : title);
        song.setSongUrl(dataString);
        song.setFileName(fileName);
        song.setDuration(Integer.parseInt(duration));
        return song;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playButton.setImageResource(R.mipmap.play_white);
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
}