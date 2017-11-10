package com.jzallas.backgroundmusic;

import com.jzallas.backgroundmusic.log.AndroidLogger;
import com.jzallas.backgroundmusic.log.LoggerInterface;

public class ActivityUtils {

    private static LoggerInterface logger;

    public static LoggerInterface getLogger() {
        if (logger == null) {
            synchronized (ActivityUtils.class) {
                if (logger == null) {
                    logger = new AndroidLogger();
                }
            }
        }

        return logger;
    }
}
