package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelModel{
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
     @SerializedName("timezone")
    @Expose
    private String timezone;
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
    @SerializedName("first_name")
    @Expose
    private Object firstName;
    @SerializedName("premium_flag")
    @Expose
    private Integer premiumFlag;
    @SerializedName("pubid")
    @Expose
    private String pubid;
    @SerializedName("ch_id")
    @Expose
    private String ch_id;
    @SerializedName("ad_pod_url")
    @Expose
    private String ad_pod_url;
    @SerializedName("ad_link")
    @Expose
    private String ad_link;

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

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }
    public Integer getPremiumFlag() {
        return premiumFlag;
    }

    public void setPremiumFlag(Integer premiumFlag) {
        this.premiumFlag = premiumFlag;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getCh_id() {
        return ch_id;
    }

    public void setCh_id(String ch_id) {
        this.ch_id = ch_id;
    }

    public String getAd_pod_url() {
        return ad_pod_url;
    }

    public void setAd_pod_url(String ad_pod_url) {
        this.ad_pod_url = ad_pod_url;
    }

    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
