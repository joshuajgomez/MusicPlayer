package com.workshop.quest.musicplayer.view.musicplayer;

import com.workshop.quest.musicplayer.model.Song;

public interface IMusicPlayerView {

    void updateNowPlayingSong(Song song);

    void updateProgress(String time, int progressPercent);

}
