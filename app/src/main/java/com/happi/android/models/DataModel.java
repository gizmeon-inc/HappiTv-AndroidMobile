package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataModel implements Serializable {

    @SerializedName("metadata")
    private List<ShowMetaDataModel> metaDataModelList;
    @SerializedName("videos")
    private List<VideoModelUpdated> videoModelUpdatedList;
    @SerializedName("categories")
    private List<CategoryModelUpdated> categoryModelUpdatedList;
    @SerializedName("languages")
    private List<LanguageModelUpdated> languageModelUpdatedList;


    public class CategoryModelUpdated implements Serializable {
        @SerializedName("category_id")
        private String category_id;
        @SerializedName("category_name")
        private String category_name;

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }
    }

    public class LanguageModelUpdated implements  Serializable{

        @SerializedName("language_id")
        private Integer language_id;
        @SerializedName("audio_language_name")
        private String audio_language_name;

        public Integer getLanguage_id() {
            return language_id;
        }

        public void setLanguage_id(Integer language_id) {
            this.language_id = language_id;
        }

        public String getAudio_language_name() {
            return audio_language_name;
        }

        public void setAudio_language_name(String audio_language_name) {
            this.audio_language_name = audio_language_name;
        }
    }

    public List<ShowMetaDataModel> getMetaDataModelList() {
        return metaDataModelList;
    }

    public void setMetaDataModelList(List<ShowMetaDataModel> metaDataModelList) {
        this.metaDataModelList = metaDataModelList;
    }

    public List<VideoModelUpdated> getVideoModelUpdatedList() {
        return videoModelUpdatedList;
    }

    public void setVideoModelUpdatedList(List<VideoModelUpdated> videoModelUpdatedList) {
        this.videoModelUpdatedList = videoModelUpdatedList;
    }

    public List<CategoryModelUpdated> getCategoryModelUpdatedList() {
        return categoryModelUpdatedList;
    }

    public void setCategoryModelUpdatedList(List<CategoryModelUpdated> categoryModelUpdatedList) {
        this.categoryModelUpdatedList = categoryModelUpdatedList;
    }

    public List<LanguageModelUpdated> getLanguageModelUpdatedList() {
        return languageModelUpdatedList;
    }

    public void setLanguageModelUpdatedList(List<LanguageModelUpdated> languageModelUpdatedList) {
        this.languageModelUpdatedList = languageModelUpdatedList;
    }
}
