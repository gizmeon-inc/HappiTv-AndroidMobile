package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguageModel {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<LanguageList> data = null;

    public class LanguageList {

        @SerializedName("language_id")
        @Expose
        private int language_id;
        @SerializedName("language_name")
        @Expose
        private String language_name;
        @SerializedName("language_icon")
        @Expose
        private String language_icon;
        @Expose
        @SerializedName("selected")
        private boolean selected = false;

        public int getLanguage_id() {
            return language_id;
        }

        public void setLanguage_id(int language_id) {
            this.language_id = language_id;
        }

        public String getLanguage_name() {
            return language_name;
        }

        public void setLanguage_name(String language_name) {
            this.language_name = language_name;
        }

        public String getLanguage_icon() {
            return language_icon;
        }

        public void setLanguage_icon(String language_icon) {
            this.language_icon = language_icon;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public List<LanguageList> getData() {
        return data;
    }

    public void setData(List<LanguageList> data) {
        this.data = data;
    }

}
