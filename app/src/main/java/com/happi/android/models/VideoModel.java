package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gizmeon on 16-02-2018.
 */

public class VideoModel implements Serializable {

    @SerializedName("show_id")
    private int show_id = 0;
    @SerializedName("show_name")
    private String show_name = "";
    @SerializedName("logo")
    private String logo = "";

    @SerializedName("video_id")
    private int video_id = 0;
    @SerializedName("video_title")
    private String video_title = "";
    @SerializedName("video_description")
    private String video_description = "";
    @SerializedName("video_tag")
    private String video_tag = "";
    @SerializedName("video_duration")
    private String video_duration = "";
    @SerializedName("view_count")
    private String view_count = "";
    @SerializedName("video_name")
    private String video_name = "";
    @SerializedName("channel_id")
    private String channel_id = "";
    @SerializedName("channel_name")
    private String channel_name = "";
    @SerializedName("email")
    private String email = "";
    @SerializedName("first_name")
    private String first_name = "";
    @SerializedName("user_image")
    private String user_image = "";
    @SerializedName("ad_link")
    private String ad_link = "";
    @SerializedName("thumbnail")
    private String thumbnail = "";
    @SerializedName("liked_flag")
    private int liked_flag = 0;
    @SerializedName("banner")
    private String banner;
    @SerializedName("premium_flag")
    @Expose
    private String premiumFlag;
    @SerializedName("pubid")
    @Expose
    private Integer pubid;


    public Integer getPubid() {
        return pubid;
    }

    public void setPubid(Integer pubid) {
        this.pubid = pubid;
    }


    public String getPremiumFlag() {
        return premiumFlag;
    }

    public void setPremiumFlag(String premiumFlag) {
        this.premiumFlag = premiumFlag;
    }

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }
    public int getShow_id() {
        return show_id;
    }

    public void setShow_id(int show_id) {
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
    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
    }

    public String getVideo_tag() {
        return video_tag;
    }

    public void setVideo_tag(String video_tag) {
        this.video_tag = video_tag;
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

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(int liked_flag) {
        this.liked_flag = liked_flag;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}


/*{
    "success": true,
    "status": 100,
    "data": [
        {
            "video_id": "5",
            "video_title": "Nadodikattu",
            "video_description": "A cult class mohanlal comedy  from old movie",
            "video_tag": "Comedy",
            "video_duration": "00:31",
            "view_count": "4500",
            "video_name": "https://dvyh5yjf9hurg.cloudfront.net/PoppoTV/lal.mp4",
            "channel_id": "2",
            "channel_name": "Comedycuts",
            "email": "ronaldo@gmail.com",
            "user_id": "1",
            "first_name": "ronaldo",
            "user_image": "cr7.jpg",
            "ad_link": "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=",
            "thumbnail": "ill2.jpg"
        }
    ]
}*/
