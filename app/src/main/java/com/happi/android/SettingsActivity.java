package com.happi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.happi.android.common.HappiApplication;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.roomController.RoomChannelSearchRepository;
import com.happi.android.roomController.RoomShowSearchRepository;
import com.happi.android.roomController.RoomVideoSearchRepository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SettingsActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_settings);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        ImageView iv_back = findViewById(R.id.iv_back);
        TypefacedTextViewRegular tv_title = findViewById(R.id.tv_title);
       // Switch sw_night_toggle = findViewById(R.id.sw_night_toggle);

        TypefacedTextViewSemiBold tv_clear_search_history = findViewById(R.id
                .tv_clear_search_history);
        TypefacedTextViewSemiBold tv_preferred_languages = findViewById(R.id
                .tv_preferred_languages);
        tv_preferred_languages.setVisibility(View.GONE);
      //  LinearLayout ll_toggle_theme = findViewById(R.id.ll_toggle_theme);

        iv_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.settings));

        String accessToken = HappiApplication
                .getAppToken();
        compositeDisposable = new CompositeDisposable();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_clear_search_history.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(HappiApplication.getCurrentContext());
            builder.setMessage("Do you want to delete Search History?")
                    .setCancelable(false)
                    .setPositiveButton("yes", (dialog, which) -> {

                        AsyncTask.execute(() -> {

                            //TODO your background code
                            RoomVideoSearchRepository roomVideoSearchRepository = new RoomVideoSearchRepository
                                    (HappiApplication.getCurrentContext());
                            roomVideoSearchRepository.deleteAllData();

                            RoomChannelSearchRepository roomChannelSearchRepository = new
                                    RoomChannelSearchRepository(HappiApplication
                                    .getCurrentContext());
                            roomChannelSearchRepository.deleteAllData();

                            RoomShowSearchRepository roomSearchSearchRepository = new
                                    RoomShowSearchRepository(HappiApplication
                                    .getCurrentContext());
                            roomSearchSearchRepository.deleteAllData();
                        });

                        Toast.makeText(HappiApplication.getCurrentContext(), "Search History deleted",
                                Toast.LENGTH_SHORT)
                                .show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {

                        dialog.cancel();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        tv_preferred_languages.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, LanguageSelectionCheckboxActivity
                    .class);
            intent.putExtra("from","settings");
            startActivity(intent);
           // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0,0);
        });

        /*int currentNightMode =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're in day time
                sw_night_toggle.setChecked(false);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                sw_night_toggle.setChecked(true);
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We don't know what mode we're in, assume notnight
                sw_night_toggle.setChecked(false);
                break;
        }*/
     /*   if (SharedPreferenceUtility.isNightMode()) {

            sw_night_toggle.setChecked(true);
        } else {

            sw_night_toggle.setChecked(false);
        }

        ll_toggle_theme.setOnClickListener(view -> {

            sw_night_toggle.toggle();
        });

        sw_night_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    sw_night_toggle.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferenceUtility.setNightMode(true);
                } else {

                    sw_night_toggle.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferenceUtility.setNightMode(false);
                }
//                Intent i = getBaseContext().getPackageManager()
//                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
                Intent launchIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchIntent.putExtra("from", "FCM");
                startActivity(launchIntent);

            }
        });*/
    }


    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(compositeDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
