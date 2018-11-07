package com.joshgomez.musicplayer.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.BitmapBuilder;
import com.joshgomez.musicplayer.generic.DateUtil;
import com.joshgomez.musicplayer.generic.ResUtil;
import com.joshgomez.musicplayer.model.Song;


import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends BaseAdapter implements Filterable {

    private List<Song> mSongs;
    private List<Song> mFilterSongs;
    private Context mContext;
    private Song mCurrentSong;
    private SongFilter mSongFilter;

    public SongAdapter(List<Song> songs, Context context) {
        this.mSongs = songs;
        this.mFilterSongs = songs;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mFilterSongs != null ? mFilterSongs.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mFilterSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFilterSongs.get(position).getId();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Song song = (Song) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.song_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        ImageView imageOverlay = convertView.findViewById(R.id.image_overlay);
        TextView track = convertView.findViewById(R.id.track);
        TextView artist = convertView.findViewById(R.id.artist);

        imageView.setTag(song.getId());
        BitmapBuilder bitmapBuilder = new BitmapBuilder(imageView, mContext);
        bitmapBuilder.execute(song);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        track.setText(song.getTrack());
        artist.setText(DateUtil.getPrettyTime(song.getDuration()));
        artist.append(" \u2022 ");
        artist.append(song.getArtist());

        if (mCurrentSong != null && song.getId() == mCurrentSong.getId()) {
            track.setTypeface(null, Typeface.BOLD);
            track.setSelected(true);
            imageOverlay.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(mContext.getResources().getColor(ResUtil.getResId(R
                    .attr.listBackground, mContext)));
        } else {
            track.setSelected(false);
            track.setTypeface(null, Typeface.NORMAL);
            imageOverlay.setVisibility(View.GONE);
            convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }
        return convertView;
    }

    public void setCurrentSong(Song song) {
        this.mCurrentSong = song;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mSongFilter != null
                ? mSongFilter
                : (mSongFilter = new SongFilter());
    }

    public List<Song> getSongs() {
        return mSongs;
    }

    public int getPosition(Song song) {
        for (int i = 0; i < mFilterSongs.size(); i++)
            if (mFilterSongs.get(i).getId() == song.getId())
                return i;
        return 0;
    }

    public void setSongs(List<Song> playList) {
        mFilterSongs = playList;
        mSongs = playList;
        notifyDataSetChanged();
    }

    private class SongFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = String.valueOf(constraint);
            FilterResults filterResults = new FilterResults();
            ArrayList<Song> list = new ArrayList<Song>();

            for (Song song : mSongs)
                if (song.getTrack().toLowerCase().contains(query.toLowerCase()) ||
                        song.getArtist().toLowerCase().contains(query.toLowerCase()) ||
                        song.getAlbum().toLowerCase().contains(query.toLowerCase()))
                    list.add(song);

            filterResults.values = list;
            filterResults.count = list.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilterSongs = (List<Song>) results.values;
            notifyDataSetChanged();
        }
    }
}
