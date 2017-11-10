package com.jzallas.backgroundmusic.log;

import android.support.annotation.NonNull;

public interface LoggerInterface {

    void setTag(@NonNull String tag);

    void log(int logLevel, @NonNull String logMessage);

    void log(int logLevel, @NonNull String logMessage, @NonNull Throwable e);

    void log(int logLevel, @NonNull String logMessage, @NonNull Object... args);
}
