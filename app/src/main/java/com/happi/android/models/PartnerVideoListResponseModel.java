package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PartnerVideoListResponseModel implements Serializable {

    @SerializedName("data")
    @Expose
    private List<PartnerVideoModel> data;

    public List<PartnerVideoModel> getData() {
        return data;
    }

    public void setData(List<PartnerVideoModel> data) {
        this.data = data;
    }

    public  static class PartnerVideoModel implements Serializable{

        @SerializedName("show_id")
        @Expose
        private String show_id;
        @SerializedName("show_name")
        @Expose
        private String show_name;
        @SerializedName("logo")
        @Expose
        private String logo;

        public String getShow_id() {
            return show_id;
        }

        public void setShow_id(String show_id) {
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
    }
}
