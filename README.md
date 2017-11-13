#### Notes

This application does not launch from the standard launcher. It will only launch with an explicit intent for now.
The explicit intent should provide a uri pointing to the playback media. You can test this by doing the following:

```
Intent intent = new Intent();
intent.setComponent(new ComponentName("com.jzallas.backgroundmusic", "com.jzallas.backgroundmusic.player.PlayerActivity"));
intent.putExtra(
  "com.jzallas.backgroundmusic.player.PlayerActivity.media_uri_string",
  "android.resource://com.jzallas.backgroundmusic/raw/sample"
);
startActivity(intent);
```

or in adb:
```
adb shell am start \
-n "com.jzallas.backgroundmusic/com.jzallas.backgroundmusic.player.PlayerActivity" \
--es "com.jzallas.backgroundmusic.player.PlayerActivity.media_uri_string" \
"android.resource://com.jzallas.backgroundmusic/raw/sample"
```
