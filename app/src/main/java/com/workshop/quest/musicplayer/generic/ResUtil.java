package com.workshop.quest.musicplayer.generic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.Log;

import com.workshop.quest.musicplayer.BuildConfig;
import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.base.BaseApp;
import com.workshop.quest.musicplayer.generic.log.Loggy;

public class ResUtil {

    public static String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static int getTheme() {
        Context context = BaseApp.getInstance().getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = preferences.getBoolean(Constants.PREF_DARK_THEME, true);
        Loggy.log(Log.INFO, "darkTheme= " + darkTheme);
        return darkTheme
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
