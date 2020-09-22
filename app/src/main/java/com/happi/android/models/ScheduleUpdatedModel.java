package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ScheduleUpdatedModel implements Serializable {

    @SerializedName("uniqueid")
    private Integer uniqueid;

    @SerializedName("channel_id")
    private Integer channel_id;

    @SerializedName("video_title")
    private String video_title;

    @SerializedName("show_id")
    private String show_id;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("video_id")
    private String video_id;

    @SerializedName("start_time")
    private String start_time;

    @SerializedName("end_time")
    private String end_time;

    @SerializedName("duration")
    private String duration;

    @SerializedName("video_duration")
    private String video_duration;

    @SerializedName("schedule_date")
    private String schedule_date;

    @SerializedName("teaser")
    private String teaser;

    @SerializedName("teaser_flag")
    private String teaser_flag;

    @SerializedName("start_date_time")
    private String start_date_time;

    @SerializedName("end_date_time")
    private String end_date_time;

    private boolean isLive = false;
    private boolean isNext = false;
    private Long epoch = 0L;


    public String getStart_date_time() {
        return start_date_time;
    }

    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    public String getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public boolean isNext() {
        return isNext;
    }

    public void setNext(boolean next) {
        isNext = next;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public Integer getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(Integer uniqueid) {
        this.uniqueid = uniqueid;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getTeaser_flag() {
        return teaser_flag;
    }

    public void setTeaser_flag(String teaser_flag) {
        this.teaser_flag = teaser_flag;
    }
}
