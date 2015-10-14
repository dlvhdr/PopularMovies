package com.dlv.popularmovies;

/**
 * Created by dlv on 14/10/2015.
 */
public class Movie {
    public static String THUMBNAIL_URL_BASE = "http://image.tmdb.org/t/p/w185";

    private String mName;
    private String mThumbnailFileName;
    private long mID;

    public Movie(long id, String name, String thumbnailFileName) {
        this.mID = id;
        this.mName = name;
        this.mThumbnailFileName = thumbnailFileName;
    }

    public String getThumbnailURL() {
        return THUMBNAIL_URL_BASE + mThumbnailFileName;
    }
}
