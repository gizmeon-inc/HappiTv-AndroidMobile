package com.happi.android.common;

import android.content.Intent;

import com.happi.android.CategoryViewActivity;
import com.happi.android.ChannelLivePlayerActivity;
import com.happi.android.ChannelScheduleActivity;
import com.happi.android.HomeActivity;
import com.happi.android.R;
import com.happi.android.ShowDetailsActivity;
import com.happi.android.ShowVideoActivity;
import com.happi.android.VideoPlayerActivity;
import com.happi.android.utils.ConstantUtils;

public class ActivityChooser {


    public static void goToActivity(int key, Object intentData) {

        Intent intent = null;

        switch (key) {


            case ConstantUtils.HOME_ACTIVITY:
                isSameClass("HomeActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;

            case ConstantUtils.VIDEO_PLAYER_ACTIVITY:
                isSameClass("VideoPlayerActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        VideoPlayerActivity.class);
                int videoId = (int) intentData;
                intent.putExtra(ConstantUtils.VIDEO_DETAILS, videoId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case ConstantUtils.CATEGORYVIEW_ACTIVITY:
                isSameClass("CategoryViewActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        CategoryViewActivity.class);

                String category = (String) intentData;
                intent.putExtra(ConstantUtils.CATEGORY_DETAILS, category);

              //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case ConstantUtils.SHOWSVIDEO_ACTIVITY:
                isSameClass("ShowVideoActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ShowVideoActivity.class);

                String show = (String) intentData;
                intent.putExtra(ConstantUtils.SHOW_DETAILS, show);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case ConstantUtils.CHANNEL_SCHEDULE_ACTIVITY:
                isSameClass("ChannelScheduleActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ChannelScheduleActivity.class);

                int channel_id = (int) intentData;
                //String thumbnail = (String) imageThumbnail;
                intent.putExtra(ConstantUtils.CHANNEL_ID, channel_id);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case ConstantUtils.SHOW_DETAILS_ACTIVITY:
                isSameClass("ShowDetailsActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ShowDetailsActivity.class);
                String showId = (String) intentData;
                intent.putExtra(ConstantUtils.SHOW_ID,showId);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

        }

        if (intent != null) {

            HappiApplication.getCurrentActivity().startActivity(intent);
        }
    }

    public static void goToHome(int key, Integer channelID) {

        Intent intent = null;

        switch (key) {

            case ConstantUtils.CHANNEL_HOME_ACTIVITY:
               // intent = new Intent(FEApplication.getCurrentActivity(), ChannelHomeActivity.class);
                intent = new Intent(HappiApplication.getCurrentActivity(), ChannelLivePlayerActivity.class);
                intent.putExtra(ConstantUtils.CHANNEL_ID, channelID);
                break;

        }

        if (intent != null) {

            HappiApplication.getCurrentActivity().startActivity(intent);
            HappiApplication.getCurrentActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public static void isSameClass(String className) {
        if (className.equals(HappiApplication.getCurrentActivity().getClass().getSimpleName())) {
            HappiApplication.getCurrentActivity().finish();
        }
    }
}