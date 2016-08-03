package com.juilyoon.booklook;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juil on 16-08-01.
 */
public class Book implements Parcelable {
    private String title;
    private String[] authors;
    private String description;
    private String thumbnailUrl;
    private String infoUrl;

    public Book(String title, String[] authors, String description, String thumbnailUrl, String infoUrl) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.infoUrl = infoUrl;
    }

    private Book(Parcel in) {
        title = in.readString();
        authors = in.createStringArray();
        description = in.readString();
        thumbnailUrl = in.readString();
        infoUrl = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeArray(authors);
        out.writeString(description);
        out.writeString(thumbnailUrl);
        out.writeString(infoUrl);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel (Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
