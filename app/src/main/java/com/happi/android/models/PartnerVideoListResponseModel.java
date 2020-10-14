package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PartnerVideoListResponseModel implements Serializable {

    @SerializedName("data")
    @Expose
    private PartnerDataModel data;

    public PartnerDataModel getData() {
        return data;
    }

    public  static class PartnerDataModel implements Serializable{

        @SerializedName("partner_id")
        @Expose
        private String partner_id;
        @SerializedName("partner_name")
        @Expose
        private String partner_name;
        @SerializedName("partner_description")
        @Expose
        private String partner_description;
        @SerializedName("partner_image")
        @Expose
        private String partner_image;
        @SerializedName("categories")
        @Expose
        private List<CategoryWiseShowsModel> categories;

        public String getPartner_id() {
            return partner_id;
        }

        public String getPartner_name() {
            return partner_name;
        }

        public String getPartner_description() {
            return partner_description;
        }

        public String getPartner_image() {
            return partner_image;
        }

        public List<CategoryWiseShowsModel> getCategories() {
            return categories;
        }
    }
}
