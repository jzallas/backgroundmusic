package com.jzallas.backgroundmusic.media;

/**
 * Immutable class that encapsulates media duration
 */
public class MediaProgress {
    private int totalDuration;
    private int currentDuration;

    public MediaProgress(int currentDuration, int totalDuration) {
        this.totalDuration = totalDuration;
        this.currentDuration = currentDuration;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }
}
