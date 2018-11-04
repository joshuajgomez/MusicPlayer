package com.workshop.quest.musicplayer.service.musicmanager;

import com.workshop.quest.musicplayer.model.Song;

/**
 * Interface for receiving notification about song change
 */
public interface MusicPlayerCallback {

    /**
     * Notify listener when a song is played
     * @param song : Instance of {@link Song}
     */
    void notifySongChanged(Song song);

}
