package com.joshgomez.musicplayer.view.activity;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;

import com.joshgomez.musicplayer.R;
import com.joshgomez.musicplayer.base.BaseActivity;
import com.joshgomez.musicplayer.view.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }
}
