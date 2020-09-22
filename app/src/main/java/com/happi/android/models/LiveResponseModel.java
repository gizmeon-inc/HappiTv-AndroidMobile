package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LiveResponseModel implements Serializable {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    private List<LiveVideoData> data;

    private class LiveVideoData {

        @SerializedName("channel_id")
        private String channel_id;
        @SerializedName("channel_name")
        private String channel_name;
        @SerializedName("address")
        private String address;
        @SerializedName("website")
        private String website;
        @SerializedName("contact")
        private String contact;
        @SerializedName("logo")
        private String logo;
        @SerializedName("video_limit")
        private String video_limit;
        @SerializedName("poppo_flag")
        private String poppo_flag;
        @SerializedName("app_flag")
        private String app_flag;
        @SerializedName("created")
        private String created;
        @SerializedName("modified")
        private String modified;
        @SerializedName("status")
        private String status;
        @SerializedName("tax_id")
        private String tax_id;
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("live_flag")
        private String live_flag;
        @SerializedName("live_link")
        private String live_link;
        @SerializedName("ad_id")
        private String ad_id;
        @SerializedName("language_id")
        private String language_id;
        @SerializedName("genre_id")
        private String genre_id;
        @SerializedName("delete_status")
        private String delete_status;
        @SerializedName("ch_id")
        private String ch_id;
        @SerializedName("logo1")
        private String logo1;
        @SerializedName("logo2")
        private String logo2;
        @SerializedName("logo3")
        private String logo3;

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getVideo_limit() {
            return video_limit;
        }

        public void setVideo_limit(String video_limit) {
            this.video_limit = video_limit;
        }

        public String getPoppo_flag() {
            return poppo_flag;
        }

        public void setPoppo_flag(String poppo_flag) {
            this.poppo_flag = poppo_flag;
        }

        public String getApp_flag() {
            return app_flag;
        }

        public void setApp_flag(String app_flag) {
            this.app_flag = app_flag;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTax_id() {
            return tax_id;
        }

        public void setTax_id(String tax_id) {
            this.tax_id = tax_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getLive_flag() {
            return live_flag;
        }

        public void setLive_flag(String live_flag) {
            this.live_flag = live_flag;
        }

        public String getLive_link() {
            return live_link;
        }

        public void setLive_link(String live_link) {
            this.live_link = live_link;
        }

        public String getAd_id() {
            return ad_id;
        }

        public void setAd_id(String ad_id) {
            this.ad_id = ad_id;
        }

        public String getLanguage_id() {
            return language_id;
        }

        public void setLanguage_id(String language_id) {
            this.language_id = language_id;
        }

        public String getGenre_id() {
            return genre_id;
        }

        public void setGenre_id(String genre_id) {
            this.genre_id = genre_id;
        }

        public String getDelete_status() {
            return delete_status;
        }

        public void setDelete_status(String delete_status) {
            this.delete_status = delete_status;
        }

        public String getCh_id() {
            return ch_id;
        }

        public void setCh_id(String ch_id) {
            this.ch_id = ch_id;
        }

        public String getLogo1() {
            return logo1;
        }

        public void setLogo1(String logo1) {
            this.logo1 = logo1;
        }

        public String getLogo2() {
            return logo2;
        }

        public void setLogo2(String logo2) {
            this.logo2 = logo2;
        }

        public String getLogo3() {
            return logo3;
        }

        public void setLogo3(String logo3) {
            this.logo3 = logo3;
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
}

/*{
    "status": 100,
    "data": [
        {
            "channel_id": 188,
            "channel_name": "test",
            "address": null,
            "website": "www.channel.com",
            "contact": "9999999999",
            "logo": "adventure.jpg",
            "video_limit": null,
            "poppo_flag": 0,
            "app_flag": null,
            "created": null,
            "modified": null,
            "status": null,
            "tax_id": null,
            "user_id": 447,
            "live_flag": 1,
            "live_link": null,
            "ad_id": 113,
            "language_id": 2,
            "genre_id": 11,
            "delete_status": 1,
            "ch_id": "CH-588",
            "logo1": null,
            "logo2": null,
            "logo3": null
        }
     ],
    "success": true,
    "message": "Live list"
}*/
