package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShowMetaDataModel implements Serializable {

    /*             "show_id": 242,
                    "show_name": "10 Eco Getaways",
                    "logo": "1571382077.jpg",
                    "producer": "UHD Lab Production",
                    "theme": null,
                    "year": "2019",
                    "teaser": null,
                    "teaser_flag": 3,
                    teaser_status_flag
                    "resolution": "10 x 52 Min HD TV Series",
                    "synopsis": "10 Eco      */

    @SerializedName("show_id")
    private Integer show_id;
    @SerializedName("show_name")
    private String show_name;
    @SerializedName("logo")
    private String logo;
    @SerializedName("producer")
    private String producer;
    @SerializedName("theme")
    private String theme;
    @SerializedName("year")
    private String year;
    @SerializedName("teaser")
    private String teaser;
    @SerializedName("teaser_flag")
    private String teaser_flag;
    @SerializedName("teaser_status_flag")
    private String teaser_status_flag;
    @SerializedName("resolution")
    private String resolution;
    @SerializedName("synopsis")
    private String synopsis;
    @SerializedName("director")
    private String director;
    @SerializedName("show_cast")
    private String show_cast;
    @SerializedName("liked_flag")
    private int liked_flag;
    @SerializedName("watchlist_flag")
    private int watchlist_flag;
    @SerializedName("disliked_flag")
    private int disliked_flag;

    public int getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(int liked_flag) {
        this.liked_flag = liked_flag;
    }

    public int getWatchlist_flag() {
        return watchlist_flag;
    }

    public void setWatchlist_flag(int watchlist_flag) {
        this.watchlist_flag = watchlist_flag;
    }

    public int getDisliked_flag() {
        return disliked_flag;
    }

    public void setDisliked_flag(int disliked_flag) {
        this.disliked_flag = disliked_flag;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getShow_cast() {
        return show_cast;
    }

    public void setShow_cast(String show_cast) {
        this.show_cast = show_cast;
    }

    public Integer getShow_id() {
        return show_id;
    }

    public void setShow_id(Integer show_id) {
        this.show_id = show_id;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getTeaser_flag() {
        return teaser_flag;
    }

    public void setTeaser_flag(String teaser_flag) {
        this.teaser_flag = teaser_flag;
    }

    public String getTeaser_status_flag() {
        return teaser_status_flag;
    }

    public void setTeaser_status_flag(String teaser_status_flag) {
        this.teaser_status_flag = teaser_status_flag;
    }
}
