package com.javierdelgado.popularmovies_stage1;

import java.io.Serializable;

/**
 * Created by Delga on 03/05/2017.
 */

class Movie implements Serializable {

    private int id;
    private String title;
    private String cover;
    private String releaseDate;
    private double rating;
    private String description;
    private String backdrop;

    public Movie(int id, String title, String cover, String backdrop, String releaseDate, double rating, String description) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.backdrop = backdrop;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.description = description;

    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getBackdrop() {
        return backdrop;
    }
}
