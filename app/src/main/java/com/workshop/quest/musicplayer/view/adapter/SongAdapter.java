package com.workshop.quest.musicplayer.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.generic.BitmapBuilder;
import com.workshop.quest.musicplayer.generic.ResUtil;
import com.workshop.quest.musicplayer.model.Song;
import com.workshop.quest.musicplayer.view.activity.MusicPlayerActivity;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Song> songs;
    private ArrayList<Song> filterSongs;
    private Context context;
    private Song currentSong;
    private SongFilter songFilter;

    public SongAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.filterSongs = songs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filterSongs != null ? filterSongs.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return filterSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filterSongs.get(position).getId();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Song song = (Song) getItem(position);
        Log.println(Log.ASSERT, "getView", song.toString());

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.song_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        ImageView imageOverlay = convertView.findViewById(R.id.image_overlay);
        TextView track = convertView.findViewById(R.id.track);
        TextView artist = convertView.findViewById(R.id.artist);

        imageView.setTag(song.getId());
        BitmapBuilder bitmapBuilder = new BitmapBuilder(imageView, context);
        bitmapBuilder.execute(song);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        track.setText(song.getTrack());
        artist.setText(MusicPlayerActivity.getTime(song.getDuration()));
        artist.append(" \u2022 ");
        artist.append(song.getArtist());

        if (currentSong != null && song.getId() == currentSong.getId()) {
            track.setTypeface(null, Typeface.BOLD);
            track.setSelected(true);
            imageOverlay.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(context.getResources().getColor(ResUtil.getResId(R.attr.listBackground, context)));
        } else {
            track.setSelected(false);
            track.setTypeface(null, Typeface.NORMAL);
            imageOverlay.setVisibility(View.GONE);
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
        return convertView;
    }

    public void setCurrentSong(Song song) {
        this.currentSong = song;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return songFilter != null
                ? songFilter
                : (songFilter = new SongFilter());
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public int getPosition(Song song) {
        for (int i = 0; i < filterSongs.size(); i++)
            if (filterSongs.get(i).getId() == song.getId())
                return i;
        return 0;
    }

    public void setSongs(ArrayList<Song> playList) {
        filterSongs = playList;
        songs = playList;
        notifyDataSetChanged();
    }

    private class SongFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = String.valueOf(constraint);
            FilterResults filterResults = new FilterResults();
            ArrayList<Song> list = new ArrayList<Song>();

            for (Song song : songs)
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
            filterSongs = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }
    }
}
