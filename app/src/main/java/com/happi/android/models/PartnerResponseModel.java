package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PartnerResponseModel implements Serializable {

    @SerializedName("data")
    @Expose
    private List<PartnerModel> data;

    public List<PartnerModel> getData() {
        return data;
    }

    public void setData(List<PartnerModel> data) {
        this.data = data;
    }

    public static class PartnerModel implements Serializable{

        @SerializedName("partner_id")
        @Expose
        private int partner_id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("video_id")
        @Expose
        private int video_id;
        @SerializedName("video_title")
        @Expose
        private String video_title;


        public int getPartner_id() {
            return partner_id;
        }

        public void setPartner_id(int partner_id) {
            this.partner_id = partner_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getVideo_id() {
            return video_id;
        }

        public void setVideo_id(int video_id) {
            this.video_id = video_id;
        }

        public String getVideo_title() {
            return video_title;
        }

        public void setVideo_title(String video_title) {
            this.video_title = video_title;
        }
    }

}
