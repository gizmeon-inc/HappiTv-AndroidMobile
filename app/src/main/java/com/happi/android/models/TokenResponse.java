package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("data")
        @Expose
        private String data;
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
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


}
