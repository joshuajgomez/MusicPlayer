package com.workshop.quest.musicplayer.view.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.MediaProvider;
import com.workshop.quest.musicplayer.view.adapter.StringAdapter;

public class AlbumListFragment extends ListFragment {

    private static final String TYPE_KEY = "type_key";
    private String type = "type";

    public static AlbumListFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE_KEY, type);
        AlbumListFragment fragment = new AlbumListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(TYPE_KEY);
        MediaProvider mediaProvider = new MediaProvider(getActivity());
        if (type.equalsIgnoreCase("album"))
            setListAdapter(new StringAdapter(mediaProvider.getAlbumList(), getActivity(), R.attr.albumIcon));
        else if (type.equalsIgnoreCase("artist"))
            setListAdapter(new StringAdapter(mediaProvider.getArtistList(), getActivity(), R.attr.artistIcon));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getListView().setDividerHeight(0);
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, SongFilterListFragment.newInstance(type, String.valueOf(getListAdapter().getItem(position))))
                .addToBackStack(null)
                .commit();
    }
}
