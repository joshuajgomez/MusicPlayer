package com.workshop.quest.musicplayer.service.musicmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.service.MusicPlayerService;
import com.workshop.quest.musicplayer.view.activity.MainActivity;

public class StatusBarPlayer implements MusicPlayerCallback {

    private static final String MAIN_ACTION = "musicplayer.action.main";
    private static final String INIT_ACTION = "musicplayer.action.init";
    private static final String PREV_ACTION = "musicplayer.action.prev";
    private static final String PLAY_ACTION = "musicplayer.action.play";
    private static final String NEXT_ACTION = "musicplayer.action.next";
    private static final String STARTFOREGROUND_ACTION = "musicplayer.action.startforeground";
    private static final String STOPFOREGROUND_ACTION = "musicplayer.action.stopforeground";

    private MusicPlayer mMusicPlayer;

    private Context mContext;

    StatusBarPlayer(Context context, final MusicPlayer musicPlayer) {
        mContext = context;
        Loggy.entryLog();
        mMusicPlayer = musicPlayer;
        Loggy.exitLog();
    }

    @Override
    public void notifySongChanged(final Song song) {
        Loggy.entryLog();
        Notification notification;
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.status_bar);
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(mContext, MusicPlayerService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(mContext, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(mContext, MusicPlayerService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(mContext, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(mContext, MusicPlayerService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(mContext, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(mContext, MusicPlayerService.class);
        closeIntent.setAction(STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(mContext, 0,
                closeIntent, 0);

        views.setImageViewResource(R.id.play, mMusicPlayer.isNowPlaying()
                ? R.mipmap.pause_white
                : R.mipmap.play_white);

        views.setOnClickPendingIntent(R.id.play, pplayIntent);
        views.setOnClickPendingIntent(R.id.next, pnextIntent);
        views.setOnClickPendingIntent(R.id.prev, ppreviousIntent);
        views.setOnClickPendingIntent(R.id.close, pcloseIntent);
        views.setImageViewBitmap(R.id.image, song.getCoverArt(mContext));
        views.setTextViewText(R.id.title, song.getTrack());
        views.setTextViewText(R.id.artist, song.getArtist());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(mContext).build();
            notification.contentView = views;
            notification.flags = mMusicPlayer.isNowPlaying()
                    ? Notification.FLAG_ONGOING_EVENT
                    : Notification.FLAG_AUTO_CANCEL;
            notification.icon = R.drawable.m_logo;
            notification.contentIntent = pendingIntent;
            mMusicPlayer.startStatusBarPlayer(12, notification);
        }
        Loggy.exitLog();
    }

    void onStatusBarEvent(final Intent intent) {
        Loggy.entryLog();
        switch (intent.getAction()) {
            case PREV_ACTION:
                mMusicPlayer.startPreviousSong();
                break;
            case PLAY_ACTION:
                if (mMusicPlayer.isNowPlaying()) {
                    mMusicPlayer.pauseSong();
                } else {
                    mMusicPlayer.resumeSong();
                }
                break;
            case NEXT_ACTION:
                mMusicPlayer.startNextSong();
                break;
            case STOPFOREGROUND_ACTION:
                mMusicPlayer.stopStatusBarPlayer();
                break;
            default:
                Loggy.log(Log.WARN, "Unknown action");
        }
        Loggy.exitLog();
    }
}
