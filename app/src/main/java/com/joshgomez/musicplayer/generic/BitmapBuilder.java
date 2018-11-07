package com.joshgomez.musicplayer.generic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.model.Song;

/**
 * Builds a {@link Bitmap} from song's album art and sets it to the given {@link ImageView} from
 * a worker thread
 */
public class BitmapBuilder extends AsyncTask<Song, Void, Bitmap> {

    /**
     * {@link ImageView} to set the {@link Bitmap} to.
     */
    private ImageView mImageView;
    /**
     * Instance of {@link Context}
     */
    private Context mContext;
    /**
     * {@link Song} to take the album art from
     */
    private Song mSong;

    /**
     * Constructor for {@link BitmapBuilder}
     * @param imageView Insanace of {@link ImageView}
     * @param context Instance of {@link Context}
     */
    public BitmapBuilder(ImageView imageView, Context context) {
        this.mImageView = imageView;
        this.mContext = context;
    }

    /**
     * Invoked on main thread before execution starts on worker thread.
     * Sets a default image to {@link ImageView}
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mImageView.setImageResource(ResUtil.getResId(R.attr.defaultAlbumArt, mContext));
    }

    /**
     * Invoked on worker thread. Builds bitmap from song object and returns it.
     * @param songs Array of songs given at execution.
     * @return Bitmap object of the album art
     */
    @Override
    protected Bitmap doInBackground(Song... songs) {
        mSong = songs[0];
        return mSong.getCoverArt(mContext);
    }

    /**
     * Invoked on main thread after execution of worker thread. Gets the {@link Bitmap} as an
     * argument. Checks the object of {@link Song} and {@link ImageView} to be on the same thread
     * and sets the {@link Bitmap} to the {@link ImageView}
     * @param bitmap {@link Bitmap} built from worker thread.
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mImageView.getTag() != null && mSong.getId() == (long) mImageView.getTag())
            mImageView.setImageBitmap(bitmap);
    }
}
