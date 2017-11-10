package com.jzallas.backgroundmusic.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jzallas.backgroundmusic.BuildConfig;

public class AndroidLogger implements LoggerInterface {

    private String tag = AndroidLogger.class.getSimpleName();

    @Override
    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    private boolean isLoggingEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void log(int logLevel, @NonNull String logMessage) {
        this.delegateLog(logLevel, logMessage, null);
    }

    @Override
    public void log(int logLevel, @NonNull String logMessage, @NonNull Throwable e) {
        delegateLog(logLevel, logMessage, e);
    }

    @Override
    public void log(int logLevel, @NonNull String logMessage, @NonNull Object... args) {
        delegateLog(logLevel, String.format(logMessage, args), null);
    }

    private void delegateLog(int level, String message, @Nullable Throwable e) {
        if (!isLoggingEnabled()) {
            return;
        }

        switch (level) {
            case Log.ASSERT:
            case Log.VERBOSE:
                if (e != null) Log.v(tag, message, e);
                else Log.v(tag, message);
                break;
            case Log.DEBUG:
                if (e != null) Log.d(tag, message, e);
                else Log.d(tag, message);
                break;
            case Log.INFO:
                if (e != null) Log.i(tag, message, e);
                else Log.i(tag, message);
                break;
            case Log.WARN:
                if (e != null) Log.w(tag, message, e);
                else Log.w(tag, message);
                break;
            case Log.ERROR:
                if (e != null) Log.e(tag, message, e);
                else Log.e(tag, message);
                break;
        }
    }
}
