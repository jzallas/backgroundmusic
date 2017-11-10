package com.jzallas.backgroundmusic;

import android.os.Handler;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static Timer startTimer(final Runnable runnable, int interval) {
        final Handler handler = new Handler();
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 0, interval);

        return timer;
    }

    /**
     * Formats millis into human readable seconds ie 26300 -> 04:23
     *
     * @param millis
     * @return
     */
    public static String formatToMinuteSeconds(long millis) {
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds);
    }
}
