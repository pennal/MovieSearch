package org.usi.ir.lp.models;

import java.util.List;

public class Movie {
    private String title;
    private String imageUrl;
    private Integer duration;
    private List<String> genres;
    private String releaseDate;
    private String description;

    private IMDBData imdbData;
    private AllMoviesData allMoviesData;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IMDBData getImdbData() {
        return imdbData;
    }

    public void setImdbData(IMDBData imdbData) {
        this.imdbData = imdbData;
    }

    public AllMoviesData getAllMoviesData() {
        return allMoviesData;
    }

    public void setAllMoviesData(AllMoviesData allMoviesData) {
        this.allMoviesData = allMoviesData;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object obj) {
        Movie m = (Movie) obj;
        // For now, just use the title
        return this.title.equals(m.getTitle());
    }
}
