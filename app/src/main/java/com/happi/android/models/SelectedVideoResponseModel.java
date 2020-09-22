package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SelectedVideoResponseModel implements Serializable {

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<SelectedVideoModel> selectedVideoModelList;
    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private  String message;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public List<SelectedVideoModel> getSelectedVideoModelList() {
        return selectedVideoModelList;
    }

    public void setSelectedVideoModelList(List<SelectedVideoModel> selectedVideoModelList) {
        this.selectedVideoModelList = selectedVideoModelList;
    }
}
/*{
    "status": 100,
    "data": [
        {
            "pubid": 50012,
            "channel_name": "Ananda Media",
            "user_image": "1567428910.png",
            "video_id": 1421,
            "show_id": 242,
            "video_title": "ECO GATEWAYS AZORES",
            "premium_flag": 0,
            "video_description": "10 Eco Getaways is a new series about eco-tourism, a present day trend among modern travelers. Residents of metropolis need a break from the traffic noise, a mad rhythm of everyday life and air pollution. Clean air, charming landscapes, unity with nature, enjoying peace and tranquility attract many travelers. Ecological tourism is a new way to get acquainted with the unique natural monuments, to study the cultural and ethnographic features of the area, which are very closely connected with nature. The creators of this series primarily want to show that the outdoor recreation is very accessible to residents of many countries. And the main thing in this type of travel is unity with nature, visiting of natural territories, relatively unaffected by anthropogenic impact.",
            "video_name": "https://gizmeon.s.llnwi.net/vod/201910101570707147/playlist~360p.m3u8",
            "category_name": [
                "Animals",
                "Environmental",
                "Nature",
                "Travel",
                "Wildlife"
            ],
            "category_id": [
                4,
                6,
                20,
                21,
                22
            ],
            "languageid": [
                1
            ],
            "array_agg": [
                "English"
            ],
            "thumbnail": "1571382291.jpg",
            "video_duration": "3128.6",
            "view_count": 12,
            "like_count": 0,
            "channel_id": 275,
            "resolution": "10 x 52 Min HD TV Series",
            "year": "2019",
            "producer": "UHD Lab Production",
            "theme": null,
            "synopsis": "10 Eco Getaways is a new series about eco-tourism, a present day trend among modern travelers. Residents of metropolis need a break from the traffic noise, a mad rhythm of everyday life and air pollution. Clean air, charming landscapes, unity with nature, enjoying peace and tranquility attract many travelers. Ecological tourism is a new way to get acquainted with the unique natural monuments, to study the cultural and ethnographic features of the area, which are very closely connected with nature. The creators of this series primarily want to show that the outdoor recreation is very accessible to residents of many countries. And the main thing in this type of travel is unity with nature, visiting of natural territories, relatively unaffected by anthropogenic impact."
        }
    ],
    "success": true,
    "message": "Selected Video details with user"
}*/
