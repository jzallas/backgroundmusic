package com.jzallas.backgroundmusic.player;

import android.util.Log;

import com.jzallas.backgroundmusic.Utils;
import com.jzallas.backgroundmusic.log.LoggerInterface;
import com.jzallas.backgroundmusic.media.MediaManager;
import com.jzallas.backgroundmusic.media.MediaMetadata;
import com.jzallas.backgroundmusic.media.MediaProgress;
import com.jzallas.backgroundmusic.schedulers.BaseSchedulerProvider;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;

public class PlayerPresenter implements PlayerContract.Presenter,
        MediaManager.OnPlaybackCompleteListener {

    private static final int QUICK_MEDIA_ADJUSTMENT_AMOUNT = 30000; // +-30

    private final LoggerInterface logger;
    private MediaManager mediaManager;
    private PlayerContract.View view;

    private CompositeDisposable compositeDisposable;

    private BaseSchedulerProvider schedulers;

    PlayerPresenter(String mediaUri,
                    MediaManager mediaManager,
                    PlayerContract.View view,
                    LoggerInterface logger,
                    BaseSchedulerProvider schedulerProvider) {
        view.setPresenter(this);
        this.mediaManager = mediaManager;
        this.mediaManager.setOnPlaybackCompleteListener(this);
        this.mediaManager.attachUri(mediaUri);
        this.view = view;

        this.logger = logger;
        this.logger.setTag(PlayerPresenter.class.getSimpleName());

        this.compositeDisposable = new CompositeDisposable();
        this.schedulers = schedulerProvider;
    }

    @Override
    public void start() {
        view.setControlsEnabled(false);
        view.showLoading(true);

        Completable.create(e -> {
            try {
                mediaManager.prepare();
                e.onComplete();
            } catch (Exception exception) {
                e.onError(exception);
            }
        })
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe(compositeDisposable::add)
                .subscribe(
                        this::onMediaPrepared,
                        e -> {
                            logger.log(Log.ERROR, "A problem occurred while loading the Uri", e);
                            view.showError(e);
                        });

        Maybe.create((MaybeOnSubscribe<MediaMetadata>) e -> {
            try {
                MediaMetadata metadata = mediaManager.getMediaMetadata();
                if (metadata != null) {
                    e.onSuccess(metadata);
                }
                e.onComplete();
            } catch (Exception exception) {
                e.onError(exception);
            }
        })
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe(compositeDisposable::add)
                .subscribe(
                        this::onMediaMetadataAvailable,
                        e -> {
                            logger.log(Log.ERROR, "A problem occurred when loading the metadata", e);
                            view.showError(e);
                        });

    }

    private void onMediaPrepared() {
        view.showLoading(false);
        view.setControlsEnabled(true);

        updateProgress();
    }

    private void onMediaMetadataAvailable(MediaMetadata metadata) {
        // text
        view.showTitle(Utils.cleanText(metadata.getTitle()));
        view.showAlbum(Utils.cleanText(metadata.getAlbum()));
        view.showArtist(Utils.cleanText(metadata.getAuthor()));

        // image
        final byte[] albumArt = metadata.getAlbumArt();
        if (albumArt != null && albumArt.length > 0) {
            view.showAlbumArt(albumArt);
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

        compositeDisposable.clear();
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
    public void refresh() {
        stop();
        start();
    }

    @Override
    public void onPlaybackComplete() {
        // set the ui back to beginning
        mediaManager.pause();
        view.showPaused();
        seekTo(0);
    }
}
