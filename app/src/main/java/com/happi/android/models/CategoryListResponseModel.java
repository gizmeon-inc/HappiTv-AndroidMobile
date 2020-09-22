package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryListResponseModel {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    private List<Category> data = null;

    public class Category implements Serializable {

        @SerializedName("categoryid")
        private int categoryid;
        @SerializedName("category")
        private String category;
        @SerializedName("genre_icon")
        private String category_icon;

        public int getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(int categoryid) {
            this.categoryid = categoryid;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategory_icon() {
            return category_icon;
        }

        public void setCategory_icon(String category_icon) {
            this.category_icon = category_icon;
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

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }
}


/*
{
    "status":100,
    "data":[
        {
        "categoryid":1,
        "category":"Sports"
        },
        {
        "categoryid":2,
        "category":"News"
        }
    ],
    "success": true,
    "message": "Category list"
}*/
