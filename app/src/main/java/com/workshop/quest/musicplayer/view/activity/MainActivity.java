package com.workshop.quest.musicplayer.view.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.workshop.quest.musicplayer.service.MusicPlayerService;
import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.base.BaseActivity;
import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.fragment.AlbumListFragment;
import com.workshop.quest.musicplayer.view.fragment.FragmentPlaylist;
import com.workshop.quest.musicplayer.view.fragment.SearchFragment;
import com.workshop.quest.musicplayer.view.fragment.SongListFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements
        MusicPlayerService.MusicServiceCallback,
        SongListFragment.SongListInteractor {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private LinearLayout noPermissionView;
    private TabLayout tabLayout;
    MusicPlayerService musicPlayerService;
    private RelativeLayout nowPlayingBar;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) service;
            musicPlayerService = musicBinder.getMusicPlayerService();
            musicPlayerService.setServiceCallback(MainActivity.this);

            if (musicPlayerService != null && musicPlayerService.getCurrentSong() != null) {
                updateUI(musicPlayerService.getCurrentSong());
                nowPlayingBar.setVisibility(View.VISIBLE);
            } else nowPlayingBar.setVisibility(View.GONE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.container);
        noPermissionView = findViewById(R.id.no_permission);
        tabLayout = findViewById(R.id.tab_layout);
        nowPlayingBar = findViewById(R.id.now_playing);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED)
            initUI();
        else {
            String[] permStrings = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this, permStrings, 12);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicPlayerService != null && musicPlayerService.getCurrentSong() != null) {
            updateUI(musicPlayerService.getCurrentSong());
            nowPlayingBar.setVisibility(View.VISIBLE);
        } else nowPlayingBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.println(Log.ASSERT, "PermissionsResult", arrayToString(grantResults));
        if (requestCode == 12 &&
                checkPermission(grantResults)) {
            initUI();
        } else {
            mViewPager.setVisibility(View.GONE);
            noPermissionView.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkPermission(int[] grantResults) {
        for (int grantResult : grantResults)
            if (grantResult == -1) return false;
        return true;
    }

    private String arrayToString(int[] grantResults) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int grantResult : grantResults)
            stringBuilder.append(grantResult + "\t\t");
        return stringBuilder.toString();
    }

    private void initUI() {
        Log.println(Log.ASSERT, "main initUI", "called");
        noPermissionView.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);

        tabLayout.setupWithViewPager(mViewPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getFragments());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        startService(new Intent(this, MusicPlayerService.class));
        tabLayout.getTabAt(0).setIcon(ResUtil.getResId(R.attr.songIcon, this));
        tabLayout.getTabAt(1).setIcon(ResUtil.getResId(R.attr.albumIcon, this));
        tabLayout.getTabAt(2).setIcon(ResUtil.getResId(R.attr.artistIcon, this));
        tabLayout.getTabAt(3).setIcon(ResUtil.getResId(R.attr.playListTabIcon, this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, MusicPlayerService.class);
        bindService(service, musicServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(musicServiceConnection);
            isBound = false;
        }
    }

    @Override
    public void updateUI(Song song) {
        TextView track = findViewById(R.id.track_name);
        TextView artist = findViewById(R.id.artist_name);
        ImageView imageView = findViewById(R.id.image_name);
        ImageView playButton = findViewById(R.id.play);

        track.setText(song.getTrack());
        artist.setText(song.getArtist());
        track.setSelected(true);
        artist.setSelected(true);
        imageView.setImageBitmap(song.getCoverArt(this));
        playButton.setImageResource(musicPlayerService.isPlaying()
                ? ResUtil.getResId(R.attr.pauseIconLite, this)
                : ResUtil.getResId(R.attr.playIconLite, this));
        playButton.setOnClickListener(v -> {
            if (musicPlayerService.isPlaying()) {
                musicPlayerService.pauseSong();
                playButton.setImageResource(ResUtil.getResId(R.attr.playIconLite, this));
            } else {
                musicPlayerService.resumeSong();
                playButton.setImageResource(ResUtil.getResId(R.attr.pauseIconLite, this));
            }
        });
    }

    public void playSong(View view) {
        startActivity(new Intent(this, MusicPlayerActivity.class));
    }

    @Override
    public void playSong(Song song, ArrayList<Song> songs) {
        if (musicPlayerService != null) {
            musicPlayerService.playSong(song, songs);
            updateUI(musicPlayerService.getCurrentSong());
            nowPlayingBar.setVisibility(View.VISIBLE);
        }
    }

    public void searchButton(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, new SearchFragment())
                .addToBackStack(null)
                .commit();
    }

    public void settingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void askPermission(View view) {
        String[] permStrings = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_PHONE_STATE};
        ActivityCompat.requestPermissions(this, permStrings, 12);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }


    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(SongListFragment.newInstance());
        list.add(AlbumListFragment.newInstance("album"));
        list.add(AlbumListFragment.newInstance("artist"));
        list.add(new FragmentPlaylist());
        return list;
    }
}
