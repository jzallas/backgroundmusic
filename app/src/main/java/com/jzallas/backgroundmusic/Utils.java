package com.jzallas.backgroundmusic;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class Utils {
    public static String cleanText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        return text.trim();
    }

    public static Uri rawResourceToUri(Context context, String resourceName) {
        String uriString = String.format(
                "android.resource://%s/raw/%s",
                context.getPackageName(),
                resourceName
        );
        return Uri.parse(uriString);
    }
}
