package com.workshop.quest.musicplayer.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.MediaProvider;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.musicplayer.MusicPlayerActivity_old;
import com.workshop.quest.musicplayer.view.adapter.SongAdapter;

import java.util.ArrayList;

public class SongFilterListFragment extends Fragment {

    private TextView albumName;
    private TextView artistName;
    private TextView songCount;
    private ImageView imageView;
    private ListView listView;

    public static SongFilterListFragment newInstance(String key, String value) {
        SongFilterListFragment fragment = new SongFilterListFragment();
        Bundle args = new Bundle();
        args.putString(key, value);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        if (getArguments().containsKey("album")) {
            view = inflater.inflate(R.layout.fragment_song_filter_list, container, false);
            albumName = view.findViewById(R.id.album_title);
            songCount = view.findViewById(R.id.songs_count);
        } else {
            view = inflater.inflate(R.layout.fragment_artist_filter, container, false);
        }
        artistName = view.findViewById(R.id.artist_title);
        listView = view.findViewById(R.id.list_view);
        imageView = view.findViewById(R.id.cover_art_image);


        MediaProvider mediaProvider = new MediaProvider(getActivity());
        ArrayList<Song> songs = new ArrayList<Song>();
        if (getArguments().containsKey("album")) {
            String album = getArguments().getString("album");
            songs = getMatchingAlbums(mediaProvider.getMediaFileList(), album);
            albumName.setText(album);
            songCount.setText(songs.size() + " song" + (songs.size() > 1 ? "s" : ""));
            artistName.setText(getArtists(songs));
        } else if (getArguments().containsKey("artist")) {
            songs = getMatchingArtists(mediaProvider.getMediaFileList(), getArguments().getString("artist"));
            artistName.setText(songs.get(0).getArtist());
        }
        SongAdapter adapter = new SongAdapter(songs, getActivity());
        listView.setAdapter(adapter);

        ArrayList<Song> finalSongs = songs;
        listView.setOnItemClickListener((parent, view2, position, id) -> {
            Intent intent2 = new Intent(getActivity(), MusicPlayerActivity_old.class);
            intent2.putExtra("song", (Song) adapter.getItem(position));
            intent2.putExtra("song_list", finalSongs);
            startActivity(intent2);
        });

        imageView.setImageBitmap(songs.get(0).getCoverArt(getActivity()));
        return view;
    }

    private String getArtists(ArrayList<Song> songs) {
        if (songs.size() == 1)
            return songs.get(0).getArtist();
        else return "Various Artists";
    }

    private ArrayList<Song> getMatchingAlbums(ArrayList<Song> songList, String album) {
        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : songList)
            if (song.getAlbum().equalsIgnoreCase(album))
                songs.add(song);
        return songs;
    }

    private ArrayList<Song> getMatchingArtists(ArrayList<Song> songList, String artist) {
        ArrayList<Song> songs = new ArrayList<>();
        for (Song song : songList)
            if (song.getArtist().equalsIgnoreCase(artist))
                songs.add(song);
        return songs;
    }
}
