package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gizmeon on 12-09-2017.
 */

public class LoginResponseModel {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Data>  data;


    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Data> getData() {
        return data;
    }

    public class Data
    {
        @SerializedName("user_id")
        private int user_id;
        @SerializedName("first_name")
        private String user_name;
        @SerializedName("expiry_date")
        private String expiry_date;
        @SerializedName("user_email")
        private String user_email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("validity")
        private boolean validity;

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getExpiry_date() {
            return expiry_date;
        }

        public boolean isValidity() {
            return validity;
        }
    }
}

