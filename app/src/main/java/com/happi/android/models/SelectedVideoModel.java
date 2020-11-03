package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SelectedVideoModel implements Serializable {
    /*{
                "pubid": 50012,
                "channel_name": "Ananda Media",
                "user_image": "1567428910.png",
                "video_id": 1421,
                "show_id": 242,
                "video_title": "ECO GATEWAYS AZORES",
                "premium_flag": 0,
                "video_description": "10 Eco Getaways is a new series about eco-tourism, a present day trend among modern travelers. Residents of metropolis need a break from the traffic noise, a mad rhythm of everyday life and air pollution. Clean air, charming landscapes, unity with nature, enjoying peace and tranquility attract many travelers. Ecological tourism is a new way to get acquainted with the unique natural monuments, to study the cultural and ethnographic features of the area, which are very closely connected with nature. The creators of this series primarily want to show that the outdoor recreation is very accessible to residents of many countries. And the main thing in this type of travel is unity with nature, visiting of natural territories, relatively unaffected by anthropogenic impact.",
                "video_name": "https://gizmeon.s.llnwi.net/vod/201910101570707147/playlist~360p.m3u8",
                "category_name": [
                    "Animals",
                    "Environmental",
                    "Nature",
                    "Travel",
                    "Wildlife"
                ],
                "category_id": [
                    4,
                    6,
                    20,
                    21,
                    22
                ],
                "languageid": [
                    1
                ],
                "array_agg": [
                    "English"
                ],
                "thumbnail": "1571382291.jpg",
                "video_duration": "3128.6",
                "view_count": 12,
                "like_count": 0,
                "channel_id": 275,
                "resolution": "10 x 52 Min HD TV Series",
                "year": "2019",
                "producer": "UHD Lab Production",
                "theme": null,
                "synopsis": "10 Eco Getaways is a new series about eco-tourism, a present day trend among modern travelers. Residents of metropolis need a break from the traffic noise, a mad rhythm of everyday life and air pollution. Clean air, charming landscapes, unity with nature, enjoying peace and tranquility attract many travelers. Ecological tourism is a new way to get acquainted with the unique natural monuments, to study the cultural and ethnographic features of the area, which are very closely connected with nature. The creators of this series primarily want to show that the outdoor recreation is very accessible to residents of many countries. And the main thing in this type of travel is unity with nature, visiting of natural territories, relatively unaffected by anthropogenic impact."
            }*/
    @SerializedName("pubid")
    private String pubid;
    @SerializedName("channel_name")
    private String channel_name;
    @SerializedName("user_image")
    private String user_image;
    @SerializedName("video_id")
    private String video_id;
    @SerializedName("show_id")
    private String show_id;
    @SerializedName("video_title")
    private String video_title;
    @SerializedName("premium_flag")
    private String premium_flag;
    @SerializedName("payper_flag")
    private String payper_flag;
    @SerializedName("rental_flag")
    private String rental_flag;
    @SerializedName("video_description")
    private String video_description;
    @SerializedName("video_name")
    private String video_name;
    @SerializedName("category_name")
    private List<String> category_name;
    @SerializedName("category_id")
    private List<String> category_id;
    @SerializedName("languageid")
    private List<String> languageid;
    @SerializedName("array_agg")
    private List<String> array_agg;
    @SerializedName("thumbnail")
    private String thumbnail;
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
    @SerializedName("theme")
    private String theme;
    @SerializedName("synopsis")
    private String synopsis;

    private int liked_flag = 0;
    //==================ad====================//
    @SerializedName("ad_link")
    private String ad_link;


    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    //========================================//

    public String getRental_flag() {
        return rental_flag;
    }

    public void setRental_flag(String rental_flag) {
        this.rental_flag = rental_flag;
    }

    public String getPayper_flag() {
        return payper_flag;
    }

    public void setPayper_flag(String payper_flag) {
        this.payper_flag = payper_flag;
    }

    public int getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(int liked_flag) {
        this.liked_flag = liked_flag;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
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

    public List<String> getCategory_name() {
        return category_name;
    }

    public void setCategory_name(List<String> category_name) {
        this.category_name = category_name;
    }

    public List<String> getCategory_id() {
        return category_id;
    }

    public void setCategory_id(List<String> category_id) {
        this.category_id = category_id;
    }

    public List<String> getLanguageid() {
        return languageid;
    }

    public void setLanguageid(List<String> languageid) {
        this.languageid = languageid;
    }

    public List<String> getArray_agg() {
        return array_agg;
    }

    public void setArray_agg(List<String> array_agg) {
        this.array_agg = array_agg;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}

