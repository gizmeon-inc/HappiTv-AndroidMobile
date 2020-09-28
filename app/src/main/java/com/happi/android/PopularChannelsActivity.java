package com.happi.android;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.happi.android.common.HappiApplication;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.happi.android.adapters.ChannelSuggestionAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelListResponse;
import com.happi.android.models.ChannelModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PopularChannelsActivity extends BaseActivity implements ChannelSuggestionAdapter.SuggesteditemClickListener {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu, iv_back, iv_logo_text, iv_search;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_channels_list;
    ChannelSuggestionAdapter channelsListAdapter;
    private AnimationItem mSelectedItem;
    private Disposable internetDisposable;
    private CompositeDisposable compositeDisposable;
    SkeletonScreen loadingChannels;

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
        setContentView(R.layout.activity_popular_channels);

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        tv_title = findViewById(R.id.tv_title);
        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        iv_search = findViewById(R.id.iv_search);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.trending_channels);
        iv_back.setVisibility(View.VISIBLE);
        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        rv_channels_list = findViewById(R.id.rv_channel_list);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);

        compositeDisposable = new CompositeDisposable();
        setupRecyclerview();

        iv_back.setOnClickListener(v ->
                PopularChannelsActivity.super.onBackPressed());
    }

    private void setupRecyclerview() {

        rv_channels_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_channels_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        channelsListAdapter = new ChannelSuggestionAdapter(getApplicationContext(),
                this::onSuggestedItemClicked, true);
        rv_channels_list.setAdapter(channelsListAdapter);
        loadingChannels = Skeleton.bind(rv_channels_list)
                .adapter(channelsListAdapter)
                .load(R.layout.loading_channel_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();


        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    if (isConnected) loadSuggestedChannels();
                });
    }

    private void loadSuggestedChannels() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable =
                usersService.PopularChannels(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ChannelListResponse>() {
                    @Override
                    public void accept(ChannelListResponse channelListResponse) {

                        if (channelListResponse.getData().size() != 0) {

                            populateSuggestion(channelListResponse.getData());
                        } else {

                            displayErrorLayout(getString(R.string.no_results_found));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout(getString(R.string.server_error));
                    }
                });
        compositeDisposable.add(videoDisposable);
    }

    private void populateSuggestion(List<ChannelModel> data) {

        channelsListAdapter.clearAll();
        channelsListAdapter.addAll(data);
        loadingChannels.hide();
        channelsListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_channels_list,mSelectedItem);
        if (channelsListAdapter.isEmpty()) {

            rv_channels_list.setVisibility(View.GONE);
        }

    }


    private void displayErrorLayout(String message) {
        rv_channels_list.setVisibility(View.GONE);

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }

    @Override
    public void onSuggestedItemClicked(int adapterPosition) {

        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelsListAdapter.getItem(adapterPosition).getChannelId());
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);

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
        super.onResume();
        HappiApplication.setCurrentContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(internetDisposable, compositeDisposable);
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
