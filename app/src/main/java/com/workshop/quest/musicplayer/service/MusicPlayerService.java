package com.workshop.quest.musicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.drm.DrmStore;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.activity.IntentMusicActivity;
import com.workshop.quest.musicplayer.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    private MusicBinder musicBinder = new MusicBinder();
    private Song song;
    private ArrayList<Song> playList;
    private boolean repeatSong = false;
    private MediaPlayer mediaPlayer;
    MusicServiceCallback serviceCallback;
    public static String MAIN_ACTION = "com.workshop.quest.musicplayer.action.main";
    public static String INIT_ACTION = "com.workshop.quest.musicplayer.action.init";
    public static String PREV_ACTION = "com.workshop.quest.musicplayer.action.prev";
    public static String PLAY_ACTION = "com.workshop.quest.musicplayer.action.play";
    public static String NEXT_ACTION = "com.workshop.quest.musicplayer.action.next";
    public static String STARTFOREGROUND_ACTION = "com.workshop.quest.musicplayer.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.workshop.quest.musicplayer.action.stopforeground";
    MediaSessionCompat mMediaSessionCompat;
    MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            resumeSong();
            Log.println(Log.ASSERT, "onPlay", "called");
        }

        @Override
        public void onPause() {
            super.onPause();
            pauseSong();
            Log.println(Log.ASSERT, "onPause", "called");
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            nextSong();
            Log.println(Log.ASSERT, "onSkipToNext", "called");
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            previousSong();
            Log.println(Log.ASSERT, "onSkipToPrevious", "called");
        }
    };


    public MusicPlayerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSession();
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "MusicService", mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

//        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }


    private void initStatusBar() {
        Notification notification = null;

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MusicPlayerService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayerService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayerService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, MusicPlayerService.class);
        closeIntent.setAction(STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setImageViewResource(R.id.play, isPlaying() ? R.mipmap.pause_white : R.mipmap.play_white);

        views.setOnClickPendingIntent(R.id.play, pplayIntent);

        views.setOnClickPendingIntent(R.id.next, pnextIntent);

        views.setOnClickPendingIntent(R.id.prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.close, pcloseIntent);

        views.setImageViewBitmap(R.id.image, song.getCoverArt(getApplicationContext()));

        views.setTextViewText(R.id.title, song.getTrack());

        views.setTextViewText(R.id.artist, song.getArtist());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this).build();
            notification.contentView = views;
            notification.flags = isPlaying() ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL;
            notification.icon = R.drawable.m_logo;
            notification.contentIntent = pendingIntent;
            startForeground(12, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeatSong) play(song);
        else
            nextSong();
    }

    public void setRepeatSong(boolean repeat) {
        repeatSong = repeat;
    }

    public void resumeSong() {
        mediaPlayer.start();
        initStatusBar();
        if (serviceCallback != null)
            serviceCallback.updateUI(song);
    }

    public int getPercentValue() {
        try {
            return (mediaPlayer.getCurrentPosition() * 100) / song.getDuration();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int progress) {
        mediaPlayer.seekTo((song.getDuration() * progress) / 100);
    }

    public ArrayList<Song> getPlayList() {
        return playList;
    }

    public boolean isRepeatSong() {
        return repeatSong;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public Song getCurrentSong() {
        return song;
    }

    public void setServiceCallback(MusicServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    public void setCompletionListener(IntentMusicActivity intentMusicActivity) {
        mediaPlayer.setOnCompletionListener(intentMusicActivity);
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getMusicPlayerService() {
            return MusicPlayerService.this;
        }
    }

    public void playSong(Song song, ArrayList<Song> songList) {
        this.playList = songList;
        this.song = song;
        play(song);
    }

    private void play(Song song) {

        if (mediaPlayer != null)
            mediaPlayer.release();
//            mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getSongUrl()));

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
        if (serviceCallback != null)
            serviceCallback.updateUI(song);
        initStatusBar();

        mMediaSessionCompat.setActive(true);
//        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
    }

    public void pauseSong() {
        mediaPlayer.pause();
        initStatusBar();
        if (serviceCallback != null)
            serviceCallback.updateUI(song);
//        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    public void nextSong() {
        int nextPosition = -1;
        for (int i = 0; i < playList.size() - 1; i++) {
            if (playList.get(i).getId() == song.getId()) {
                nextPosition = i + 1;
                break;
            }
        }
        if (nextPosition != -1) {
            song = playList.get(nextPosition);
            play(song);
        }
    }

    public void previousSong() {
        int prevPosition = -1;
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).getId() == song.getId()) {
                prevPosition = i - 1;
                break;
            }
        }
        if (prevPosition != -1) {
            song = playList.get(prevPosition);
            play(song);
        }
    }

    public interface MusicServiceCallback {
        void updateUI(Song song);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null || intent.getAction() == null)
            return START_STICKY;

        if (mediaPlayer == null) {
            stopSelf(startId);
            return START_STICKY;
        }

        if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
        } else if (intent.getAction().equals(PREV_ACTION)) {
            previousSong();
        } else if (intent.getAction().equals(PLAY_ACTION)) {
            if (isPlaying()) {
                pauseSong();
                initStatusBar();
            } else {
                resumeSong();
                initStatusBar();
            }
        } else if (intent.getAction().equals(NEXT_ACTION)) {
            nextSong();
        } else if (intent.getAction().equals(
                STOPFOREGROUND_ACTION)) {
            mediaPlayer.stop();
            if (serviceCallback != null)
                serviceCallback.updateUI(song);
            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equalsIgnoreCase("pause")) {
            pauseSong();
            initStatusBar();
        } else if (intent.getAction().equalsIgnoreCase("resume")) {
            resumeSong();
            initStatusBar();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mMediaSessionCompat.release();
        super.onDestroy();
    }
}
