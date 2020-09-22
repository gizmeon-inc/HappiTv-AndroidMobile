package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChannelScheduleModel {
    @SerializedName("schedule_id")
    @Expose
    private Integer schedule_id;
    @SerializedName("program_name")
    @Expose
    private String program_name;
    @SerializedName("channel_id")
    @Expose
    private Integer channel_id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("status_flag")
    @Expose
    private boolean status_flag = false;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("start_time")
    @Expose
    private String start_time;
    @SerializedName("end_time")
    @Expose
    private String end_time;

    private String pgm_end_time = "";
    private String pgm_start_time = "";

    private boolean showProgram = false;

    public boolean getShowProgram() {
        return showProgram;
    }

    public void setShowProgram(boolean showProgram) {
        this.showProgram = showProgram;
    }

    public String getPgm_start_time() {
        return pgm_start_time;
    }

    public void setPgm_start_time(String pgm_start_time) {
        this.pgm_start_time = pgm_start_time;
    }

    public String getPgm_end_time() {
        return pgm_end_time;
    }

    public void setPgm_end_time(String pgm_end_time) {
        this.pgm_end_time = pgm_end_time;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getStatus_flag() {
        return status_flag;
    }

    public void setStatus_flag(boolean status_flag) {
        this.status_flag = status_flag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
