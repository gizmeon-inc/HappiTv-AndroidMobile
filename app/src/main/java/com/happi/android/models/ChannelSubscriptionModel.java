package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelSubscriptionModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("pub_id")
    @Expose
    private Integer pub_id;
    @SerializedName("channel_id")
    @Expose
    private Integer channel_id;
    @SerializedName("subscription_id")
    @Expose
    private String subscription_id;
    @SerializedName("subscription_name")
    @Expose
    private String subscription_name;
    @SerializedName("subscription_type_name")
    @Expose
    private String subscription_type_name;
    @SerializedName("subscription_type_id")
    @Expose
    private Integer subscription_type_id;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("android_keyword")
    @Expose
    private String android_keyword;

    @SerializedName("interval")
    @Expose
    private String interval;

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    private String new_price = "";


    public String getNew_price() {
        return new_price;
    }

    public void setNew_price(String new_price) {
        this.new_price = new_price;
    }

    public String getAndroid_keyword() {
        return android_keyword;
    }

    public void setAndroid_keyword(String android_keyword) {
        this.android_keyword = android_keyword;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPub_id() {
        return pub_id;
    }

    public void setPub_id(Integer pub_id) {
        this.pub_id = pub_id;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getSubscription_name() {
        return subscription_name;
    }

    public void setSubscription_name(String subscription_name) {
        this.subscription_name = subscription_name;
    }

    public String getSubscription_type_name() {
        return subscription_type_name;
    }

    public void setSubscription_type_name(String subscription_type_name) {
        this.subscription_type_name = subscription_type_name;
    }

    public Integer getSubscription_type_id() {
        return subscription_type_id;
    }

    public void setSubscription_type_id(Integer subscription_type_id) {
        this.subscription_type_id = subscription_type_id;
    }
}
