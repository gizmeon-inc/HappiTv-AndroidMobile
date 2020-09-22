package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShowsResponseModel implements Serializable {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    private List<ShowData> data;

    public class ShowData {

        @SerializedName("show_id")
        private String show_id;
        @SerializedName("show_name")
        private String show_name;
        @SerializedName("logo")
        private String logo;
        @SerializedName("genre_id")
        private String genre_id;
        @SerializedName("channel_id")
        private String channel_id;
        @SerializedName("delete_status")
        private String delete_status;
        @SerializedName("premium_flag")
        @Expose
        private String premiumFlag;
        public String getPremiumFlag() {
            return premiumFlag;
        }

        public void setPremiumFlag(String premiumFlag) {
            this.premiumFlag = premiumFlag;
        }


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

        public String getGenre_id() {
            return genre_id;
        }

        public void setGenre_id(String genre_id) {
            this.genre_id = genre_id;
        }

        public String getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(String channel_id) {
            this.channel_id = channel_id;
        }

        public String getDelete_status() {
            return delete_status;
        }

        public void setDelete_status(String delete_status) {
            this.delete_status = delete_status;
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

    public List<ShowData> getData() {
        return data;
    }

    public void setData(List<ShowData> data) {
        this.data = data;
    }

}

/*"status": 100,
    "data": [
        {
            "show_id": 5,
            "show_name": "show11",
            "logo": "tsunami.jpg",
            "genre_id": 4,
            "channel_id": 149,
            "delete_status": 1
        }
     ],
    "success": true,
    "message": "Shows list"
}*/
