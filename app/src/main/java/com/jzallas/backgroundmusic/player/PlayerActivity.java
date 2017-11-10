package com.jzallas.backgroundmusic.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jzallas.backgroundmusic.ActivityUtils;
import com.jzallas.backgroundmusic.R;
import com.jzallas.backgroundmusic.media.MediaManager;

import java.io.File;

public class PlayerActivity extends AppCompatActivity {

    private PlayerPresenter playerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // fab
        // TODO - Launch dialog to customize uri
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

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

        // TODO - replace with real uri mechanism
        Uri mediaUri = Uri.parse("android.resource://com.jzallas.backgroundmusic/" + R.raw.sample);

        playerPresenter = new PlayerPresenter(
                mediaUri,
                new MediaManager(this),
                playerFragment,
                ActivityUtils.getLogger()
        );
    }

}
