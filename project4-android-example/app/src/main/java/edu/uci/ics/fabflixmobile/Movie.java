package edu.uci.ics.fabflixmobile;

public class Movie {
    private String title;
    private String year;
    private String director;
    private String genre;
    private String star;
    private String id;

    public Movie(String id, String title, String year, String director, String genre, String star) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.star = star;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }
}
