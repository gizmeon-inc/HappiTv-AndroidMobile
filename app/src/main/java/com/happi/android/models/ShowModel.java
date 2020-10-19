package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShowModel implements Serializable {

    @SerializedName("show_id")
    private String show_id;
    @SerializedName("show_name")
    private String show_name;

    @SerializedName("video_flag")
    private Integer video_flag;
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
    private List<String> category_id_list;
    @SerializedName("category_name")
    private List<String> category_name;
    @SerializedName("languageid")
    private List<String> languageid;
    @SerializedName("languagename")
    private List<String> languagename;

    private String cardType = "";

    //----------------------------for search------------------------------------//
    @SerializedName("video_id")
    private Integer video_id;

    public Integer getVideo_id() {
        return video_id;
    }

    public void setVideo_id(Integer video_id) {
        this.video_id = video_id;
    }

    //--------------------------------------------------------------------------//
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

    public List<String> getCategory_id_list() {
        return category_id_list;
    }

    public void setCategory_id_list(List<String> category_id_list) {
        this.category_id_list = category_id_list;
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

    public Integer getVideo_flag() {
        return video_flag;
    }

    public void setVideo_flag(Integer video_flag) {
        this.video_flag = video_flag;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}

