package com.jzallas.backgroundmusic.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jzallas.backgroundmusic.ActivityUtils;
import com.jzallas.backgroundmusic.R;
import com.jzallas.backgroundmusic.media.MediaManager;
import com.jzallas.backgroundmusic.schedulers.SchedulerProvider;

public class PlayerActivity extends AppCompatActivity {

    private static final String INTENT_EXTRA_MEDIA_URI = PlayerActivity.class.getName() + ".media_uri";
    private static final String INTENT_EXTRA_MEDIA_URI_STRING = PlayerActivity.class.getName() + ".media_uri_string";

    private PlayerPresenter playerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // fragment
        PlayerFragment playerFragment =
                (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if (playerFragment == null) {
            // Create the fragment
            playerFragment = PlayerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_placeholder, playerFragment, PlayerFragment.TAG)
                    .commit();
        }

        String mediaUri = getMediaUri(getIntent());

        playerPresenter = new PlayerPresenter(
                mediaUri,
                new MediaManager(this),
                playerFragment,
                ActivityUtils.getLogger(),
                SchedulerProvider.getInstance()
        );
    }

    private String getMediaUri(Intent intent) {
        Uri uri = intent.getParcelableExtra(INTENT_EXTRA_MEDIA_URI);

        return uri == null ?
                intent.getStringExtra(INTENT_EXTRA_MEDIA_URI_STRING) :
                uri.toString();
    }

}
