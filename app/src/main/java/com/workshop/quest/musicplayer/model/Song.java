package com.workshop.quest.musicplayer.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.ResUtil;

public class Song implements Parcelable {


    private long id;
    private String album;
    private String track;
    private String fileName;
    private String artist;
    private int duration;
    private String songUrl;
    private String composer;
    private long dateAdded;


    public Song() {
    }


    protected Song(Parcel in) {
        id = in.readLong();
        album = in.readString();
        track = in.readString();
        fileName = in.readString();
        artist = in.readString();
        duration = in.readInt();
        songUrl = in.readString();
        composer = in.readString();
        dateAdded = in.readLong();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArtist() {
        return artist == null || artist.equalsIgnoreCase("<unknown>")
                ? "Unknown artist"
                : artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public Bitmap getCoverArt(Context context) {
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(getSongUrl());
        byte[] art = metaRetriver.getEmbeddedPicture();
        return art != null
                ? BitmapFactory.decodeByteArray(art, 0, art.length)
                : BitmapFactory.decodeResource(context.getResources(), ResUtil.getResId(R.attr.defaultAlbumArt, context));
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(album);
        dest.writeString(track);
        dest.writeString(fileName);
        dest.writeString(artist);
        dest.writeInt(duration);
        dest.writeString(songUrl);
        dest.writeString(composer);
        dest.writeLong(dateAdded);
    }
}
