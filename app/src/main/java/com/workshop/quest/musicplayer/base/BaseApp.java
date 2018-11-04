package com.workshop.quest.musicplayer.base;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {

    private static BaseApp sBaseInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sBaseInstance = this;
    }

    public static BaseApp getInstance(){
        return sBaseInstance;
    }

    public Context getContext(){
        return getApplicationContext();
    }

}
