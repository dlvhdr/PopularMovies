package com.dlv.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by dlv on 14/10/2015.
 */
public class Movie implements Parcelable {
    public static final String DATE_FORMAT = "yyyy-mm-dd";
    public static String THUMBNAIL_URL_BASE = "http://image.tmdb.org/t/p/w185";

    private String mName;
    private String mThumbnailFileName;
    private long mID;
    private String mOverview;
    private double mUserRating;
    private Date mReleaseDate;
    private int mLength;

    public Movie(long id) {
        this.mID = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getThumbnailURL() {
        return THUMBNAIL_URL_BASE + mThumbnailFileName;
    }

    public String getThumbnailFileName() {
        return this.mThumbnailFileName;
    }

    public void setThumbnailFileName(String thumbnailFileName) {
        this.mThumbnailFileName = thumbnailFileName;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public void setUserRating(double userRating) {
        this.mUserRating = userRating;
    }

    public Date getReleaseDate() {
        return this.mReleaseDate;
    }

    public void setReleaseDate(Date date) {
        mReleaseDate = date;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        this.mLength = length;
    }

    private Movie(Parcel in) {
        mName = in.readString();
        mThumbnailFileName = in.readString();
        mID = in.readLong();
        mOverview = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = new Date(in.readLong());
        //mLength = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mThumbnailFileName);
        dest.writeLong(mID);
        dest.writeString(mOverview);
        dest.writeDouble(mUserRating);
        dest.writeLong(mReleaseDate.getTime());
        //dest.writeInt(mLength);
    }

    @Override
    public String toString() {
        return mName;
    }
}
