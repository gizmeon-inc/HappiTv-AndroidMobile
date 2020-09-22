package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ASTVHome implements Serializable {


    @SerializedName("pubid")
    @Expose
    private String pubid;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("video_limit")
    @Expose
    private Integer videoLimit;
    @SerializedName("poppo_flag")
    @Expose
    private Integer poppoFlag;
    @SerializedName("app_flag")
    @Expose
    private Object appFlag;
    @SerializedName("created")
    @Expose
    private Object created;
    @SerializedName("modified")
    @Expose
    private Object modified;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("tax_id")
    @Expose
    private Object taxId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("live_flag")
    @Expose
    private Integer liveFlag;
    @SerializedName("live_link")
    @Expose
    private String liveLink;
    @SerializedName("ad_id")
    @Expose
    private Integer adId;
    @SerializedName("ad_link")
    @Expose
    private String adLink;
    @SerializedName("premium_flag")
    private String premium_flag;
    @SerializedName("payper_flag")
    private String payper_flag;
    @SerializedName("rental_flag")
    private String rental_flag;
    @SerializedName("ad_pod_url")
    @Expose
    private String ad_pod_url;
    @SerializedName("language_id")
    @Expose
    private String language_id;
    @SerializedName("genre_id")
    @Expose
    private String genre_id;
    @SerializedName("delete_status")
    @Expose
    private String delete_status;
    @SerializedName("ch_id")
    @Expose
    private String ch_id;
    @SerializedName("logo1")
    @Expose
    private String logo1;
    @SerializedName("logo2")
    @Expose
    private String logo2;
    @SerializedName("logo3")
    @Expose
    private String logo3;
    @SerializedName("add_break_stream")
    @Expose
    private String add_break_stream;
    @SerializedName("list_order")
    @Expose
    private String list_order;
    @SerializedName("ad_break_start")
    @Expose
    private String ad_break_start;
    @SerializedName("ad_break_stop")
    @Expose
    private String ad_break_stop;
    @SerializedName("sub_id")
    @Expose
    private String sub_id;
    @SerializedName("vod_flag")
    @Expose
    private String vod_flag;


    public String getPayper_flag() {
        return payper_flag;
    }

    public void setPayper_flag(String payper_flag) {
        this.payper_flag = payper_flag;
    }

    public String getRental_flag() {
        return rental_flag;
    }

    public void setRental_flag(String rental_flag) {
        this.rental_flag = rental_flag;
    }

    public String getPremium_flag() {
        return premium_flag;
    }

    public void setPremium_flag(String premium_flag) {
        this.premium_flag = premium_flag;
    }


    public String getAd_pod_url() {
        return ad_pod_url;
    }

    public void setAd_pod_url(String ad_pod_url) {
        this.ad_pod_url = ad_pod_url;
    }


    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getVideoLimit() {
        return videoLimit;
    }

    public void setVideoLimit(Integer videoLimit) {
        this.videoLimit = videoLimit;
    }

    public Integer getPoppoFlag() {
        return poppoFlag;
    }

    public void setPoppoFlag(Integer poppoFlag) {
        this.poppoFlag = poppoFlag;
    }

    public Object getAppFlag() {
        return appFlag;
    }

    public void setAppFlag(Object appFlag) {
        this.appFlag = appFlag;
    }

    public Object getCreated() {
        return created;
    }

    public void setCreated(Object created) {
        this.created = created;
    }

    public Object getModified() {
        return modified;
    }

    public void setModified(Object modified) {
        this.modified = modified;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getTaxId() {
        return taxId;
    }

    public void setTaxId(Object taxId) {
        this.taxId = taxId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLiveFlag() {
        return liveFlag;
    }

    public void setLiveFlag(Integer liveFlag) {
        this.liveFlag = liveFlag;
    }

    public String getLiveLink() {
        return liveLink;
    }

    public void setLiveLink(String liveLink) {
        this.liveLink = liveLink;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getAdLink() {
        return adLink;
    }

    public void setAdLink(String adLink) {
        this.adLink = adLink;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
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

    public String getAdd_break_stream() {
        return add_break_stream;
    }

    public void setAdd_break_stream(String add_break_stream) {
        this.add_break_stream = add_break_stream;
    }

    public String getList_order() {
        return list_order;
    }

    public void setList_order(String list_order) {
        this.list_order = list_order;
    }

    public String getAd_break_start() {
        return ad_break_start;
    }

    public void setAd_break_start(String ad_break_start) {
        this.ad_break_start = ad_break_start;
    }

    public String getAd_break_stop() {
        return ad_break_stop;
    }

    public void setAd_break_stop(String ad_break_stop) {
        this.ad_break_stop = ad_break_stop;
    }

    public String getSub_id() {
        return sub_id;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public String getVod_flag() {
        return vod_flag;
    }

    public void setVod_flag(String vod_flag) {
        this.vod_flag = vod_flag;
    }

}