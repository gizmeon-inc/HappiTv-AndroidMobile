package com.happi.android.models;

import com.happi.android.R;

public enum BannerModel {

    ONE(R.layout.banner_one),
    TWO(R.layout.banner_two),
    THREE(R.layout.banner_three),
    FOUR(R.layout.banner_four),
    FIVE(R.layout.banner_five);

    private int mLayoutResId;

    BannerModel(int layoutResId) {
        mLayoutResId = layoutResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
