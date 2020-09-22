package com.happi.android;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happi.android.adapters.ChannelAdapter;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScheduleActivity extends BaseActivity implements  ChannelAdapter.itemClickListener {
ImageView iv_menu;
ImageView iv_back;
ImageView iv_logo_text;
TextView tv_errormsg;
TypefacedTextViewRegular tv_title;
LinearLayout rl_end_icons;
RecyclerView rv_channels;
private CompositeDisposable compositeDisposable;
ChannelAdapter channelAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
        rl_end_icons = findViewById(R.id.rl_end_icons);
        rv_channels = findViewById(R.id.rv_channels);
        tv_errormsg = findViewById(R.id.tv_errormsg);


        iv_menu.setVisibility(View.GONE);
        rl_end_icons.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_logo_text.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);

        tv_title.setText(R.string.schedule);

        compositeDisposable = new CompositeDisposable();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleActivity.super.onBackPressed();
            }
        });
        channelAdapter = new ChannelAdapter(getApplicationContext(),this::onChannelItemClicked);

        loadChannelList();
    }




    private void loadChannelList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.getChannels(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelresponse -> {

                    if (channelresponse.getData().size()!=0) {
                       channelAdapter.updateList(channelresponse.getData());
                        rv_channels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        rv_channels.setAdapter(channelAdapter);

                        } else {
                            //Toast.makeText(this," DATA Received",Toast.LENGTH_LONG).show();
                            //Log.e("SCHEDULE ACTIVITY",""+channelresponse.getData());
                        Toast.makeText(this,"NO DATA",Toast.LENGTH_LONG).show();

                    }
                }, throwable -> {
                    displayErrorLayout();

                });
        compositeDisposable.add(videoDisposable);

    }
    private void displayErrorLayout() {

        // loading_animation.setVisibility(View.GONE);
        rv_channels.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(getString(R.string.no_results_found));
    }


    @Override
    public void onChannelItemClicked(int adapterPosition) {

       //ActivityChooser.goToActivity(ConstantUtils.CHANNEL_SCHEDULE_ACTIVITY,
           //     channelAdapter.getItem(adapterPosition).getChannelId());
       //,channelAdapter.getItem(adapterPosition).getLogo()
        Intent intent= new Intent(ScheduleActivity.this,ChannelScheduleActivity.class);
        intent.putExtra(ConstantUtils.CHANNEL_ID, channelAdapter.getItem(adapterPosition).getChannelId());
        intent.putExtra(ConstantUtils.CHANNEL_thumbnail, channelAdapter.getItem(adapterPosition).getLogo());
        startActivity(intent);


    }

}
