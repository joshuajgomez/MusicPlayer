package com.workshop.quest.musicplayer.generic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.Log;

import com.workshop.quest.musicplayer.BuildConfig;
import com.workshop.quest.musicplayer.R;

public class ResUtil {

    public static String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static int getTheme(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean dark_theme = preferences.getBoolean("dark_theme", true);
        Log.println(Log.ASSERT, "getTheme", dark_theme + "");
        return dark_theme
                ? R.style.AppThemeDark
                : R.style.AppThemeLight;
    }

    @SuppressLint("ResourceAsColor")
    public static int getResId(int resId, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{resId});
        int resId2 = a.getResourceId(0, R.color.black);
        return resId2;
    }

}
