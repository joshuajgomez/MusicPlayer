package com.joshgomez.musicplayer.view.musicplayer;

import android.content.Intent;

import com.joshgomez.musicplayer.model.Song;

import java.util.List;

public interface IMusicPlayerPresenter {

    void onClickEvent(int event);

    void onSeekProgress(int progress);

//    void handleViewStart(Intent intent);

    void onBindComplete();

    void onStart();

    void onStop();

    void notifySongChanged(Song song);

    void onPlaylistClick(Song song, List<Song> list);
}
