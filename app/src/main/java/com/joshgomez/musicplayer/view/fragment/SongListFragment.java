package com.joshgomez.musicplayer.view.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.MediaProvider;
import com.joshgomez.musicplayer.generic.log.Loggy;
import com.joshgomez.musicplayer.model.Song;
import com.joshgomez.musicplayer.view.musicplayer.MusicPlayerActivity;

import com.joshgomez.musicplayer.view.adapter.SongAdapter;

import java.util.ArrayList;

public class SongListFragment extends Fragment {

    private SongAdapter adapter;
    private ArrayList<Song> songList;
    SongListInteractor songListInteractor;

    public static SongListFragment newInstance() {
        Bundle args = new Bundle();
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        ListView listView = view.findViewById(R.id.list_view);
        MediaProvider mediaProvider = new MediaProvider(getActivity());
        songList = mediaProvider.getMediaFileList();

        adapter = new SongAdapter(songList, getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view2, position, id) -> itemClick(position));

        songListInteractor = (SongListInteractor) getActivity();

        return view;
    }

    private void itemClick(int position) {
        Loggy.entryLog();
        MusicPlayerActivity.play(songList, (Song) adapter.getItem(position));
        Loggy.exitLog();
    }

    public interface SongListInteractor {
        void playSong(Song song, ArrayList<Song> songs);
    }
}
