package com.joshgomez.musicplayer.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.Constants;
import com.joshgomez.musicplayer.generic.database.DatabaseManager;
import com.joshgomez.musicplayer.model.Song;

import com.joshgomez.musicplayer.view.adapter.SongAdapter;
import com.joshgomez.musicplayer.view.musicplayer.MusicPlayerActivity;

import java.util.ArrayList;

public class PlaylistDetailFragment extends Fragment {

    private TextView playListName;
    private TextView songsCount;
    private ListView listView;
    private ImageView imageView;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private SongAdapter adapter;

    public static PlaylistDetailFragment newInstance(ArrayList<Song> list, String playlistName) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("songs", list);
        args.putString("playlist_name", playlistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_detail_new, container, false);
        playListName = view.findViewById(R.id.play_list_title);
        songsCount = view.findViewById(R.id.songs_count);
        listView = view.findViewById(R.id.list_view);
        imageView = view.findViewById(R.id.cover_art_image);

        if (!getArguments().containsKey("songs")) return view;

        String playlist_name = getArguments().getString("playlist_name");
        playListName.setText(playlist_name);
        songs = getArguments().getParcelableArrayList("songs");
        if (playlist_name.equalsIgnoreCase("Favourites")) {
            DatabaseManager databaseManager = new DatabaseManager(getActivity());
            songs = databaseManager.getFavouriteSongs(songs);
        }
        songsCount.setText(songs.size() + " songs");

        adapter = new SongAdapter(songs, getActivity());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view2, position, id) -> itemClick(position));

        imageView.setImageBitmap(songs.get(0).getCoverArt(getActivity()));
        return view;
    }

    private void itemClick(int position) {
        MusicPlayerActivity.play(songs, (Song) adapter.getItem(position));
    }

}
