package com.workshop.quest.musicplayer.view.musicplayer;

import android.content.Intent;

import com.workshop.quest.musicplayer.model.Song;

public interface IMusicPlayerPresenter {

    void onClickEvent(int viewId);

    void onSeekProgress(int progress);

    void handleViewStart(Intent intent);

    void onStart();

    void onStop();

    void notifySongChanged(Song song);
}
