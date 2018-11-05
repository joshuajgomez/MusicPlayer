package com.workshop.quest.musicplayer.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.musicplayer.MusicPlayerActivity_old;


public class SongInfoFragment extends Fragment {

    private Song song;

    public static SongInfoFragment newInstance(Song song) {
        SongInfoFragment fragment = new SongInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("song", song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            song = getArguments().getParcelable("song");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_info, container, false);
        TextView title = view.findViewById(R.id.title);
        TextView artist = view.findViewById(R.id.artist);
        TextView duration = view.findViewById(R.id.duration);
        TextView album = view.findViewById(R.id.album);
        TextView composer = view.findViewById(R.id.composer);
        TextView fileLocation = view.findViewById(R.id.file_location);
        ImageView albumArt = view.findViewById(R.id.image);
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        title.setText(song.getTrack());
        artist.setText(song.getArtist());
        duration.setText(MusicPlayerActivity_old.getTime(song.getDuration()));
        album.setText(song.getAlbum());
        composer.setText(song.getComposer());
        fileLocation.setText(song.getSongUrl());
        albumArt.setImageBitmap(song.getCoverArt(getActivity()));
        return view;
    }

}
