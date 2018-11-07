package com.joshgomez.musicplayer.generic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.joshgomez.musicplayer.model.Song;

import java.util.ArrayList;

public class MediaProvider {

    private Context context;

    public MediaProvider(Context context) {
        this.context = context;
    }

    public ArrayList<Song> getMediaFileList() {

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(
                uri, // Uri
                null, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );
        return convertToArrayList(cursor);
    }

    public static ArrayList<Song> convertToArrayList(Cursor cursor) {
        ArrayList<Song> songList = new ArrayList<>();

        if (cursor == null) {
//            mResult.append("\n" +"Query failed, handle error.");
        } else if (!cursor.moveToFirst()) {
            // no media on the device
//            mResult.append("\n" +"Nno music found on the sd card.");
        } else {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songUrl = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int fileName = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int dateAdded = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
            int composer = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
            // Loop through the musics
            do {
                Song song = new Song();
                song.setId(cursor.getLong(id));
                song.setTrack(cursor.getString(title));
                song.setAlbum(cursor.getString(album));
                song.setArtist(cursor.getString(artist));
                song.setDuration(cursor.getInt(duration));
                song.setSongUrl(cursor.getString(songUrl));
                song.setFileName(cursor.getString(fileName));
                song.setDateAdded(cursor.getLong(dateAdded));
                song.setComposer(cursor.getString(composer));

                // Process current music here
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    public ArrayList<String> getAlbumList() {
        ArrayList<Song> songs = getMediaFileList();
        ArrayList<String> albumList = new ArrayList<String>();
        for (Song song : songs)
            if (!albumList.contains(song.getAlbum()))
                albumList.add(song.getAlbum());
        return albumList;
    }

    public ArrayList<String> getArtistList() {
        ArrayList<Song> songs = getMediaFileList();
        ArrayList<String> artistList = new ArrayList<String>();
        for (Song song : songs)
            if (!artistList.contains(song.getArtist()))
                artistList.add(song.getArtist());
        return artistList;
    }
}
