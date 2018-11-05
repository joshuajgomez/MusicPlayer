package com.workshop.quest.musicplayer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.generic.log.Loggy;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Loggy.entryLog();
        super.onCreate(savedInstanceState);
        setTheme(ResUtil.getTheme());
        Loggy.exitLog();
    }
}
