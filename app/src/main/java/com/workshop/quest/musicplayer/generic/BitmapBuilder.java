package com.workshop.quest.musicplayer.generic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.workshop.quest.musicplayer.R;
import com.workshop.quest.musicplayer.model.Song;

public class BitmapBuilder extends AsyncTask<Song, Void, Bitmap> {

    private ImageView imageView;
    private Context context;
    private Song song;

    public BitmapBuilder(ImageView imageView, Context context) {
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        imageView.setImageResource(ResUtil.getResId(R.attr.defaultAlbumArt, context));
    }

    @Override
    protected Bitmap doInBackground(Song... songs) {
        song = songs[0];
        return song.getCoverArt(context);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (imageView.getTag() != null && song.getId() == (long) imageView.getTag())
            imageView.setImageBitmap(bitmap);
    }
}
