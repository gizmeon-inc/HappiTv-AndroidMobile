package com.happi.android;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.ChannelListAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveVideoListingActivity extends BaseActivity implements ChannelListAdapter.itemClickListener {

    ImageView iv_menu, iv_back, iv_search, iv_logo_text;
    TypefacedTextViewRegular tv_title;
    private AnimationItem mSelectedItem;
    GridRecyclerView rv_live_list;
    ChannelListAdapter channelListAdapter;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    SkeletonScreen loadingLive;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_live_video);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
        iv_search = findViewById(R.id.iv_search);
        rv_live_list = findViewById(R.id.rv_live_list);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        iv_errorimg = findViewById(R.id.iv_errorimg);

        iv_menu.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_logo_text.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.live_channel);
        iv_search.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);

        compositeDisposable = new CompositeDisposable();
        int userId = SharedPreferenceUtility.getUserId();

        setupRecyclerview();

        iv_back.setOnClickListener(v -> {

            //backPressed
            onBackPressed();
        });
    }

    private void setupRecyclerview() {

        rv_live_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_live_list.addItemDecoration(new ItemDecorationAlbumColumns(7,3));
        channelListAdapter = new ChannelListAdapter(getApplicationContext(),
                this::onChannelItemClicked,true);
        rv_live_list.setAdapter(channelListAdapter);

        loadingLive = Skeleton.bind(rv_live_list)
                .adapter(channelListAdapter)
                .load(R.layout.loading_channel_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
        loadLiveChannelList();
       // loadLiveList();
    }
    private void loadLiveChannelList(){
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable liveDisposable = usersService.getChannels(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelListResponse -> {
                    if (channelListResponse.getData().size() != 0) {

                            updateChannelList(channelListResponse.getData());

                    }else{
                        displayErrorLayout(getString(R.string.no_results_found));
                    }

                }, throwable -> {
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(liveDisposable);
    }

    private void loadLiveList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable =
                usersService.GetPopularLiveVideos(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelListResponse -> {
                    if(channelListResponse.getData().isEmpty()){

                        displayErrorLayout(getString(R.string.no_results_found));
                    } else {

                        updateChannelList(channelListResponse.getData());
                    }
                }, throwable -> displayErrorLayout(getString(R.string.server_error)));
        compositeDisposable.add(videoDisposable);
    }

    private void updateChannelList(List<ChannelModel> data) {
        channelListAdapter.clearAll();
        channelListAdapter.addAll(data);
        loadingLive.hide();
        runLayoutAnimation(rv_live_list, mSelectedItem);
        if (channelListAdapter.isEmpty()) {
            rv_live_list.setVisibility(View.GONE);
            tv_errormsg.setVisibility(View.VISIBLE);
            iv_errorimg.setVisibility(View.VISIBLE);
            tv_errormsg.setText(getString(R.string.no_channels));
        }
    }

    private void displayErrorLayout(String message) {
        rv_live_list.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.VISIBLE);
        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }

    @Override
    public void onChannelItemClicked(int adapterPosition) {

        SharedPreferenceUtility.setChannelId(channelListAdapter.getItem(adapterPosition)
                .getChannelId());
        SharedPreferenceUtility.setChannelTimeZone(channelListAdapter.getItem(adapterPosition).getTimezone());
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelListAdapter.getItem(adapterPosition)
                .getChannelId());

    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, item.getResourceId());
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private AnimationItem[] getAnimationItems() {
        return new AnimationItem[]{
                new AnimationItem("Slide from bottom", R.anim.grid_layout_animation_from_bottom),
                new AnimationItem("Scale", R.anim.grid_layout_animation_scale),
                new AnimationItem("Scale random", R.anim.grid_layout_animation_scale_random)
        };
    }

    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
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
