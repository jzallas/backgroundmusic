package com.jzallas.backgroundmusic.player;

import com.jzallas.backgroundmusic.BasePresenter;
import com.jzallas.backgroundmusic.BaseView;

class PlayerContract {

    interface View extends BaseView<Presenter> {

        void showTitle(String title);

        void showArtist(String artist);

        void showAlbum(String album);

        void showAlbumArt(byte[] albumArt);

        void showPlaying();

        void showPaused();

        void showLoading(boolean isLoading);

        void showError(Throwable e);

        void setControlsEnabled(boolean enabled);

        void updateMediaProgress(int currentDuration, int totalDuration);
    }

    interface Presenter extends BasePresenter {

        void playMusic();

        void fastForward();

        void rewind();

        void seekTo(int currentDuration, int totalDuration);

        void updateProgress();

        void refresh();
    }
}
