package com.jzallas.backgroundmusic;

public interface BasePresenter {
    /**
     * Begin the task of loading and presenting the data
     */
    void start();

    /**
     * Unregister from any loading callbacks
     */
    void stop();

}
