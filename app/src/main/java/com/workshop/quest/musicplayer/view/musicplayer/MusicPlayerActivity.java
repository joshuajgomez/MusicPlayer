package com.workshop.quest.musicplayer.view.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.workshop.quest.musicplayer.generic.log.Loggy;

public class MusicPlayerActivity extends Activity {

    private MusicPlayerView mMusicPlayerView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        Loggy.entryLog();
        super.onCreate(savedInstanceState);
        mMusicPlayerView = new MusicPlayerView(this);
        Loggy.exitLog();
    }

    @Override
    protected void onStart() {
        Loggy.entryLog();
        super.onStart();
        if (mMusicPlayerView != null) {
            mMusicPlayerView.onStart();
        }
        Loggy.exitLog();
    }

    @Override
    protected void onStop() {
        Loggy.entryLog();
        mMusicPlayerView.onStop();
        super.onStop();
        Loggy.exitLog();
    }
}
