package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gizmeon on 25-10-2019.
 */

public class HomeVideoModel  {

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private List<HomeVideoModel.HomeVideo> data = null;

    public class HomeVideo implements Serializable {

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("data")
        private List<HomeVideoModel.HomeVideoList> data = null;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<HomeVideoModel.HomeVideoList> getData() {
            return data;
        }

        public void setData(List<HomeVideoModel.HomeVideoList> data) {
            this.data = data;
        }
    }
    public class HomeVideoList implements Serializable {

        @SerializedName("show_id")
        private int show_id;
        @SerializedName("show_name")
        private String show_name;
        @SerializedName("logo")
        private String logo;

        public int getShow_id() {
            return show_id;
        }

        public void setShow_id(int show_id) {
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


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<HomeVideoModel.HomeVideo> getData() {
        return data;
    }

    public void setData(List<HomeVideoModel.HomeVideo> data) {
        this.data = data;
    }
}
