package com.happi.android;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.happi.android.common.HappiApplication;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.ChannelListAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
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

public class ChannelsListingActivity extends BaseActivity implements ChannelListAdapter.itemClickListener {

    ImageView iv_menu, iv_back, iv_search, iv_logo_text;
    TypefacedTextViewRegular tv_title;
   // private AnimationItem[] mAnimationItems;
  //  private AnimationItem mSelectedItem;
  //  private CompositeDisposable compositeDisposable;
    public String VIDEO_ID;

    //AFTER REMOVING YT
    GridRecyclerView rv_channel_list;
    ChannelListAdapter channelListAdapter;
    SkeletonScreen loadingChannels;
    private CompositeDisposable compositeDisposable;
    private AnimationItem mSelectedItem;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;





 //   TabLayout tabLayout;
  //  ViewPager viewPager;
   // OtherChannelFragment otherfragment = new OtherChannelFragment();
   // Youtubefragment youtubefragment = new Youtubefragment();

 //   public RelativeLayout rl_ytplayer;
 //   public RelativeLayout rl_tablayout;
 //   public boolean playVideo = false;
 //   public boolean liveFrag = false;

/*
    public void playYtVideo() {
        if (playVideo) {
            rl_ytplayer.setVisibility(View.VISIBLE);
            Fragment ytchannelfragment = new YoutubeChannelFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.youtube_fragment, ytchannelfragment).commit();
        }
    }
*/

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
        setContentView(R.layout.activity_channels);

       // mAnimationItems = getAnimationItems();
      //  mSelectedItem = mAnimationItems[0];

        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
        iv_search = findViewById(R.id.iv_search);

        //after removing yt
        compositeDisposable = new CompositeDisposable();
        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];
        tv_errormsg = findViewById(R.id.tv_errormsg);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        rv_channel_list = findViewById(R.id.rv_channel_list);






        iv_menu.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_logo_text.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.channels);
        iv_search.setVisibility(View.GONE);


        compositeDisposable = new CompositeDisposable();
        //Yt
        //   rl_ytplayer = findViewById(R.id.rl_ytplayer);
        // tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //  viewPager = (ViewPager) findViewById(R.id.view_pager);
        //  tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        iv_back.setOnClickListener(v -> {

            //backPressed
            ChannelsListingActivity.super.onBackPressed();
        });

    //    setupViewPager(viewPager);
/*
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 1) {
                    rl_ytplayer.setVisibility(View.GONE);

                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
*/
        setupRecyclerview();

    }


/*
    private void setupViewPager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(
                HappiApplication.getCurrentContext(), getSupportFragmentManager(), tabLayout.getTabCount());
        adapter.addFragment(otherfragment, "Live Channels");
        adapter.addFragment(youtubefragment, "YouTube Channels");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
*/












    @Override
    public void onChannelItemClicked(int adapterPosition) {
        SharedPreferenceUtility.setChannelId(channelListAdapter.getItem(adapterPosition)
                .getChannelId());
        SharedPreferenceUtility.setChannelTimeZone(channelListAdapter.getItem(adapterPosition).getTimezone());
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelListAdapter.getItem(adapterPosition)
                .getChannelId());

    }

/*
    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, item.getResourceId());
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
*/

/*
    private AnimationItem[] getAnimationItems() {
        return new AnimationItem[]{
                new AnimationItem("Slide from bottom", R.anim.grid_layout_animation_from_bottom),
                new AnimationItem("Scale", R.anim.grid_layout_animation_scale),
                new AnimationItem("Scale random", R.anim.grid_layout_animation_scale_random)
        };
    }
*/


    @Override
    protected void onResume() {
        super.onResume();
        HappiApplication.setCurrentContext(this);
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

    //after removing yt
    private AnimationItem[] getAnimationItems() {
        return new AnimationItem[]{
                new AnimationItem("Slide from bottom", R.anim.grid_layout_animation_from_bottom),
                new AnimationItem("Scale", R.anim.grid_layout_animation_scale),
                new AnimationItem("Scale random", R.anim.grid_layout_animation_scale_random)
        };
    }
    private void setupRecyclerview() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        rv_channel_list.setLayoutManager(new GridLayoutManager(HappiApplication.getCurrentContext(), 3));
        rv_channel_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        channelListAdapter = new ChannelListAdapter(HappiApplication.getCurrentContext(),
                this::onChannelItemClicked, true, width);
        rv_channel_list.setAdapter(channelListAdapter);

        loadingChannels = Skeleton.bind(rv_channel_list)
                .adapter(channelListAdapter)
                .load(R.layout.loading_channel_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

        loadChannelList();
    }
    private void loadChannelList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.getChannels(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelresponse -> {

                    if (channelresponse.getData().isEmpty()) {

                        displayErrorLayout(getString(R.string.no_results_found));
                    } else {

                        updateChannelList(channelresponse.getData());
                    }
                }, throwable -> displayErrorLayout(getString(R.string.server_error)));
        compositeDisposable.add(videoDisposable);
    }
    private void updateChannelList(List<ChannelModel> data) {
        channelListAdapter.clearAll();
        channelListAdapter.addAll(data);
        loadingChannels.hide();
        runLayoutAnimation(rv_channel_list, mSelectedItem);
        if (channelListAdapter.isEmpty()) {
            rv_channel_list.setVisibility(View.GONE);
            tv_errormsg.setVisibility(View.VISIBLE);
            iv_errorimg.setVisibility(View.VISIBLE);
            tv_errormsg.setText(getString(R.string.no_channels));
        }
    }
    private void displayErrorLayout(String message) {
        rv_channel_list.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.VISIBLE);
        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }
    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, item.getResourceId());
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
