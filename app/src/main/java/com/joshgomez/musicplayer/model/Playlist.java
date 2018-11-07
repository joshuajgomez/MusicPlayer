package com.joshgomez.musicplayer.model;

public class Playlist {

    private String name;
    private int id;
    private long dateAdded;

    public Playlist(String name, int id, long dateAdded) {
        this.name = name;
        this.id = id;
        this.dateAdded = dateAdded;
    }

    public Playlist() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
