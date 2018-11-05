package com.workshop.quest.musicplayer.service.musicmanager;

import android.app.Activity;
import android.os.IInterface;

import com.workshop.quest.musicplayer.model.Song;

import java.util.List;

public interface IMusicPlayer {

    /**
     * Inits {@link MusicPlayer} with a song and a playlist
     * @param song : Instance of {@link Song}
     * @param playlist
     */
    void initMusicPlayer(Song song, List<Song> playlist);

    void pauseSong();

    void resumeSong();

    boolean isNowPlaying();

    void startNextSong();

    void startPreviousSong();

    int getCurrentPositionByPercentValue();

    int getCurrentPosition();

    void seekTo(int progress);

    List<Song> getPlayList();

    Song getNowPlayingSong();

    boolean registerCallback(MusicPlayerCallback musicPlayerCallback);

    boolean unRegisterCallback(MusicPlayerCallback musicPlayerCallback);

    void setCompletionListener(Activity activity);

    boolean isShuffleEnabled();

    void setShuffleEnabled(boolean shuffleSong);
}
