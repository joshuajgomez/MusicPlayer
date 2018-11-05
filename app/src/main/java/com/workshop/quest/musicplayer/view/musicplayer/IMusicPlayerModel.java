package com.workshop.quest.musicplayer.view.musicplayer;

import com.workshop.quest.musicplayer.model.Song;

import java.util.List;

public interface IMusicPlayerModel {

    void initMusicPlayer(Song song, final List<Song> songList);

    Song getNowPlayingSong();

    int getCurrentPositionByPercentValue();

    int getCurrentPosition();

    void onSeekProgress(int progress);

    void onStop();

    void onStart();
}
