package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WatchlistflagResponseModel implements Serializable {

    /*"status": 100,
    "data": [
        {
            "watchlist_flag": 1
        }
    ],
    "success": true,
    "message": "watchlist flag of corresponding user.."*/

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private List<WatchListData> watchListData;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public class WatchListData implements Serializable{
        @SerializedName("watchlist_flag")
        private int watchlist_flag;

        public int getWatchlist_flag() {
            return watchlist_flag;
        }

        public void setWatchlist_flag(int watchlist_flag) {
            this.watchlist_flag = watchlist_flag;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<WatchListData> getWatchListData() {
        return watchListData;
    }

    public void setWatchListData(List<WatchListData> watchListData) {
        this.watchListData = watchListData;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
