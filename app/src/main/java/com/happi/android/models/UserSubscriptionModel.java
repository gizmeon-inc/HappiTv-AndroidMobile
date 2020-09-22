package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

public class UserSubscriptionModel {
    @SerializedName("sub_id")
    private String sub_id;
    @SerializedName("valid_from")
    private String valid_from;
    @SerializedName("valid_to")
    private String valid_to;
    @SerializedName("subscription_name")
    private String subscription_name;
    @SerializedName("subscription_type_name")
    private String subscription_type_name;
    @SerializedName("subscription_type_id")
    private int subscription_type_id;
    @SerializedName("price")
    private String price;
    @SerializedName("mode_of_payment")
    private String mode_of_payment;

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSub_id() {
        return sub_id;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_to() {
        return valid_to;
    }

    public void setValid_to(String valid_to) {
        this.valid_to = valid_to;
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

    public int getSubscription_type_id() {
        return subscription_type_id;
    }

    public void setSubscription_type_id(int subscription_type_id) {
        this.subscription_type_id = subscription_type_id;
    }





}
