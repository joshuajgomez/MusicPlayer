package com.joshgomez.musicplayer.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.BitmapBuilder;
import com.joshgomez.musicplayer.generic.MediaProvider;
import com.joshgomez.musicplayer.generic.database.DatabaseManager;
import com.joshgomez.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;


public class FragmentPlaylist extends Fragment {

    private CardView favouritesCard;

    public FragmentPlaylist() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_playlist, container, false);
        MediaProvider mediaProvider = new MediaProvider(getActivity());
        ArrayList<Song> songs = mediaProvider.getMediaFileList();
        favouritesCard = view.findViewById(R.id.favourites_card);

        initLastAddedCard(view, songs);

        initFavouritesCard(view, songs);

        Bundle args = new Bundle();
        args.putParcelableArrayList("songs", songs);

        return view;
    }

    private void initFavouritesCard(View view, ArrayList<Song> songs) {
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        ArrayList<Song> favouriteSongs = databaseManager.getFavouriteSongs(songs);

        if (favouriteSongs.size() < 1) {
            favouritesCard.setVisibility(View.GONE);
            return;
        } else favouritesCard.setVisibility(View.VISIBLE);


        ImageView favPic1 = view.findViewById(R.id.pic1);
        ImageView favPic2 = view.findViewById(R.id.pic2);
        ImageView favPic3 = view.findViewById(R.id.pic3);
        ImageView favPic4 = view.findViewById(R.id.pic4);

        if (0 < favouriteSongs.size())
            new BitmapBuilder(favPic1, getActivity()).execute(favouriteSongs.get(0));
        if (1 < favouriteSongs.size())
            new BitmapBuilder(favPic2, getActivity()).execute(favouriteSongs.get(1));
        if (2 < favouriteSongs.size())
            new BitmapBuilder(favPic3, getActivity()).execute(favouriteSongs.get(2));
        if (3 < favouriteSongs.size())
            new BitmapBuilder(favPic4, getActivity()).execute(favouriteSongs.get(3));

        favouritesCard.setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, PlaylistDetailFragment.newInstance(favouriteSongs, "Favourites"))
                        .addToBackStack(null)
                        .commit()
        );
    }

    private void initLastAddedCard(View view, ArrayList<Song> songs) {

        ArrayList<Song> lastAddedSongs = (ArrayList<Song>) songs.clone();
        Collections.sort(lastAddedSongs, (song1, song2) ->
                Long.compare(song2.getDateAdded(), song1.getDateAdded())
        );

        CardView lastAddedCard = view.findViewById(R.id.last_added_card);
        ImageView pic1 = view.findViewById(R.id.last_pic1);
        ImageView pic2 = view.findViewById(R.id.last_pic2);
        ImageView pic3 = view.findViewById(R.id.last_pic3);
        ImageView pic4 = view.findViewById(R.id.last_pic4);

        if (0 < lastAddedSongs.size())
            new BitmapBuilder(pic1, getActivity()).execute(lastAddedSongs.get(0));
        if (1 < lastAddedSongs.size())
            new BitmapBuilder(pic2, getActivity()).execute(lastAddedSongs.get(1));
        if (2 < lastAddedSongs.size())
            new BitmapBuilder(pic3, getActivity()).execute(lastAddedSongs.get(2));
        if (3 < lastAddedSongs.size())
            new BitmapBuilder(pic4, getActivity()).execute(lastAddedSongs.get(3));

        lastAddedCard.setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, PlaylistDetailFragment.newInstance(lastAddedSongs, "Last added"))
                        .addToBackStack(null)
                        .commit()
        );
    }

}
