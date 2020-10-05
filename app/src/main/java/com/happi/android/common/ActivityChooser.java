package com.happi.android.common;

import android.content.Intent;

import com.happi.android.CategoriesListActivity;
import com.happi.android.CategoryViewActivity;
import com.happi.android.ChannelLivePlayerActivity;
import com.happi.android.ChannelScheduleActivity;
import com.happi.android.HomeActivity;
import com.happi.android.LiveVideoListingActivity;
import com.happi.android.MainHomeActivity;
import com.happi.android.SearchActivity;
import com.happi.android.ShowDetailsActivity;
import com.happi.android.ShowVideoActivity;
import com.happi.android.VideoPlayerActivity;
import com.happi.android.utils.ConstantUtils;

public class ActivityChooser {


    public static void goToActivity(int key, Object intentData) {

        Intent intent = null;

        switch (key) {


            case ConstantUtils.HOME_ACTIVITY:
               // isSameClass("HomeActivity");
                isSameClass("MainHomeActivity");

                //intent = new Intent(HappiApplication.getCurrentActivity(), HomeActivity.class);
                intent = new Intent(HappiApplication.getCurrentActivity(), MainHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case ConstantUtils.VIDEO_PLAYER_ACTIVITY:
                isSameClass("VideoPlayerActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        VideoPlayerActivity.class);
                int videoId = (int) intentData;
                intent.putExtra(ConstantUtils.VIDEO_DETAILS, videoId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case ConstantUtils.CATEGORYVIEW_ACTIVITY:
                isSameClass("CategoryViewActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        CategoryViewActivity.class);

                String category = (String) intentData;
                intent.putExtra(ConstantUtils.CATEGORY_DETAILS, category);

                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case ConstantUtils.SHOWSVIDEO_ACTIVITY:
                isSameClass("ShowVideoActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ShowVideoActivity.class);

                String show = (String) intentData;
                intent.putExtra(ConstantUtils.SHOW_DETAILS, show);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

            case ConstantUtils.CHANNEL_SCHEDULE_ACTIVITY:
                isSameClass("ChannelScheduleActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ChannelScheduleActivity.class);

                int channel_id = (int) intentData;
                //String thumbnail = (String) imageThumbnail;
                intent.putExtra(ConstantUtils.CHANNEL_ID, channel_id);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;
            case ConstantUtils.SHOW_DETAILS_ACTIVITY:
                // isSameClass("ShowDetailsActivity");

                intent = new Intent(HappiApplication.getCurrentActivity(),
                        ShowDetailsActivity.class);
                String showId = (String) intentData;
                intent.putExtra(ConstantUtils.SHOW_ID, showId);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

        }

        if (intent != null) {

            HappiApplication.getCurrentActivity().startActivity(intent);
            HappiApplication.getCurrentActivity().overridePendingTransition(0,0);
        }
    }

    public static void goToHome(int key, Integer channelID) {

        Intent intent = null;

        switch (key) {

            case ConstantUtils.CHANNEL_HOME_ACTIVITY:
                // intent = new Intent(HappiApplication.getCurrentActivity(), ChannelHomeActivity.class);
                intent = new Intent(HappiApplication.getCurrentActivity(), ChannelLivePlayerActivity.class);
                intent.putExtra(ConstantUtils.CHANNEL_ID, channelID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

        }

        if (intent != null) {

            HappiApplication.getCurrentActivity().startActivity(intent);
            //HappiApplication.getCurrentActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            HappiApplication.getCurrentActivity().overridePendingTransition(0, 0);
        }
    }

    public static void isSameClass(String className) {
        if (className.equals(HappiApplication.getCurrentActivity().getClass().getSimpleName())) {
            HappiApplication.getCurrentActivity().finish();
        }
    }
    public static boolean isSameClassForMenuSelector(String className) {
        boolean isSameClass = false;
        if (className.equals(HappiApplication.getCurrentActivity().getClass().getSimpleName())) {
            //HappiApplication.getCurrentActivity().finish();
            isSameClass = true;
        }
        return isSameClass;
    }

    public static void goToSelectedActivity(int key) {

        Intent intent = null;
        boolean isSameClass = false;

        switch (key) {

            case ConstantUtils.HOME_ACTIVITY:
               // isSameClass("HomeActivity");
                isSameClass("MainHomeActivity");
              //  isSameClass = isSameClassForMenuSelector("MainHomeActivity");
               // intent = new Intent(HappiApplication.getCurrentActivity(), HomeActivity.class);
              //  if(!isSameClass){
                    intent = new Intent(HappiApplication.getCurrentActivity(), MainHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
               // }
                break;

            case ConstantUtils.SEARCH_ACTIVITY:
                isSameClass("SearchActivity");
              //  isSameClass = isSameClassForMenuSelector("SearchActivity");
              //  if(!isSameClass){
                    intent = new Intent(HappiApplication.getCurrentActivity(),
                            SearchActivity.class);
                    intent.putExtra("search_type", "show");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
              //  }

                break;
            case ConstantUtils.CATEGORIES_LIST_ACTIVITY:
                isSameClass("CategoriesListActivity");
                //isSameClass = isSameClassForMenuSelector("CategoriesListActivity");
               // if(!isSameClass){
                    intent = new Intent(HappiApplication.getCurrentActivity(),
                            CategoriesListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
               // }
                break;
                case ConstantUtils.LIVE_VIDEO_LISTING_ACTIVITY:
                isSameClass("LiveVideoListingActivity");
                    intent = new Intent(HappiApplication.getCurrentActivity(),
                            LiveVideoListingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                break;

        }

        if (intent != null) {
            isPlayerOrSubscription(HappiApplication.getCurrentActivity().getClass().getSimpleName());
            HappiApplication.getCurrentActivity().startActivity(intent);
            HappiApplication.getCurrentActivity().overridePendingTransition(0, 0);
        }


    }

    public static void isPlayerOrSubscription(String className) {
        switch (className) {


                case "VideoPlayerActivity":
                case "ChannelLivePlayerActivity":
                case "SubscriptionActivity":
                    HappiApplication.getCurrentActivity().finish();
                    break;

        }
    }
}