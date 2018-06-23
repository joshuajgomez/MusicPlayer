package com.workshop.quest.musicplayer.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.adapter.CustomLinearLayoutManager;
import com.workshop.quest.musicplayer.view.adapter.ImageAdpater;
import com.workshop.quest.musicplayer.view.adapter.SongAdapter;

import java.util.ArrayList;

public class PlayListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private static final String SONG_LIST_KEY = "SONG_LIST";
    private SongAdapter adapter;
    private PlayListInteractor interactor;
    private ArrayList<Song> songs;
    private TextView songCount;
    RecyclerView recyclerView;

    public static PlayListFragment newInstance() {
        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        listView = view.findViewById(R.id.list_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        songs = getArguments().getParcelableArrayList(SONG_LIST_KEY);
        interactor = (PlayListInteractor) getActivity();
        songCount = view.findViewById(R.id.songs_count);
        if (interactor != null) {
            Song currentSong = interactor.getCurrentSong();
            if (currentSong != null)
                setCurrentSong(currentSong);
        }
        return view;
    }

    public void setCurrentSong(Song song) {
        if (adapter == null) {
            songs = interactor.getCurrentSongList();
            recyclerView.setAdapter(new ImageAdpater(songs, getActivity()));
            recyclerView.smoothScrollToPosition(songs.size() - 1);
            adapter = new SongAdapter(songs, getContext());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            songCount.setText(songs.size() + " song" + (songs.size() > 1 ? "s" : ""));
        }
        adapter.setCurrentSong(song);
        listView.smoothScrollToPosition(adapter.getPosition(song));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (interactor != null)
            interactor.playSong((Song) adapter.getItem(position), songs);
    }

    public void setSongList(ArrayList<Song> playList) {
        songs = playList;
        adapter.setSongs(playList);
        listView.smoothScrollToPosition(0);
    }

    public interface PlayListInteractor {
        void playSong(Song song, ArrayList<Song> list);

        Song getCurrentSong();

        ArrayList<Song> getCurrentSongList();
    }


}
