package com.jzallas.backgroundmusic.media;

/**
 * Immutable class that encapsulates metadata describing media
 */
public class MediaMetadata {
    private String title;
    private String album;
    private String author;
    private byte[] albumArt;

    public MediaMetadata(String title, String album, String author, byte[] albumArt) {
        this.title = title;
        this.album = album;
        this.author = author;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getAlbumArt() {
        return albumArt;
    }

}
