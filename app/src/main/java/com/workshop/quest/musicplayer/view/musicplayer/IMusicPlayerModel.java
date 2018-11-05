package com.workshop.quest.musicplayer.view.musicplayer;

import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.service.musicmanager.MusicPlayerCallback;

import java.util.List;

public interface IMusicPlayerModel extends MusicPlayerCallback {

    void initMusicPlayer(Song song, final List<Song> songList);

    Song getNowPlayingSong();

    int getCurrentPositionByPercentValue();

    int getCurrentPosition();

    void onSeekProgress(int progress);

    void onStop();

    void onStart();

    boolean isNowPlaying();

    void pauseSong();

    void resumeSong();

    void startNextSong();

    boolean isShuffleEnabled();

    List<Song> getSongList();

    void setShuffleEnabled(boolean shuffleSong);

    void startPreviousSong();

    void setPlaylist(List<Song> songList);
}
