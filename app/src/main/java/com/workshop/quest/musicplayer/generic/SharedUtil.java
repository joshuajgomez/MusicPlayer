package com.workshop.quest.musicplayer.generic;

import android.content.Context;
import android.content.SharedPreferences;

import com.workshop.quest.musicplayer.base.BaseApp;
import com.workshop.quest.musicplayer.generic.log.Loggy;

public class SharedUtil {

    private static final String APP_DATA = "APP_DATA";

    private static final String PREF_REPEAT_SONG = "PREF_REPEAT_SONG";

    public static void setRepeatSongPref(boolean repeatSong) {
        Loggy.entryLog();
        Context context = BaseApp.getInstance().getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_REPEAT_SONG, repeatSong);
        editor.apply();
        Loggy.exitLog();
    }

    public static boolean getRepeatSongPref() {
        Loggy.entryLog();
        Context context = BaseApp.getInstance().getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_DATA, Context.MODE_PRIVATE);
        boolean repeatSong = sharedPreferences.getBoolean(PREF_REPEAT_SONG, false);
        Loggy.exitLog();
        return repeatSong;
    }

}
