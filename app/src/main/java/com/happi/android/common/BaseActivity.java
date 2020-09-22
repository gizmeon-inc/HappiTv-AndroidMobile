package com.happi.android.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.happi.android.ChannelsListingActivity;
import com.happi.android.HomeActivity;
import com.happi.android.LiveVideoListingActivity;
import com.happi.android.LoginActivity;
import com.happi.android.NoInternetActivity;
import com.happi.android.PayPerViewVideoListActivity;
import com.happi.android.PremiumActivity;
import com.happi.android.R;
import com.happi.android.SettingsActivity;
import com.happi.android.VideoPlayerActivity;
import com.happi.android.WatchHistoryActivity;
import com.happi.android.WatchListActivity;
import com.happi.android.WebViewActivity;
import com.happi.android.customviews.AboutUsDialogClass;
import com.happi.android.customviews.TypefacedTextViewRegular;

import java.util.ArrayList;
import java.util.Objects;

//public class BaseActivity extends CommonActivity {
public class BaseActivity extends AppCompatActivity {

    public DrawerLayout drawer;

    public Boolean isDrawerOpened = false;
    public TypefacedTextViewRegular tv_user;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    protected void onCreateDrawer() {

        drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

         tv_user = findViewById(R.id.tv_user);
         setUserName();


        LinearLayout ll_contact_us = findViewById(R.id.ll_contact_us);
        ll_contact_us.setOnClickListener(v -> {

            AboutUsDialogClass aboutUsDialogClass =
                    new AboutUsDialogClass(HappiApplication.getCurrentActivity());
            Objects.requireNonNull(aboutUsDialogClass.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            drawer.closeDrawer(findViewById(R.id.navigation));
            aboutUsDialogClass.show();
        });

        //Menu Icon Click
        ImageView navMenu = findViewById(R.id.iv_menu);
        navMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.openDrawer(findViewById(R.id.navigation));
            }
        });

        LinearLayout ll_home = findViewById(R.id.ll_home);
        ll_home.setOnClickListener(v -> {

            finish();
            goToHomePage();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_channels = findViewById(R.id.ll_channels);
        ll_channels.setVisibility(View.GONE);
        LinearLayout ll_new = findViewById(R.id.ll_new);
        ll_channels.setOnClickListener(v -> {

            goToChannels();
            ll_new.setVisibility(View.INVISIBLE);
            drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_live = findViewById(R.id.ll_live);
        ll_live.setOnClickListener(v -> {
             //Toast.makeText(FEApplication.getCurrentContext(),"Coming Soon",Toast.LENGTH_SHORT).show();
            goToLive();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });


      /*  LinearLayout ll_schedule = findViewById(R.id.ll_schedule);
        ll_schedule.setOnClickListener(v -> {

            goToSchedule();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });*/
        LinearLayout ll_premium = findViewById(R.id.ll_premium);
        ll_premium.setOnClickListener(v -> {

            goToPremium();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_favourite = findViewById(R.id.ll_favourite);
        ll_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowListingPage("Favourites");
                drawer.closeDrawer(findViewById(R.id.navigation));
            }
        });

        LinearLayout ll_watch_list = findViewById(R.id.ll_watch_list);
        ll_watch_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowListingPage("Watch List");
                drawer.closeDrawer(findViewById(R.id.navigation));
            }
        });
        LinearLayout ll_payperview_list = findViewById(R.id.ll_payperview_list);
        ll_payperview_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowPayPerViewPage();
                drawer.closeDrawer(findViewById(R.id.navigation));
            }
        });
      /*  LinearLayout ll_watch_history = findViewById(R.id.ll_watch_history);
        ll_watch_history.setOnClickListener(v -> {

            goToWatchHistory();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });*/

        LinearLayout ll_terms = findViewById(R.id.ll_terms);
        ll_terms.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Coming Soon!!", Toast.LENGTH_SHORT).show();
           // goToWebView("T");
           // drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_privacy = findViewById(R.id.ll_privacy);
        ll_privacy.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Coming Soon!!", Toast.LENGTH_SHORT).show();
           // goToWebView("P");
           // drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_settings = findViewById(R.id.ll_settings);
        ll_settings.setOnClickListener(v -> {

            goToSettings();
            drawer.closeDrawer(findViewById(R.id.navigation));
        });

        LinearLayout ll_logout = findViewById(R.id.ll_logout);
        ll_logout.setOnClickListener(v -> {

            drawer.closeDrawer(findViewById(R.id.navigation));
            ll_new.setVisibility(View.INVISIBLE);
            logoutPrompt();
        });
    }

   public void setUserName(){
       if (!SharedPreferenceUtility.getUserName().isEmpty()) {
           tv_user.setText(SharedPreferenceUtility.getUserName());

       }
   }

    private void goToLoginPage() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void goToHomePage() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void goToChannels() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), ChannelsListingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
   /* private void goToSchedule() {
        Intent intent = new Intent(FEApplication.getCurrentContext(), ScheduleActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }*/
    private void goToPremium() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), PremiumActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    private void goToWatchHistory() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), WatchHistoryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void goToWebView(String value) {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), WebViewActivity.class);
        intent.putExtra("web_view_select", value);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void goToSettings() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void goToLive() {
        Intent intent = new Intent(HappiApplication.getCurrentContext(), LiveVideoListingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    private void goToShowListingPage(String pageContext){
        Intent intent = new Intent(HappiApplication.getCurrentContext(), WatchListActivity.class);
        intent.putExtra("pageContext", pageContext);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void goToShowPayPerViewPage(){
        Intent intent = new Intent(HappiApplication.getCurrentContext(), PayPerViewVideoListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void logoutPrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HappiApplication.getCurrentContext());
        builder.setMessage("Are you sure you want to Logout?")
                .setCancelable(false)
                .setPositiveButton("yes", (dialog, which) -> {

                    SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "", "", false,"");
                    SharedPreferenceUtility.setGuest(false);
                    SharedPreferenceUtility.setChannelId(0);
                    SharedPreferenceUtility.setVideoId(0);
                    SharedPreferenceUtility.setChannelTimeZone("");
                    HappiApplication.setSub_id(new ArrayList<>());
                    SharedPreferenceUtility.setNotificationIds(new ArrayList<>());


                    goToLoginPage();
                })
                .setNegativeButton("No", (dialog, which) -> {

                    dialog.cancel();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showAlertDialogAndExitApp(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(HappiApplication.getCurrentContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

        alertDialog.show();
    }

    @Override
    protected void onResume() {

        if (!isNetworkConnected()) {

            if (!(HappiApplication.getCurrentContext() instanceof VideoPlayerActivity)) {

                Intent noInternetIntent = new Intent(HappiApplication.getCurrentContext(),
                        NoInternetActivity.class);
                startActivity(noInternetIntent);
            } else {

                Toast.makeText(HappiApplication.getCurrentContext(), "No internet", Toast
                        .LENGTH_SHORT).show();
            }
        }

        registerReceiver();

        super.onResume();
    }

    public void registerReceiver() {

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        if(networkReceiver!=null) {
            unregisterReceiver(networkReceiver);
        }
        super.onPause();
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }


        return false;
    }

    /*private BroadcastReceiver networkReceiver123 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {

                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // we're connected
                } else {

                    if (!(FEApplication.getCurrentContext() instanceof VideoPlayerActivity)) {

                        Intent noInternetIntent = new Intent(FEApplication.getCurrentContext(),
                                NoInternetActivity.class);
                        startActivity(noInternetIntent);
                    } else {

                        Toast.makeText(FEApplication.getCurrentContext(), "No internet", Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        }
    };*/


    public BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {

                } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

                    if (!(HappiApplication.getCurrentContext() instanceof VideoPlayerActivity)) {

                        Intent noInternetIntent = new Intent(HappiApplication.getCurrentContext(),
                                NoInternetActivity.class);
                        startActivity(noInternetIntent);
                    } else {

                        Toast.makeText(HappiApplication.getCurrentContext(), "No internet", Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

}
