package com.workshop.quest.musicplayer.view.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.workshop.quest.musicplayer.base.BaseActivity;
import com.workshop.quest.musicplayer.base.BaseApp;
import com.workshop.quest.musicplayer.generic.Constants;
import com.workshop.quest.musicplayer.generic.log.Loggy;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.fragment.PlayListFragment;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends BaseActivity implements PlayListFragment.PlayListInteractor {

    private MusicPlayerView mMusicPlayerView;

    public static void play(List<Song> songList, Song song) {
        Loggy.entryLog();
        Loggy.log(Log.INFO, "play() called with: songList = [" + songList + "], song = ["
                + song + "]");
        Context context = BaseApp.getInstance().getContext();
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.putExtra(Constants.EXTRA_SONG, song);
        intent.putExtra(Constants.EXTRA_SONG_LIST, (ArrayList) songList);
        context.startActivity(intent);
        Loggy.exitLog();
    }

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

    @Override
    public void onPlaylistClick(Song song, List<Song> list) {
        Loggy.entryLog();
        mMusicPlayerView.onPlaylistClick(song, list);
        Loggy.exitLog();
    }

}
