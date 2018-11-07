package com.joshgomez.musicplayer.generic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.MediaController;

import com.joshgomez.musicplayer.generic.MediaProvider;
import com.joshgomez.musicplayer.model.Playlist;
import com.joshgomez.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_db";
    private static final int DATABASE_VERSION = 1;
    private static final String FAVOURITES_TABLE = "fav_table";
    private static final String KEY_SONG_ID = "key_song_id";
    private static final String KEY_DATE_ADDED = "key_date_added";
    private Context context;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " +
                FAVOURITES_TABLE + "(" +
                KEY_SONG_ID + " INTEGER PRIMARY KEY," +
                KEY_DATE_ADDED + " INTEGER" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVOURITES_TABLE);
        onCreate(db);
    }

    public void addSongToFavourites(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SONG_ID, song.getId());
        values.put(KEY_DATE_ADDED, System.currentTimeMillis());

        // Inserting Row
        db.insert(FAVOURITES_TABLE, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Song> getFavouriteSongs(ArrayList<Song> songs) {
        ArrayList<Long> favouriteIdList = new ArrayList<Long>();

        String selectQuery = "SELECT * FROM " + FAVOURITES_TABLE
                + " ORDER BY " + KEY_DATE_ADDED;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isFirst()) {
                do {
                    int id = Integer.parseInt(cursor.getString(0));
                    favouriteIdList.add(Long.valueOf(id));
                } while (cursor.moveToNext());
            }
        }
        db.close();

        ArrayList<Song> favouriteSongs = new ArrayList<>();

        for (Song song : songs)
            if (favouriteIdList.contains(song.getId()))
                favouriteSongs.add(song);

        return favouriteSongs;
    }

    public void removeSongFromFavourites(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {String.valueOf(song.getId())};
        db.delete(FAVOURITES_TABLE, KEY_SONG_ID + " = ?", whereArgs);
        db.close();
        //db.close(); // Closing database connection
    }

    public boolean isFavourite(Song song) {
        String selectQuery = "SELECT * FROM " + FAVOURITES_TABLE
                + " ORDER BY " + KEY_DATE_ADDED;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isFirst()) {
                do {
                    long id = Long.parseLong(cursor.getString(0));
                    if (song.getId() == id) {
                        db.close();
                        return true;
                    }
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return false;
    }
}
