package com.joshgomez.musicplayer.view.musicplayer;

import com.joshgomez.musicplayer.model.Song;

import java.util.List;

public interface IMusicPlayerView {

    void updateNowPlayingSong(Song song, boolean isNowPlaying, boolean isFavourite);

    void updateProgress(String time, int progressPercent);

    void updateViewImage(int viewId, int imageRes);

    void updatePlayList(List<Song> playList);

    void stopActivity();

    void showPlaylist(Song nowPlayingSong, List<Song> songList);

}
