package com.jzallas.backgroundmusic.player;

import android.net.Uri;
import android.util.Log;

import com.jzallas.backgroundmusic.Utils;
import com.jzallas.backgroundmusic.log.LoggerInterface;
import com.jzallas.backgroundmusic.media.MediaManager;
import com.jzallas.backgroundmusic.media.MediaMetadata;
import com.jzallas.backgroundmusic.media.MediaProgress;

public class PlayerPresenter implements PlayerContract.Presenter,
        MediaManager.OnPreparedListener,
        MediaManager.OnPlaybackCompleteListener {

    private static final int QUICK_MEDIA_ADJUSTMENT_AMOUNT = 30000; // +-30

    private final LoggerInterface logger;
    private String mediaUri;
    private MediaManager mediaManager;
    private PlayerContract.View view;

    PlayerPresenter(String mediaUri, MediaManager mediaManager, PlayerContract.View view, LoggerInterface logger) {
        view.setPresenter(this);
        this.mediaUri = mediaUri;
        this.mediaManager = mediaManager;
        this.view = view;

        this.logger = logger;
        this.logger.setTag(PlayerPresenter.class.getSimpleName());
    }

    @Override
    public void start() {
        view.setControlsEnabled(false);

        mediaManager.setOnPreparedListener(this);
        mediaManager.setOnPlaybackCompleteListener(this);

        try {
            mediaManager.loadUri(Uri.parse(mediaUri));
        } catch (Exception e) {
            logger.log(Log.ERROR, "A problem occurred while loading the Uri", e);
            view.showError(e);
        }
    }

    @Override
    public void onMediaPrepared() {
        view.setControlsEnabled(true);

        updateProgress();

        MediaMetadata mediaMetadata = mediaManager.getMediaMetadata();

        if (mediaMetadata != null) {
            // text
            view.showTitle(Utils.cleanText(mediaMetadata.getTitle()));
            view.showAlbum(Utils.cleanText(mediaMetadata.getAlbum()));
            view.showArtist(Utils.cleanText(mediaMetadata.getAuthor()));

            // image
            view.showAlbumArt(mediaMetadata.getAlbumArt());
        }
    }

    @Override
    public void playMusic() {
        if (mediaManager.isPlaying()) {
            mediaManager.pause();
            view.showPaused();
        } else {
            mediaManager.play();
            view.showPlaying();
        }
    }

    @Override
    public void fastForward() {
        MediaProgress progress = mediaManager.getProgress();
        if (progress != null) {
            // either fast foward to the end or +n seconds
            int millis = Math.min(
                    progress.getCurrentDuration() + QUICK_MEDIA_ADJUSTMENT_AMOUNT,
                    progress.getTotalDuration()
            );
            seekTo(millis);
        }
    }

    @Override
    public void rewind() {
        MediaProgress progress = mediaManager.getProgress();
        if (progress != null) {
            // either rewind to the beginning or -n seconds
            int millis = Math.max(
                    progress.getCurrentDuration() - QUICK_MEDIA_ADJUSTMENT_AMOUNT,
                    0
            );
            seekTo(millis);
        }
    }

    @Override
    public void stop() {
        mediaManager.stop();
        view.showPaused();
        view.updateMediaProgress(0, 1);
        view.setControlsEnabled(false);
    }

    @Override
    public void seekTo(int currentDuration, int totalDuration) {
        if (totalDuration == 0) {
            return;
        }
        MediaProgress progress = mediaManager.getProgress();
        int millis = (progress.getTotalDuration() * currentDuration) / totalDuration;
        seekTo(millis);
    }

    private void seekTo(int millis) {
        mediaManager.seekTo(millis);
        updateProgress();
    }

    @Override
    public void updateProgress() {
        MediaProgress progress = mediaManager.getProgress();
        if (progress != null) {
            view.updateMediaProgress(progress.getCurrentDuration(), progress.getTotalDuration());
        }
    }

    @Override
    public void onPlaybackComplete() {
        // just reset
        stop();
        start();
    }
}
