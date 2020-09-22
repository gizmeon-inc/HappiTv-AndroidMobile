package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhoneVerificationModel {
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("phone_verified_flag")
    @Expose
    private String phone_verified_flag;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone_verified_flag() {
        return phone_verified_flag;
    }

    public void setPhone_verified_flag(String phone_verified_flag) {
        this.phone_verified_flag = phone_verified_flag;
    }
}
