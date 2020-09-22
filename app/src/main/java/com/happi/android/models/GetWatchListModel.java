package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetWatchListModel implements Serializable {

    /*      "show_id": 717,
            "show_name": "City ",
            "video_flag": 1,
            "logo": "1578167519.jpg",
            "resolution": "10 x 22 Min HD TV Series",
            "year": "2015",
            "producer": "Bike Channel",
            "synopsis": "Showing  details.",
            "category_id": [6,11,13,6],
            "category_name": ["Adventure","Cycling","Travel", "TV Series"],
            "languageid": [ 51   ],
            "languagename": [ "Italian" ],
            "watchlist_flag": 1,
            "modified": "2020-01-13T04:13:05.000Z"*/

    @SerializedName("show_id")
    private String show_id;
    @SerializedName("show_name")
    private String show_name;
    @SerializedName("video_flag")
    private int video_flag;
    @SerializedName("logo")
    private String logo;
    @SerializedName("resolution")
    private String resolution;
    @SerializedName("year")
    private String year;
    @SerializedName("producer")
    private String producer;
    @SerializedName("synopsis")
    private String synopsis;
    @SerializedName("category_id")
    private List<String> category_id;
    @SerializedName("category_name")
    private List<String> category_name;
    @SerializedName("languageid")
    private List<String> languageid;
    @SerializedName("languagename")
    private List<String> languagename;
    @SerializedName("watchlist_flag")
    private int  watchlist_flag;
    @SerializedName("liked_flag")
    private int liked_flag;
    @SerializedName("modified")
    private String modified;

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public int getVideo_flag() {
        return video_flag;
    }

    public void setVideo_flag(int video_flag) {
        this.video_flag = video_flag;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(List<String> category_id) {
        this.category_id = category_id;
    }

    public List<String> getCategory_name() {
        return category_name;
    }

    public void setCategory_name(List<String> category_name) {
        this.category_name = category_name;
    }

    public List<String> getLanguageid() {
        return languageid;
    }

    public void setLanguageid(List<String> languageid) {
        this.languageid = languageid;
    }

    public List<String> getLanguagename() {
        return languagename;
    }

    public void setLanguagename(List<String> languagename) {
        this.languagename = languagename;
    }

    public int getWatchlist_flag() {
        return watchlist_flag;
    }

    public void setWatchlist_flag(int watchlist_flag) {
        this.watchlist_flag = watchlist_flag;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(int liked_flag) {
        this.liked_flag = liked_flag;
    }
}
