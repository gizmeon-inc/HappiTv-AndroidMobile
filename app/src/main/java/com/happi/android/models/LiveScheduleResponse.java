package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LiveScheduleResponse implements Serializable {
    @SerializedName("data")
    @Expose
    private List<LiveScheduleModel> data;

    public List<LiveScheduleModel> getData() {
        return data;
    }

    public class LiveScheduleModel implements Serializable{
        @SerializedName("starttime")
        @Expose
        private String starttime;
        @SerializedName("endtime")
        @Expose
        private String endtime;
        @SerializedName("video_title")
        @Expose
        private String video_title;
        @SerializedName("video_description")
        @Expose
        private String video_description;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;

////////////////////////////////////////////////////////////////////////////
        private Date startDateTime = null;
        private Date endDateTime = null;

        public Date getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(Date startDateTime) {
            this.startDateTime = startDateTime;
        }

        public Date getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(Date endDateTime) {
            this.endDateTime = endDateTime;
        }

        ////////////////////////////////////////////////////////////////////////////
        public String getStarttime() {
            return starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public String getVideo_title() {
            return video_title;
        }

        public String getVideo_description() {
            return video_description;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }

}
