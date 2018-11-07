package com.joshgomez.musicplayer.service.musicmanager;

import com.joshgomez.musicplayer.model.Song;

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
