package com.happi.android.models;

public class SetLanguagePriorityRequestModel {

    private int uid;
    private String lang_list;

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setLang_list(String lang_list) {
        this.lang_list = lang_list;
    }
}

/*
{
"uid": "333",
"lang_list": "3,4"
 }
 */
