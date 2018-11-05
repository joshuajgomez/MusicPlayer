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
import java.util.List;

public class PlayListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;

    private SongAdapter mSongAdapter;

    private List<Song> mSongList;

    private Song mNowPlayingSong;

    private PlayListInteractor mPlayListInteractor;

    private TextView mSongCountTextView;

    private RecyclerView mRecyclerView;

    private static final String BUNDLE_SONG = "BUNDLE_SONG";

    private static final String BUNDLE_SONG_LIST = "BUNDLE_SONG_LIST";

    public static PlayListFragment newInstance(Song song, List<Song> songList) {
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_SONG, song);
        args.putParcelableArrayList(BUNDLE_SONG_LIST, (ArrayList) songList);
        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        mListView = view.findViewById(R.id.list_view);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mSongList = getArguments().getParcelableArrayList(BUNDLE_SONG_LIST);
        mNowPlayingSong = getArguments().getParcelable(BUNDLE_SONG);
        mPlayListInteractor = (PlayListInteractor) getActivity();
        mSongCountTextView = view.findViewById(R.id.songs_count);
            if (mNowPlayingSong != null)
                setCurrentSong(mNowPlayingSong);
        return view;
    }

    public void setCurrentSong(Song song) {
        if (mSongAdapter == null) {
            mRecyclerView.setAdapter(new ImageAdpater(getContext(), mSongList));
            mRecyclerView.smoothScrollToPosition(mSongList.size() - 1);
            mSongAdapter = new SongAdapter(mSongList, getContext());
            mListView.setAdapter(mSongAdapter);
            mListView.setOnItemClickListener(this);
            mSongCountTextView.setText(mSongList.size() + " song" + (mSongList.size() > 1 ? "s" : ""));
        }
        mSongAdapter.setCurrentSong(song);
        mListView.smoothScrollToPosition(mSongAdapter.getPosition(song));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPlayListInteractor != null)
            mPlayListInteractor.onPlaylistClick((Song) mSongAdapter.getItem(position), mSongList);
    }

    public void setSongList(List<Song> playList) {
        mSongList = playList;
        mSongAdapter.setSongs(playList);
        mListView.smoothScrollToPosition(0);
    }

    public interface PlayListInteractor {
        void onPlaylistClick(Song song, List<Song> list);
    }


}
