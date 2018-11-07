package com.joshgomez.musicplayer.generic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.model.Song;

public class BitmapBuilder extends AsyncTask<Song, Void, Bitmap> {

    private ImageView mImageView;
    private Context mContext;
    private Song mSong;

    public BitmapBuilder(ImageView imageView, Context context) {
        this.mImageView = imageView;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mImageView.setImageResource(ResUtil.getResId(R.attr.defaultAlbumArt, mContext));
    }

    @Override
    protected Bitmap doInBackground(Song... songs) {
        mSong = songs[0];
        return mSong.getCoverArt(mContext);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mImageView.getTag() != null && mSong.getId() == (long) mImageView.getTag())
            mImageView.setImageBitmap(bitmap);
    }
}
