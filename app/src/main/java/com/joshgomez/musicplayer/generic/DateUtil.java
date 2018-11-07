package com.joshgomez.musicplayer.generic;

import android.annotation.SuppressLint;

public class DateUtil {

    @SuppressLint("DefaultLocale")
    public static String getPrettyTime(int millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return minutes + ":" + String.format("%02d", seconds);
    }
}
