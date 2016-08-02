package com.juilyoon.booklook;

/**
 * Created by juil on 16-08-01.
 */
public class Book {
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
}
