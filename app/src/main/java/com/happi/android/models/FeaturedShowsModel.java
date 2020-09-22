package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeaturedShowsModel {


    @SerializedName("pubid")
    private String pubid;
    @SerializedName("video_id")
    private String video_id;
    @SerializedName("video_title")
    private String video_title;
    @SerializedName("premium_flag")
    private String premium_flag;
    @SerializedName("video_description")
    private String video_description;
    @SerializedName("video_name")
    private String video_name;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("show_id")
    private String show_id;
    @SerializedName("show_name")
    private String show_name;
    @SerializedName("banner")
    private String banner;
    @SerializedName("video_duration")
    private String video_duration;
    @SerializedName("view_count")
    private String view_count;
    @SerializedName("like_count")
    private String like_count;
    @SerializedName("channel_id")
    private String channel_id;
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
    @SerializedName("vid")
    private String vid;
    @SerializedName("liked_flag")
    private String liked_flag;

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getPremium_flag() {
        return premium_flag;
    }

    public void setPremium_flag(String premium_flag) {
        this.premium_flag = premium_flag;
    }

    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
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

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(String liked_flag) {
        this.liked_flag = liked_flag;
    }
}
/* "pubid": 50012,
            "video_id": 1353,
            "video_title": "Blood Moon",
            "premium_flag": 1,
            "video_description": "The full-
            "video_name": "https://gizmeon.s.llnwi.net/vod/ruvsvq/playlist~1080p.m3u8",
            "thumbnail": "1569586854.jpg",
            "show_id": 172,
            "show_name": "Blood Moon",
            "banner": "1569939209.jpg",
            "video_duration": "2661",
            "view_count": 45,
            "like_count": 0,
            "channel_id": 275,
            "resolution": "1 x 45 Min HD Documentary",
            "year": "2018",
            "producer": "Posing Productions",
            "synopsis": "The fu
             "category_id": [
                8,
                14
            ],
            "category_name": [
                "Climbing",
                "Mountain"
            ],
            "languageid": [
                1
            ],
            "languagename": [
                "English"
            ],
            "vid": null,
            "liked_flag": null  */