package com.jzallas.backgroundmusic.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import com.jzallas.backgroundmusic.player.PlayerPresenter;

import java.io.IOException;

public class MediaManager {

    private MediaPlayer mediaPlayer;

    private Context context;

    private Uri uri;

    private OnPreparedListener onPreparedListener;

    private OnPlaybackCompleteListener onPlaybackCompleteListener;

    public MediaManager(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Begin async uri loading process.
     *
     * @param uri the {@link Uri} that this {@link MediaManager} is expected to playback
     * @throws IOException if a problem occurs while loading the {@link Uri}
     * @see OnPreparedListener
     * @see #setOnPreparedListener(OnPreparedListener)
     */
    public void loadUri(Uri uri) throws IOException {
        this.uri = uri;
        prepare();
    }

    /**
     * Seek to the time specified by the position
     *
     * @param position in millis
     */
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaPlayer.seekTo(position, MediaPlayer.SEEK_PREVIOUS_SYNC);
            } else {
                mediaPlayer.seekTo(position);
            }
        }
    }

    private void prepare() throws IOException {
        if (this.mediaPlayer != null) {
            return;
        }

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build();
                mediaPlayer.setAudioAttributes(attributes);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mediaPlayer.setDataSource(context, uri);

            if (onPreparedListener != null) {
                mediaPlayer.setOnPreparedListener(player -> onPreparedListener.onMediaPrepared());
            }

            if (onPlaybackCompleteListener != null) {
                mediaPlayer.setOnCompletionListener(player -> onPlaybackCompleteListener.onPlaybackComplete());
            }

            mediaPlayer.prepare();

            this.mediaPlayer = mediaPlayer;
        } catch (IOException e) {
            String message = String.format("A problem occurred while trying to prepare the %s.", MediaManager.class.getSimpleName());
            throw new IOException(message, e);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    /**
     * Starts or resumes the media playback.
     *
     * @throws IllegalStateException when attempting to play an unprepared {@link MediaManager}
     */
    public void play() {
        if (mediaPlayer == null) {
            String message = String.format("%s is not prepared. Did you remember to call %s?", MediaManager.class.getSimpleName(), "loadUri(...)");
            throw new IllegalStateException(message);
        } else {
            mediaPlayer.start();
        }
    }

    /**
     * Stops the media playback and releases any resources that were required for media playback.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Pauses the media playback.
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Get extra information about the media.
     *
     * @return
     */
    public MediaMetadata getMediaMetadata() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(context, uri);

        return new MediaMetadata(
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                retriever.getEmbeddedPicture()
        );
    }

    /**
     * Get the current progress of the currently playing content.
     *
     * @return {@link MediaProgress} if media is loaded, or <strong>null</strong> if no media is loaded.
     */
    public MediaProgress getProgress() {
        if (mediaPlayer == null) {
            return null;
        }
        return new MediaProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnPlaybackCompleteListener(PlayerPresenter onPlaybackCompleteListener) {
        this.onPlaybackCompleteListener = onPlaybackCompleteListener;
    }

    public interface OnPreparedListener {
        void onMediaPrepared();
    }

    public interface OnPlaybackCompleteListener {
        void onPlaybackComplete();
    }
}
