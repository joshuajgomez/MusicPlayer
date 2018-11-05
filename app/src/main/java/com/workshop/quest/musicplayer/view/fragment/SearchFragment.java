package com.workshop.quest.musicplayer.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.MediaProvider;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.musicplayer.MusicPlayerActivity_old;
import com.workshop.quest.musicplayer.view.adapter.SongAdapter;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements TextWatcher {

    private SongAdapter songAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchText = (EditText) view.findViewById(R.id.searchText);
        MediaProvider mediaProvider = new MediaProvider(getActivity());
        songAdapter = new SongAdapter(mediaProvider.getMediaFileList(), getActivity());
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayerActivity_old.play(getActivity(), (ArrayList<Song>) songAdapter.getSongs(), (Song) songAdapter.getItem(position));
            }
        });
        searchText.addTextChangedListener(this);
        listView.setAdapter(songAdapter);
        return view;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        songAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
