package com.jzallas.backgroundmusic;

import android.text.TextUtils;

public class Utils {
    public static String cleanText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        return text.trim();
    }
}
