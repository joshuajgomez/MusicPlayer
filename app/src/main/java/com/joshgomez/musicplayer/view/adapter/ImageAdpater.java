package com.joshgomez.musicplayer.view.adapter;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.generic.BitmapBuilder;
import com.joshgomez.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ImageAdpater extends RecyclerView.Adapter<ImageAdpater.ImageViewHolder> {

    private List<Song> songs;
    private Context context;

    public ImageAdpater(Context context, List<Song> songs) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.imageView.setTag(song.getId());
        BitmapBuilder bitmapBuilder = new BitmapBuilder(holder.imageView, context);
        bitmapBuilder.execute(song);
    }

    @Override
    public int getItemCount() {
        return songs != null
                ? songs.size()
                : 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }
}