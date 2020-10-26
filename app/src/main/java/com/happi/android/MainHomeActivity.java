package com.happi.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.gson.JsonObject;
import com.happi.android.adapters.CategoriesHomeListAdapter;
import com.happi.android.adapters.CategoryCircleViewAdapter;
import com.happi.android.adapters.CategoryListAdapter;
import com.happi.android.adapters.ChannelListAdapter;
import com.happi.android.adapters.ChannelSuggestionAdapter;
import com.happi.android.adapters.LiveScheduleHomeListAdapter;
import com.happi.android.adapters.LiveScheduleInfoAdapter;
import com.happi.android.adapters.PartnersListingAdapter;
import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.adapters.ShowsAdapter;
import com.happi.android.adapters.VideoList_adapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.AdvertisingIdClient;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.ScrollingLinearLayoutManager;
import com.happi.android.customviews.SpacesItemDecoration;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.exoplayercontroller.EventLogger;
import com.happi.android.exoplayercontroller.TrackSelectionHelper;
import com.happi.android.models.CategoriesHomeListVideoModel;
import com.happi.android.models.CategoryModel;
import com.happi.android.models.ChannelModel;
import com.happi.android.models.LiveScheduleResponse;
import com.happi.android.models.PartnerResponseModel;
import com.happi.android.models.ShowModel;
import com.happi.android.models.TokenResponse;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.AnalyticsApi;
import com.happi.android.webservice.ApiClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainHomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, CustomAlertDialog.OnOkClick,
        VideoList_adapter.itemClickListener,
        ShowList_adapter.itemClickListener,
        ChannelSuggestionAdapter.SuggesteditemClickListener, CategoryListAdapter.itemClickListener,
        ShowsAdapter.itemClickListener, ChannelListAdapter.itemClickListener,
        RewardedVideoAdListener, CategoriesHomeListAdapter.ICallAdmobAd,
        CategoryCircleViewAdapter.itemClickListenerForCategory,
        ChannelListAdapter.RedirectToLive, PartnersListingAdapter.PartnerItemClickListener {


    VideoList_adapter videoList_adapter;
    ShowList_adapter freeShowList_adapter;
    CategoryListAdapter categoryList_adapter;
    ChannelSuggestionAdapter channelListAdapter;
    ChannelListAdapter liveChannelsAdapter;
    private CompositeDisposable compositeDisposable;
    //error layout
    private LinearLayout ll_error_home;
    private TypefacedTextViewRegular tv_errormsg;
    private ImageView iv_errorimg;

    SkeletonScreen loadingVideos, loadingCategories, loadingLive, loadingFreeShows, loadingLiveSchedule, loadingPartnerInfo;
    LinearLayout ll_category_list, ll_popular_videos,
            ll_popular_live, ll_watch_free;
    GridRecyclerView rv_video_grid, rv_category_list, rv_categories_home_list, rv_live, rv_watch_free;

    ImageView iv_search;
    private AnimationItem mSelectedItem;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean homeLoaded = false;

    //get user subscription id list
    private List<UserSubscriptionModel> subscriptionModelList;
    private List<String> subids;


    //RewardedVideoAd and InterstitialAd variables
    private RewardedVideoAd mRewardedVideoAd;
    private int commonVideoOrChannelId = 0;

    private String showId = "empty";

    int apiErrorCount = 0;

    CategoryCircleViewAdapter circleViewAdapter;

    public int channelId = 0;
    public boolean isRedirectToLive = false;
    public int uniqueId = 0;
    //display metrics
    private static int width;
    private static int height;
    private static WifiManager wifiManager;
    private static ConnectivityManager connectivityManager;
    private static SharedPreferences sharedPreferences;

    //live player
    private TypefacedTextViewBold tv_channel_title_live;
    private List<ChannelModel> channelModelList;
    private RelativeLayout rl_player_live;
    private PlayerView exo_player_view_home;
    private ProgressBar pb_live;
    private LinearLayout ll_live_guide;
    private GridRecyclerView rv_live_schedule_list;
    private SimpleExoPlayer exoPlayer;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private EventLogger eventLogger;
    private LiveScheduleHomeListAdapter liveScheduleHomeListAdapter;
    private LiveScheduleInfoAdapter liveScheduleInfoAdapter;
    private int liveChannelId = 0;
    //partner
    private LinearLayout ll_partner;
    private GridRecyclerView rv_partner;
    private PartnersListingAdapter partnersListingAdapter;
    //nested scroll
    private NestedScrollView sv_scrollview;
    private ProgressDialog dialog;
    private static Context context;

    private boolean shouldAutoPlay = true;
    private boolean isMuted = false;
    private float volume = 0;
    //partner autoscroll
    private LinearLayoutManager layoutManager;
    private SwipeTask swipeTask;
    private Timer swipeTimer;
    private boolean isPartnerScroll = false;
    private CountDownTimer timerOrientation;
    private int currentItem = 0;

    //live analytics
    //analytics
    boolean  isLivePlaying = false;
    boolean isLivePaused = false;
    boolean isTimerActive = false;
    boolean durationSet = false;
    private Timer timerSChedule = null;
    private String channel_Id = "";
    private String channelTitle = "";


    @Override

    public void onClick(View view) {
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_home_sample);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();

        if (getIntent() != null && HappiApplication.isIsFromLink()) {
            if (getIntent().getStringExtra("show") != null && !getIntent().getStringExtra("show").isEmpty()) {
                showId = getIntent().getStringExtra("show");
            } else {
                showId = "empty";
            }
        } else {
            showId = "empty";
        }
        if (getIntent() != null) {
            isRedirectToLive = getIntent().getBooleanExtra("notification", false);
            if (isRedirectToLive) {
                channelId = getIntent().getIntExtra(ConstantUtils.CHANNEL_ID, 0);
                uniqueId = getIntent().getIntExtra(ConstantUtils.UNIQUE_ID, 0);
            }
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        sharedPreferences = getSharedPreferences(HappiApplication.getCurrentContext().getString(R.string.USER_PREFERENCES),
                Context.MODE_PRIVATE);
        getDisplayMetrics();

        compositeDisposable = new CompositeDisposable();

        context = this;

        //progress dialog
        dialog = new ProgressDialog(this, R.style.MyTheme);
        dialog.setCancelable(false);

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        onCreateDrawer();

        RelativeLayout rl_exoplayer_parent = findViewById(R.id.rl_exoplayer_parent);
        RelativeLayout rl_toolbar = findViewById(R.id.rl_toolbar);


        //error layout
        ll_error_home = findViewById(R.id.ll_error_home);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        ll_error_home.setVisibility(View.GONE);

        rv_watch_free = findViewById(R.id.rv_watch_free);
        rv_live = findViewById(R.id.rv_live);
        rv_video_grid = findViewById(R.id.rv_video_grid);
        rv_category_list = findViewById(R.id.rv_category_list);
        rv_categories_home_list = findViewById(R.id.rv_categories_home_list);
        ll_category_list = findViewById(R.id.ll_category_list);
        ll_popular_videos = findViewById(R.id.ll_popular_videos);
        ll_watch_free = findViewById(R.id.ll_watch_free);
        ll_popular_live = findViewById(R.id.ll_popular_live);
        ll_popular_live = findViewById(R.id.ll_popular_live);
        iv_search = findViewById(R.id.iv_search);

        //no free shows
        ll_watch_free.setVisibility(View.GONE);
        rv_watch_free.setVisibility(View.GONE);
        //no live category in home
        rv_live.setVisibility(View.GONE);
        ll_popular_live.setVisibility(View.GONE);
        //no category
        ll_category_list.setVisibility(View.GONE);
        rv_category_list.setVisibility(View.GONE);

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setDistanceToTriggerSync(250);


        //get user subscription id list
        subscriptionModelList = new ArrayList<>();
        subids = new ArrayList<>();


        //live player
        channelModelList = new ArrayList<>();
        rl_player_live = findViewById(R.id.rl_player_live);
        pb_live = findViewById(R.id.pb_live);
        exo_player_view_home = findViewById(R.id.exo_player_view_home);
        ll_live_guide = findViewById(R.id.ll_live_guide);
        rv_live_schedule_list = findViewById(R.id.rv_live_schedule_list);

        tv_channel_title_live = exo_player_view_home.findViewById(R.id.tv_channel_title_live);
        tv_channel_title_live.setVisibility(View.VISIBLE);
        tv_channel_title_live.setSelected(true);

        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.default_spacing_small);
        //rv_live_schedule_list.addItemDecoration(new SpacesItemDecoration(spacingPixels));

        //partner info
        ll_partner = findViewById(R.id.ll_live_partner);
        rv_partner = findViewById(R.id.rv_live_schedule_partner);
        int spacingPixelsPartner = getResources().getDimensionPixelSize(R.dimen.default_spacing_1dp);
        rv_partner.addItemDecoration(new SpacesItemDecoration(spacingPixelsPartner));
        //layoutManager = new ScrollingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false, 100);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        //scroll view
        sv_scrollview = findViewById(R.id.sv_scrollview);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        Log.v("okhttp", "total width >>" + width);
        apiErrorCount = 0;
        homeLoaded = false;
        setupRecyclerView();
        getSessionToken();

        /*mSwipeRefreshLayout.setOnRefreshListener(() -> {

         *//* mSwipeRefreshLayout.setRefreshing(false);
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);*//*

        });*/

        iv_search.setOnClickListener(v -> {
            releasePlayer();
            Intent intent = new Intent(MainHomeActivity.this, SearchActivity.class);
            intent.putExtra("search_type", "show");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        /*ll_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releasePlayer();
                Intent intent = new Intent(MainHomeActivity.this, PartnerListingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        });*/
        ll_watch_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releasePlayer();
                Intent intent = new Intent(MainHomeActivity.this, PopularVideosActivity.class);
                intent.putExtra("title", "watchFree");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);


                overridePendingTransition(0, 0);
            }
        });
        ll_popular_live.setOnClickListener(v -> {
            releasePlayer();
            Intent intent = new Intent(HappiApplication.getCurrentContext(), LiveVideoListingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        ll_category_list.setOnClickListener(v -> {
            releasePlayer();
            Intent intent = new Intent(MainHomeActivity.this, CategoriesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            overridePendingTransition(0, 0);
        });

        ll_popular_videos.setOnClickListener(v -> {
            releasePlayer();
            Intent intent = new Intent(MainHomeActivity.this, PopularVideosActivity.class);
            intent.putExtra("title", "newRelease");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            overridePendingTransition(0, 0);
        });


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        HappiApplication.setCurrentContext(this);
        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
        if (getMenuItem() != R.id.item_home) {
            updateMenuItem(0);
        }
        setUserName();
        setLogoutAllVisibility();

        if (apiErrorCount == 4) {
            // if (!homeLoaded) {
            setupRecyclerView();
            recallHomeApis();
        }


        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
        //analytics
        if (!SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            setSessionId();
            firstTimeInstallAnalyticsApiCall(context);
            appLaunchAnalyticsApiCall();
        } else {
            new AdvertisingIdAsyncTask().execute();
        }

        shouldAutoPlay = true;
        resumePlayer();
        if (liveChannelId != 0) {
            Log.e("HOME", "ONRESUME");
            loadLiveSchedule(liveChannelId);
        }
        Log.e("CARS", "onresume");
        if (rv_partner != null && partnersListingAdapter != null && !partnersListingAdapter.isEmpty() && !isPartnerScroll) {
            Log.e("CARS", "onresume:play cars");
            currentItem = 0;
            rv_partner.scrollToPosition(currentItem);
            startTimer();
        }
        super.onResume();
    }

    private void setupRecyclerView() {
        //no free shows
        ll_watch_free.setVisibility(View.GONE);
        rv_watch_free.setVisibility(View.GONE);
        //no live category in home
        rv_live.setVisibility(View.GONE);
        ll_popular_live.setVisibility(View.GONE);
        //no category
        ll_category_list.setVisibility(View.GONE);
        rv_category_list.setVisibility(View.GONE);

        pb_live.setVisibility(View.VISIBLE);
        ll_error_home.setVisibility(View.GONE);
        sv_scrollview.setScrollX(0);
        sv_scrollview.setScrollY(0);
        //----------------------------------------live schedule-------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_live_schedule_list, false);
        rv_live_schedule_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        liveScheduleInfoAdapter = new LiveScheduleInfoAdapter(this, width);
        //liveScheduleHomeListAdapter = new LiveScheduleHomeListAdapter(this);
        //rv_live_schedule_list.setAdapter(liveScheduleHomeListAdapter);
        rv_live_schedule_list.setAdapter(liveScheduleInfoAdapter);
        loadingLiveSchedule = Skeleton.bind(rv_live_schedule_list)
                //.adapter(liveScheduleHomeListAdapter)
                .adapter(liveScheduleInfoAdapter)
                .load(R.layout.item_live_schedule_info_home_skeleton)
                //.load(R.layout.loading_videos_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
        //-------------------------------------------------partner info-------------------------------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_partner, false);
        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_partner.setLayoutManager(layoutManager);
        partnersListingAdapter = new PartnersListingAdapter(this, true, this::onPartnerItemClicked, width);
        rv_partner.setAdapter(partnersListingAdapter);
        loadingPartnerInfo = Skeleton.bind(rv_partner)
                .adapter(partnersListingAdapter)
                //.load(R.layout.item_partner_info_home_skeleton)
                .load(R.layout.item_live_schedule_home_skeleton)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
            /*int duration = 10;
            int pixelsToMove = 40;
            Handler mHandler = new Handler(Looper.getMainLooper());
            Runnable SCROLLING_RUNNABLE = new Runnable() {

                @Override
                public void run() {
                    rv_partner.smoothScrollBy(pixelsToMove, 0);
                    mHandler.postDelayed(this, duration);
                }
            };
            rv_partner.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    if(lastItem == layoutManager.getItemCount()-1){
                        mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                        Handler postHandler = new Handler();
                        postHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rv_partner.setAdapter(null);
                                rv_partner.setAdapter(partnersListingAdapter);
                                mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);
                            }
                        }, 2000);
                    }
                }
            });
            mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);*/

        //---------------------------------------------------category-----------------------------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_category_list, false);
        rv_category_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList_adapter = new CategoryListAdapter(this,
                this::onCategoryItemClicked, false);

        circleViewAdapter = new CategoryCircleViewAdapter(this, this::onCategoryItemClickedForCircleView, false);
        rv_category_list.setAdapter(circleViewAdapter);

        loadingCategories = Skeleton.bind(rv_category_list)
                .adapter(circleViewAdapter)
                .load(R.layout.item_category_circle_loading)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
        //-----------------------------------------------watch for free---------------------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_watch_free, false);
        /*ll_watch_free.setVisibility(View.VISIBLE);
        rv_watch_free.setVisibility(View.VISIBLE);
*/
        rv_watch_free.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        freeShowList_adapter = new ShowList_adapter(this, this::onShowsItemClicked, false);
        rv_watch_free.setAdapter(freeShowList_adapter);
        loadingFreeShows = Skeleton.bind(rv_watch_free)
                .adapter(freeShowList_adapter)
                .load(R.layout.loading_videos_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

        //-------------------------------------------New Release-----------------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_video_grid, false);
        rv_video_grid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videoList_adapter = new VideoList_adapter(this, this::onItemClicked, false, true, width);
        rv_video_grid.setAdapter(videoList_adapter);

        loadingVideos = Skeleton.bind(rv_video_grid)
                .adapter(videoList_adapter)
                .load(R.layout.loading_videos_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();


        //-----------------------------------------live----------------------------------------------------//
        ViewCompat.setNestedScrollingEnabled(rv_live, false);
        rv_live.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        liveChannelsAdapter = new ChannelListAdapter(this,
                this::onChannelItemClicked, false, MainHomeActivity.this, this::onRedirectionToLive);
        rv_live.setAdapter(liveChannelsAdapter);

        loadingLive = Skeleton.bind(rv_live)
                .adapter(liveChannelsAdapter)
                .load(R.layout.loading_channel_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

    }

    private void getSessionToken() {
        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();


        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), appKey, bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {

                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());                            //  HappiApplication.setPublisher_id(sessionTokenResponseModel.getPublisher_id());


                            //Test AdIDs
                            //SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-3940256099942544/6300978111", "ca-app-pub-3940256099942544/5224354917","ca-app-pub-3940256099942544/1033173712","ca-app-pub-3940256099942544~3347511713", "1", "0","24534e1901884e398f1253216226017e","b195f8dd8ded45fe847ad89ed1d016da","0");
                            //Live AdIDs
                            //SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-1674238972502360/1600095046", "ca-app-pub-1674238972502360/8688247573","ca-app-pub-1674238972502360/4202207657");
                            //SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(),sessionTokenResponseModel.getInterstitial_id(),sessionTokenResponseModel.getApp_id(),sessionTokenResponseModel.getRewarded_status(),sessionTokenResponseModel.getInterstitial_status(),sessionTokenResponseModel.getMopub_interstitial_id(),sessionTokenResponseModel.getMopub_banner_id(),"0");

                            SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(), sessionTokenResponseModel.getInterstitial_id(), sessionTokenResponseModel.getApp_id(), sessionTokenResponseModel.getRewarded_status(), sessionTokenResponseModel.getInterstitial_status(), sessionTokenResponseModel.getMopub_interstitial_id(), sessionTokenResponseModel.getMopub_banner_id(), sessionTokenResponseModel.getMopub_interstitial_status(), sessionTokenResponseModel.getMopub_rect_banner_id());


                            if (!SharedPreferenceUtility.getGuest()) {
                                checkSubscription();
                            }
                            liveNow();
                            loadPartners();
                            //categoryApiCall();
                            //watchForFreeShowList();
                            newReleases();
                            loadCategoriesHomeList();


                        }, throwable -> {
                            // Log.w("KKK###","session");
                            Log.e("getSessionToken", "token error");
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void checkSubscription() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel -> {
                    if (subscriptionResponseModel.isForcibleLogout()) {
                        loginExceededAlertSubscription();
                    } else {
                        subids.clear();
                        if (subscriptionResponseModel.getData().size() != 0) {
                            subscriptionModelList = subscriptionResponseModel.getData();
                            for (UserSubscriptionModel item : subscriptionModelList) {
                                subids.add(item.getSub_id());
                            }
                        }
                        HappiApplication.setSub_id(subids);
                    }

                }, throwable -> {
                    // Toast.makeText(MainHomeActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }

    private void loginExceededAlertSubscription() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        releasePlayer();
        String message = "You are no longer Logged in this device. Please Login again to access.";
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(MainHomeActivity.this, "ok", message, "Ok", "", null, null, this::onOkClickNeutral, null);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void watchForFreeShowList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getFreeShowList(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showModelResponse -> {
                    if (showModelResponse.getShowModelList().size() != 0) {

                        if (showModelResponse.getShowModelList().size() >= 10) {

                            updateFreeShowList(showModelResponse.getShowModelList().subList(0, 10));
                        } else {

                            updateFreeShowList(showModelResponse.getShowModelList());
                        }
                    } else {
                        ll_watch_free.setVisibility(View.GONE);
                        rv_watch_free.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    apiErrorCount++;
                });
        compositeDisposable.add(showDisposable);
    }

    private void newReleases() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.NewReleases(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoResponse -> {

                    if (videoResponse.getShowModelList().size() != 0) {

                        if (videoResponse.getShowModelList().size() >= 10) {

                            updateVideoDataList(videoResponse.getShowModelList().subList(0, 10));
                        } else {

                            updateVideoDataList(videoResponse.getShowModelList());
                        }
                    } else {

                        ll_popular_videos.setVisibility(View.GONE);
                        rv_video_grid.setVisibility(View.GONE);
                    }


                }, throwable -> {
                    apiErrorCount++;
                });
        compositeDisposable.add(videoDisposable);
    }

    private void liveNow() {
        pb_live.setVisibility(View.VISIBLE);
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable liveDisposable = usersService.getChannels(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelListResponse -> {
                    List<ChannelModel> channelModels = new ArrayList<>();
                    if (channelListResponse.getData().size() != 0) {
                        channelModels.addAll(channelListResponse.getData());
                    }
                    if (!channelModelList.isEmpty()) {
                        channelModelList.clear();
                    }
                    channelModelList.addAll(channelModels);

                    //load all live - player and channels list
                    if (!channelModelList.isEmpty()) {
                        liveChannelId = channelModelList.get(0).getChannelId();
                        Log.e("HOME", "GETALLCHANNEL");
                        loadLiveSchedule(liveChannelId);
                        generateToken(channelModelList.get(0));

                        /*//load live channel list
                        if (channelModelList.size() >= 10) {
                            updateLiveVideos(channelModelList.subList(0, 10));
                        } else {
                            updateLiveVideos(channelModelList);
                        }*/

                    } else {
                        //hideLivePlayerAndSchedule();
                        pb_live.setVisibility(View.GONE);
                        ll_live_guide.setVisibility(View.GONE);
                        rv_live_schedule_list.setVisibility(View.GONE);

                        rv_live.setVisibility(View.GONE);
                        ll_popular_live.setVisibility(View.GONE);

                    }


                }, throwable -> {
                    apiErrorCount++;
                });
        compositeDisposable.add(liveDisposable);
    }

    private void generateToken(ChannelModel channelModel) {

        pb_live.setVisibility(View.VISIBLE);
        ApiClient.UsersService usersService = ApiClient.createToken();
        Disposable tokenDisposable = usersService.getVideoToken(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenResponse>() {
                    @Override
                    public void accept(TokenResponse tvexcelResponse) {
                        String token = tvexcelResponse.getData().trim();
                        initializePlayer(channelModel, token);
                    }
                }, throwable -> {
                    liveError();
                });
        compositeDisposable.add(tokenDisposable);

    }

    private void loadLiveSchedule(int channelId) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable = usersService.getLiveSchedule(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(), channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveScheduleResponse -> {

                    if (liveScheduleResponse.getData().size() != 0) {
                        updateLiveScheduleList(liveScheduleResponse.getData());
                    } else {
                        ll_live_guide.setVisibility(View.GONE);
                        rv_live_schedule_list.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    apiErrorCount++;
                    ll_live_guide.setVisibility(View.GONE);
                    rv_live_schedule_list.setVisibility(View.GONE);
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void loadPartners() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable = usersService.getPartnerList(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(partnerResponseModel -> {

                    if (partnerResponseModel.getData().size() != 0) {
                        updatePartnersList(partnerResponseModel.getData());
                    } else {
                        ll_partner.setVisibility(View.GONE);
                        rv_partner.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    apiErrorCount++;
                    ll_partner.setVisibility(View.GONE);
                    rv_partner.setVisibility(View.GONE);
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void updatePartnersList(List<PartnerResponseModel.PartnerModel> partnerModelList) {
        partnersListingAdapter.clearAll();
        partnersListingAdapter.addAll(partnerModelList);
        loadingPartnerInfo.hide();
        SnapHelper snapHelper1;
        snapHelper1 = new GravitySnapHelper(Gravity.START);
        snapHelper1.attachToRecyclerView(rv_partner);
        partnersListingAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_partner, mSelectedItem);
        if (partnersListingAdapter.isEmpty()) {

            ll_partner.setVisibility(View.GONE);
            rv_partner.setVisibility(View.GONE);
        } else {
            isPartnerScroll = true;
            Log.e("CARS", "partnload: play cars");
            Log.e("CARS", "count " + partnersListingAdapter.getItemCount());
            //playCarousel();
            currentItem = 0;
            rv_partner.scrollToPosition(currentItem);
            //playCarousel();
            startTimer();
        }
    }

    private void updateLiveScheduleList(List<LiveScheduleResponse.LiveScheduleModel> liveScheduleModelList) {

        liveScheduleInfoAdapter.clearAll();
        liveScheduleInfoAdapter.addAll(liveScheduleModelList);
        loadingLiveSchedule.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rv_live_schedule_list);
        liveScheduleInfoAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_live_schedule_list, mSelectedItem);
        if (liveScheduleInfoAdapter.isEmpty()) {

            ll_live_guide.setVisibility(View.GONE);
            rv_live_schedule_list.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(ChannelModel liveModel, String playerToken) {
        MediaSource videoSource = null;

        pb_live.setVisibility(View.GONE);
        try {
            Uri videoURI = Uri.parse(liveModel.getLiveLink().trim());
            // Uri videoURI = Uri.parse("https://content.uplynk.com/channel/e1e04b2670174e93b5d5499ee73de095.m3u8");
            // Uri videoURI = Uri.parse("https://gizmeon.s.llnwi.net/vod/PUB-50023/202009291601356793/playlist~360p.m3u8");

            boolean needNewPlayer = exoPlayer == null;

            if (needNewPlayer) {
                //boolean shouldAutoPlay = true;
                TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                DefaultTrackSelector trackSelector1 = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
                TrackSelectionHelper trackSelectionHelper = new TrackSelectionHelper(trackSelector1, adaptiveTrackSelectionFactory);
                EventLogger eventLogger = new EventLogger(trackSelector1);
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
                boolean preferExtensionDecoders = false;
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF); // drmSessionManager = null
                exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
                exoPlayer.addListener(eventLogger);
                exoPlayer.addMetadataOutput(eventLogger);
                exoPlayer.addAudioDebugListener(eventLogger);
                exoPlayer.addVideoDebugListener(eventLogger);
                exo_player_view_home.setPlayer(exoPlayer);
                exoPlayer.setPlayWhenReady(shouldAutoPlay);
                //exo_player_view_home.findViewById(R.id.exo_fullscreen_button).setVisibility(View.GONE);
                FrameLayout exo_fullscreen_button = exo_player_view_home.findViewById(R.id.exo_fullscreen_button);
                exo_fullscreen_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        releasePlayer();
                        SharedPreferenceUtility.setChannelId(liveModel.getChannelId());
                        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, liveModel.getChannelId());
                        overridePendingTransition(0, 0);
                    }
                });
                volume = exoPlayer.getVolume();


                FrameLayout fl_volume = exo_player_view_home.findViewById(R.id.fl_volume);
                ImageView iv_volume = exo_player_view_home.findViewById(R.id.iv_volume);

                isMuted = false;
                iv_volume.setImageResource(R.drawable.ic_volume);

                fl_volume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isMuted) {
                            Log.e("VOL", "muted");
                            isMuted = false;
                            exoPlayer.setVolume(volume);
                            iv_volume.setImageResource(R.drawable.ic_volume);
                        } else {
                            Log.e("VOL", "not muted");
                            isMuted = true;
                            volume = exoPlayer.getVolume();
                            exoPlayer.setVolume(0);
                            iv_volume.setImageResource(R.drawable.ic_mute);
                        }
                    }
                });


                // volume = exoPlayer.getVolume();
            }
            DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
            String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
            DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeterA);
            factory.getDefaultRequestProperties().set("token", playerToken);

            videoSource = new HlsMediaSource.Factory(factory).createMediaSource(videoURI);
            //videoSource = new HlsMediaSource(videoURI, factory,1, null,null);

            try{



                exoPlayer.addListener(new Player.EventListener() {

                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                        try{
                            if (manifest instanceof HlsManifest) {
                                HlsManifest hlsManifest = (HlsManifest) manifest;
                                HlsMediaPlaylist hlsMediaPlaylist = hlsManifest.mediaPlaylist;
                                if (hlsMediaPlaylist != null) {
                                    List<String> list = hlsMediaPlaylist.tags;
                                    for (String item : list) {
                                        if (item.contains("#EXTINF:")) {
                                            String info = "";
                                            info = item.replace("#EXTINF:", "");
                                            if( !info.isEmpty() && info.contains(",")){
                                                String[] titleList = info.split(",");
                                                if(titleList.length != 0 && titleList.length > 1 ){
                                                    if (!tv_channel_title_live.getText().equals(titleList[1])) {
                                                        tv_channel_title_live.setText(titleList[1]);
                                                    }
                                                }else{
                                                    tv_channel_title_live.setText("");
                                                }

                                            }

                                        }
                                    }
                                }

                            }
                        }catch(Exception e){
                            Log.e("LIVE##", "exception in LIVE : " + e.toString());
                        }
                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if(playbackState == Player.STATE_ENDED){
                            try{
                                isLivePlaying = false;
                                isLivePaused = false;
                                if(timerSChedule != null){
                                    isTimerActive = false;
                                    timerSChedule.cancel();
                                }
                            }catch(Exception ex){
                                Log.e("QWERTY5","EXCEPTION : END : "+ex.toString());
                            }

                        }else if(playWhenReady && playbackState == Player.STATE_READY){
                            try{
                                isLivePlaying = true;
                                isLivePaused = false;
                                if(!durationSet) {
                                    durationSet = true;
                                    // timerVideoHandler.post(timeRunnable);
                                    liveEventAnalyticsApiCall("POP02");
                                }
                                if(isLivePlaying){
                                    if(!isTimerActive) {
                                        initializeTimerScheduler("POP03");
                                    }
                                }
                            }catch(Exception ex){
                                Log.e("QWERTY5","EXCEPTION : PLAYING : "+ex.toString());
                            }

                        }else if (playWhenReady){

                        }else{
                            try{
                                isLivePlaying = false;
                                isLivePaused = true;

                                //live paused
                                if (isLivePaused) {
                                    if(timerSChedule != null){
                                        isTimerActive = false;
                                        timerSChedule.cancel();
                                    }
                                    //no pause for live
                                    //liveEventAnalyticsApiCall("POP04");
                                }

                                /*//live pause analytics api call
                                if (isLivePaused) {
                                    isLivePaused = false;
                                    if (timerSChedule != null) {
                                        isTimerActive = false;
                                        timerSChedule.cancel();
                                    }
//                                    if (!isVideoEndT) {
                                    liveEventAnalyticsApiCall("POP04");
                                   // }
                                }*/
                            }catch(Exception ex){
                                Log.e("QWERTY5","EXCEPTION : PAUSE : "+ex.toString());
                            }

                        }

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });
            }catch(Exception e){
                Log.d("MANIFEST##","error: "+e.getMessage());
            }
        } catch (Exception e) {
            liveError();
            Log.e("MainAcvtivity error", " exoplayer error " + e.getMessage());
        }


        exoPlayer.prepare(videoSource);

        durationSet = false;
        isTimerActive = false;
        channel_Id = String.valueOf(liveModel.getChannelId());
        channelTitle = liveModel.getChannelName();
    }

    private void categoryApiCall() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.GetTheme(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        if (loginResponseModel.getData().size() != 0) {

                            if (loginResponseModel.getData().size() >= 10) {

                                updateCategoryList(loginResponseModel.getData().subList(0, 10));
                            } else {

                                updateCategoryList(loginResponseModel.getData());
                            }
                        } else {

                            ll_category_list.setVisibility(View.GONE);
                            rv_category_list.setVisibility(View.GONE);
                        }

                    }

                }, throwable -> {
                    apiErrorCount++;
                    Toast.makeText(MainHomeActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);

    }

    private void loadCategoriesHomeList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable homeVideoDisposable = usersService.GetHomeVideo(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categorisHomeListResponseModel -> {

                    if (categorisHomeListResponseModel.getCategoriesList().size() != 0) {

                        if (categorisHomeListResponseModel.getCategoriesList().size() >= 10) {
                            //  populateCategoryHomeList(categorisHomeListResponseModel.getCategoriesList().subList(0,10));
                            populateCategoryHomeList(categorisHomeListResponseModel
                                    .getCategoriesList());
                        } else {
                            populateCategoryHomeList(categorisHomeListResponseModel
                                    .getCategoriesList());
                        }
                    } else {
                        rv_categories_home_list.setVisibility(View.GONE);
                    }
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, throwable -> {
                    apiErrorCount++;
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(homeVideoDisposable);

        if (!showId.equalsIgnoreCase("empty") && HappiApplication.isIsFromLink()) {
            shouldAutoPlay = false;
            releasePlayer();

            HappiApplication.setIsFromLink(false);
            Intent show = new Intent(MainHomeActivity.this, ShowDetailsActivity.class);
            show.putExtra(ConstantUtils.SHOW_ID, showId);
            show.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(show);
            overridePendingTransition(0, 0);
            showId = "empty";
        }
    }

    private void recallHomeApis() {
        if (!HappiApplication.getAppToken().isEmpty()) {
//            if (liveScheduleHomeListAdapter == null || liveScheduleHomeListAdapter.isEmpty()) {
//                loadLiveSchedule(liveChannelId);
//            }
            if (liveScheduleInfoAdapter == null || liveScheduleInfoAdapter.isEmpty() && liveChannelId != 0) {
                loadLiveSchedule(liveChannelId);
            }
            if (partnersListingAdapter == null || partnersListingAdapter.isEmpty()) {
                loadPartners();
            }
           /* if (circleViewAdapter == null || circleViewAdapter.isEmpty()) {
                categoryApiCall();
            }*/
            /*if (freeShowList_adapter == null || freeShowList_adapter.isEmpty()) {
                watchForFreeShowList();
            }*/
            if (videoList_adapter == null || videoList_adapter.isEmpty()) {
                newReleases();
            }
            /*if (liveChannelsAdapter == null || liveChannelsAdapter.isEmpty()) {
                liveNow();
            }*/
            if (!homeLoaded) {
                loadCategoriesHomeList();
            }
        } else {
            getSessionToken();
        }
    }

    private void updateLiveVideos(List<ChannelModel> channelData) {

        liveChannelsAdapter.clearAll();
        liveChannelsAdapter.addAll(channelData);
        loadingLive.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rv_live);
        liveChannelsAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_live, mSelectedItem);
        if (liveChannelsAdapter.isEmpty()) {
            rv_live.setVisibility(View.GONE);
            ll_popular_live.setVisibility(View.GONE);
        }

        //return true;
    }

    private void updateVideoDataList(List<ShowModel> videoModelList) {

        videoList_adapter.clearAll();
        videoList_adapter.addAll(videoModelList);
        loadingVideos.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rv_video_grid);
        videoList_adapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_grid, mSelectedItem);
        if (videoList_adapter.isEmpty()) {

            ll_popular_videos.setVisibility(View.GONE);
            rv_video_grid.setVisibility(View.GONE);
        }
        //return true;
    }

    private void updateFreeShowList(List<ShowModel> showModelList) {

        freeShowList_adapter.clearAll();
        freeShowList_adapter.addAll(showModelList);
        loadingFreeShows.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rv_watch_free);
        freeShowList_adapter.notifyDataSetChanged();
        runLayoutAnimation(rv_watch_free, mSelectedItem);
        if (freeShowList_adapter.isEmpty()) {

            ll_watch_free.setVisibility(View.GONE);
            rv_watch_free.setVisibility(View.GONE);
        }

    }

    private void updateCategoryList(List<CategoryModel.Category> categories) {
        circleViewAdapter.clearAll();
        circleViewAdapter.addAll(categories);
        loadingCategories.hide();
        SnapHelper snapHelper;
        snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.attachToRecyclerView(rv_category_list);
        circleViewAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_category_list, mSelectedItem);

        if (circleViewAdapter.isEmpty()) {

            ll_category_list.setVisibility(View.GONE);
            rv_category_list.setVisibility(View.GONE);
        } else {
            homeLoaded = true;
        }

    }

    private void populateCategoryHomeList(List<CategoriesHomeListVideoModel> categoriesHomeListVideoModelList) {

        rv_categories_home_list.setHasFixedSize(true);
        CategoriesHomeListAdapter adapter = new CategoriesHomeListAdapter(categoriesHomeListVideoModelList, this, true, width);
        rv_categories_home_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_categories_home_list.setAdapter(adapter);
        homeLoaded = true;
        //return true;
    }


    private void displayErrorLayout(String message) {
        if (apiErrorCount == 4) {

            //hideLivePlayerAndSchedule();
            releaseExoPlayer();
            pb_live.setVisibility(View.GONE);
            rl_player_live.setVisibility(View.GONE);
            exo_player_view_home.setVisibility(View.GONE);
            ll_live_guide.setVisibility(View.GONE);
            rv_live_schedule_list.setVisibility(View.GONE);


            rv_watch_free.setVisibility(View.GONE);
            rv_video_grid.setVisibility(View.GONE);
            rv_category_list.setVisibility(View.GONE);
            rv_live.setVisibility(View.GONE);
            rv_categories_home_list.setVisibility(View.GONE);

            ll_watch_free.setVisibility(View.GONE);
            ll_category_list.setVisibility(View.GONE);
            ll_popular_videos.setVisibility(View.GONE);
            ll_popular_live.setVisibility(View.GONE);

            //display error layout
            ll_error_home.setVisibility(View.VISIBLE);
            iv_errorimg.setVisibility(View.VISIBLE);
            tv_errormsg.setVisibility(View.VISIBLE);
            tv_errormsg.setText(message);
        }
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
                new AnimationItem("Fall down", R.anim.layout_animation_fall_down),
                new AnimationItem("Slide from right", R.anim.layout_animation_from_right),
                new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom)
        };
    }

    @Override
    protected void onPause() {
        shouldAutoPlay = false;
        releasePlayer();
        if (swipeTimer != null) {
            Log.e("CARS", "onpause:timer cancel");
            isPartnerScroll = false;
            swipeTimer.cancel();
        }
        if (null != swipeTask) {
            isPartnerScroll = false;
            Log.e("CARS", "onpause: swipeTask canc");
            swipeTask.cancel();
        }
        if(timerOrientation != null){
            timerOrientation.cancel();
        }
        currentItem = 0;
        super.onPause();
    }

    public void onBackPressed() {

        if (isDrawerOpened) {

            drawer.closeDrawer(GravityCompat.START);
        } else {

            AppUtils.exitApp();
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
        if(timerSChedule != null) {
            timerSChedule.cancel();
        }
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        if (swipeTimer != null) {
            isPartnerScroll = false;
            Log.e("CARS", "onDestroy: tmr canc");
            swipeTimer.cancel();
        }
        if (null != swipeTask) {
            isPartnerScroll = false;
            Log.e("CARS", "onDestroy: swipeTask canc");
            swipeTask.cancel();
        }
        if(timerOrientation != null){
            timerOrientation.cancel();
        }
        currentItem = 0;
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
    public void onShowsItemClicked(int adapterPosition) {
        releasePlayer();
        SharedPreferenceUtility.setShowId(freeShowList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, freeShowList_adapter.getItem(adapterPosition).getShow_id());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClicked(int adapterPosition) {
        releasePlayer();
        SharedPreferenceUtility.setShowId(videoList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, videoList_adapter.getItem(adapterPosition).getShow_id());

        overridePendingTransition(0, 0);

    }

    @Override
    public void onCategoryItemClicked(int adapterPosition) {
        releasePlayer();
        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, categoryList_adapter
                .getItem(adapterPosition).getCategoryid() + ";" + categoryList_adapter.getItem
                (adapterPosition).getCategory());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onSuggestedItemClicked(int adapterPosition) {
        releasePlayer();
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelListAdapter.getItem(adapterPosition).getChannelId());

        overridePendingTransition(0, 0);
    }


    @Override
    public void onShowItemClicked(int adapterPosition) {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChannelItemClicked(int adapterPosition) {
        releasePlayer();
        SharedPreferenceUtility.setChannelId(liveChannelsAdapter.getItem(adapterPosition)
                .getChannelId());
        SharedPreferenceUtility.setChannelTimeZone(liveChannelsAdapter.getItem(adapterPosition).getTimezone());
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, liveChannelsAdapter.getItem(adapterPosition)
                .getChannelId());

        overridePendingTransition(0, 0);

        //  Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();

    }


    //RewardedVideo event listeners
    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        releasePlayer();
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, commonVideoOrChannelId);
        overridePendingTransition(0, 0);


    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void callAdMobShowAdMethod(int videoId) {


    }

    private static void firstTimeInstallAnalyticsApiCall(Context context) {

        //Uncomment to enable analytics api call
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;

            boolean isExist = sharedPreferences.contains("isFirstTimeInstall");
            if (!isExist) {
                SharedPreferenceUtility.setIsFirstTimeInstall(true);
                isExist = true;
            }
            boolean isFirstTimeInstall = SharedPreferenceUtility.getIsFirstTimeInstall();
            if (isFirstTimeInstall) {

                SharedPreferenceUtility.setIsFirstTimeInstall(false);
                //get time stamp
                Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long epoch = currentCalendar.getTimeInMillis() / 1000L;
                String ipAddress = HappiApplication.getIpAddress();
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = getNetworkIP();
                }
                //String userAgent = new WebView(HappiApplication.getCurrentContext()).getSettings().getUserAgentString();

                String userAgent = "";
                try {
                    userAgent = new WebView(context).getSettings().getUserAgentString();
                } catch (Exception ex) {
                    userAgent = "Mozilla/5.0 (Linux; Android 5.1.1; NEO-U1 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Safari/537.36";
                }

                if (userAgent == null || userAgent.isEmpty()) {
                    userAgent = "Mozilla/5.0 (Linux; Android 5.1.1; NEO-U1 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Safari/537.36";
                }
                String deviceId = SharedPreferenceUtility.getAdvertisingId();

                JsonObject details = new JsonObject();
                details.addProperty("timestamp", String.valueOf(epoch));
                details.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
                details.addProperty("device_id", deviceId);
                details.addProperty("device_type", "Android");
                details.addProperty("latitude", String.valueOf(HappiApplication.getLatitude()));
                details.addProperty("longitude", String.valueOf(HappiApplication.getLongitude()));
                details.addProperty("country", HappiApplication.getCountry());
                details.addProperty("city", HappiApplication.getCity());
                details.addProperty("ua", userAgent);
                details.addProperty("ip_address", ipAddress);
                details.addProperty("advertiser_id", SharedPreferenceUtility.getAdvertisingId());
                details.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
                details.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
                details.addProperty("width", String.valueOf(width));
                details.addProperty("height", String.valueOf(height));
                details.addProperty("device_make", manufacturer);
                details.addProperty("device_model", model);
                details.addProperty("user_name", SharedPreferenceUtility.getUserName());
                details.addProperty("user_email", SharedPreferenceUtility.getUserEmail());
                details.addProperty("user_contact_number", SharedPreferenceUtility.getUserContact());
                details.addProperty("publisherid", SharedPreferenceUtility.getPublisher_id());


                Log.e("000##", " FRST: " + "api about to call");
                AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
                Call<String> calls = analyticsServiceScalar.firstTimeInstall(details);
                calls.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("000##", "success: " + "FRST" + " - " + response.code());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("000##", "failure: " + "FRST" + " - " + t.toString());
                    }
                });

            }
        } catch (Exception e) {
            Log.e("000##", "exception: " + "FRST" + " - " + e.toString());
        }
    }

    private static String getNetworkIP() {
        boolean isMobileData = false;
        boolean isWifi = false;
        String ipAddress = "";


        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (networkInfo.isConnected()) {
                    isWifi = true;
                } else {
                    isWifi = false;
                }
            }

            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (networkInfo.isConnected()) {
                    isMobileData = true;
                } else {
                    isMobileData = false;
                }

            }
        }

        if (isWifi) {
            ipAddress = getWifiIpAddress();
        }
        if (isMobileData) {
            ipAddress = getMobileIpAddress();
        }
        return ipAddress;
    }

    private static String getWifiIpAddress() {
        @SuppressWarnings("deprecation")

        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    private static String getMobileIpAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumipAddr = networkInterface.getInetAddresses(); enumipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        // if(inetAddress instanceof Inet4Address)
                        return Formatter.formatIpAddress(inetAddress.hashCode());

                    }

                }
            }

        } catch (Exception ex) {
            Log.e("1234###", "exception: " + ex.toString());
        }
        return null;
    }

    private static void appLaunchAnalyticsApiCall() {

        //Uncomment to enable analytics api call
        try {
            if (HappiApplication.isApplaunch()) {
                HappiApplication.setApplaunch(false);
                String deviceId = SharedPreferenceUtility.getAdvertisingId();
                Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long epoch = currentCalendar.getTimeInMillis() / 1000L;


                JsonObject eventDetails = new JsonObject();
                eventDetails.addProperty("device_id", deviceId);
                eventDetails.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
                eventDetails.addProperty("event_type", "POP01");
                eventDetails.addProperty("timestamp", String.valueOf(epoch));
                eventDetails.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
                eventDetails.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
                eventDetails.addProperty("publisherid", SharedPreferenceUtility.getPublisher_id());


                Log.e("000##", " POP01: " + "api about to call");
                AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
                Call<String> call = analyticsServiceScalar.eventCall2(eventDetails);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("000##", "success: " + "POP01" + " - " + response.code());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("000##", "failure: " + "POP01" + " - " + t.toString());
                    }
                });

            }
        } catch (Exception e) {
            Log.e("000##", "exception: " + "POP01" + " - " + e.toString());
        }
    }


    @Override
    public void onCategoryItemClickedForCircleView(int adapterPosition) {
        releasePlayer();

        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, circleViewAdapter
                .getItem(adapterPosition).getCategoryid() + ";" + circleViewAdapter.getItem
                (adapterPosition).getCategory());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRedirectionToLive(ChannelModel channelModel) {
        releasePlayer();
        SharedPreferenceUtility.setChannelId(channelModel.getChannelId());
        SharedPreferenceUtility.setChannelTimeZone(channelModel.getTimezone());
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelModel.getChannelId());
        ArrayList<Integer> notfIds = new ArrayList<>();
        notfIds = SharedPreferenceUtility.getNotificationIds();
        if (notfIds.size() > 0 && (uniqueId != 0)) {
            int index = notfIds.indexOf(uniqueId);
            if (index != -1) {
                notfIds.remove(index);
                SharedPreferenceUtility.setNotificationIds(notfIds);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (isNetworkConnected()) {
            mSwipeRefreshLayout.setRefreshing(false);
            releasePlayer();
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onOkClickNeutral() {
        logoutApiCall();
    }

    @Override
    public void onPartnerItemClicked(int adapterPosition) {
        releasePlayer();
        String partnerDetails = partnersListingAdapter.getItem(adapterPosition).getPartner_id() + ";" + partnersListingAdapter.getItem
                (adapterPosition).getName();
        SharedPreferenceUtility.setPartnerId(partnerDetails);
        ActivityChooser.goToActivity(ConstantUtils.PARTNER_VIDEOS_LISTING_ACTIVITY, partnerDetails);
        overridePendingTransition(0, 0);
    }


    //async task for advertising id
    public static class AdvertisingIdAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            AdvertisingIdClient.AdInfo adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo
                        (HappiApplication.getCurrentContext());
                String advertisingId_fromThread = adInfo.getId();
                SharedPreferenceUtility.setAdvertisingId(advertisingId_fromThread);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Advertising_id", "exception : " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setSessionId();
            firstTimeInstallAnalyticsApiCall(context);
            appLaunchAnalyticsApiCall();
        }
    }

    private void getDisplayMetrics() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    private static void setSessionId() {
        String sessionId = "";
        String device_id = SharedPreferenceUtility.getAdvertisingId();
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;
        sessionId = String.valueOf(epoch) + device_id;
        SharedPreferenceUtility.setSession_Id(sessionId);
    }

    private void hideLivePlayerAndSchedule() {
        /*releaseExoPlayer();
        pb_live.setVisibility(View.GONE);
        rl_player_live.setVisibility(View.GONE);
        exo_player_view_home.setVisibility(View.GONE);
        ll_live_guide.setVisibility(View.GONE);
        rv_live_schedule_list.setVisibility(View.GONE);*/
    }

    private void liveError() {
        pb_live.setVisibility(View.GONE);
        //Toast.makeText(this, "Oops!! Can't play video. Please try again.", Toast.LENGTH_SHORT).show();

        //hideLivePlayerAndSchedule();
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.removeListener(eventLogger);
            exoPlayer.removeMetadataOutput(eventLogger);
            exoPlayer.removeAudioDebugListener(eventLogger);
            exoPlayer.removeVideoDebugListener(eventLogger);
            exoPlayer = null;
            exo_player_view_home.setPlayer(null);
        }
    }

    private void loadHome() {
        releaseExoPlayer();
        setupRecyclerView();
        if (HappiApplication.getAppToken().isEmpty()) {
            getSessionToken();
        } else {
            if (!SharedPreferenceUtility.getGuest()) {
                checkSubscription();
            }

            liveNow();
            loadPartners();
            //categoryApiCall();
            //watchForFreeShowList();
            newReleases();
            loadCategoriesHomeList();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void resumePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void logoutApiCall() {
        dialog.show();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable logoutDisposable = usersService.logout(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getPublisher_id(),
                SharedPreferenceUtility.getAdvertisingId(), HappiApplication.getIpAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutResponseModel -> {

                    if (logoutResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "", "", false, "");
                        SharedPreferenceUtility.setGuest(false);
                        SharedPreferenceUtility.setIsFirstTimeInstall(false);
                        SharedPreferenceUtility.setChannelId(0);
                        SharedPreferenceUtility.setShowId("0");
                        SharedPreferenceUtility.setVideoId(0);
                        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
                        SharedPreferenceUtility.setChannelTimeZone("");
                        SharedPreferenceUtility.setSession_Id("");
                        SharedPreferenceUtility.setPartnerId("");
                        SharedPreferenceUtility.setNotificationIds(new ArrayList<>());
                        SharedPreferenceUtility.setSubscriptionItemIdList(new ArrayList<>());

                        HappiApplication.setSub_id(new ArrayList<>());


                        goToLogin();


                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                        Log.e("Logout", "api call failed");
                    }

                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                    Log.e("Logout", "api call failed");
                });

        compositeDisposable.add(logoutDisposable);
    }

    private void goToLogin() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(MainHomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        MainHomeActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        shouldAutoPlay = false;
        if(timerOrientation != null){
            timerOrientation.cancel();
        }
        currentItem = 0;
        super.onStop();

    }

    private void stopScrollTimer() {
        Log.e("HOME", "stopScrollTimer");
        if (null != swipeTimer) {
            swipeTimer.cancel();
        }
        if (null != swipeTask) {
            swipeTask.cancel();
        }
    }

    private void resetScrollTimer() {
        Log.e("HOME", "resetScrollTimer");
        stopScrollTimer();
        swipeTask = new SwipeTask();
        swipeTimer = new Timer();
    }

    private void playCarousel() {
        Log.e("CARS", "playCarousel func");
        resetScrollTimer();
        swipeTimer.schedule(swipeTask, 0, 5000);
    }


    private class SwipeTask extends TimerTask {
        public void run() {
            rv_partner.post(() -> {

                //if(currentItem == partnersListingAdapter.getItemCount() || currentItem > partnersListingAdapter.getItemCount()){
                if(layoutManager.findFirstVisibleItemPosition() == partnersListingAdapter.getItemCount() - 2){
                    currentItem = 0;
                }else{
                    currentItem = layoutManager.findFirstVisibleItemPosition() + 2;
                }
                rv_partner.scrollToPosition(currentItem);
                Log.e("CARS", "first visible " + layoutManager.findFirstVisibleItemPosition());
                Log.e("CARS", "currentItem " + currentItem);



               /* int nextPage;

                if (layoutManager.findFirstVisibleItemPosition() == partnersListingAdapter.getItemCount() - 2) {
                //if (layoutManager.findFirstVisibleItemPosition() == partnersListingAdapter.getItemCount() - 1) {
                    nextPage = 0;
                }else{
                    nextPage = layoutManager.findFirstVisibleItemPosition() + 2;
                    //nextPage = layoutManager.findFirstVisibleItemPosition() + 1;
                }
                //rv_partner.smoothScrollToPosition(nextPage);
                rv_partner.scrollToPosition(nextPage);
                Log.e("CARS", "first visible " + layoutManager.findFirstVisibleItemPosition());
                Log.e("CARS", "nextPage " + nextPage);*/

            });
        }
    }

    private void startTimer(){
        timerOrientation = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                playCarousel();
                if(timerOrientation != null){
                    timerOrientation.cancel();
                }

            }
        }.start();

    }
    private void initializeTimerScheduler(String event){//edit timer
        timerSChedule = new Timer();
        timerSChedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(isLivePlaying){
                    //  Log.e("########","TRUE  ");
                    isTimerActive = true;
                    liveEventAnalyticsApiCall(event);
                }
            }
        },0,60000);
    }

    private void liveEventAnalyticsApiCall(String eventType){

        /*Uncomment to enable analytics api call for CHANNEL*/

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;

        String device_id = SharedPreferenceUtility.getAdvertisingId();

        JsonObject videoDetails = new JsonObject();
        videoDetails.addProperty("device_id",device_id);
        videoDetails.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
        videoDetails.addProperty("event_type",eventType);
        videoDetails.addProperty("video_id","0");
        videoDetails.addProperty("channel_id",channel_Id);
        videoDetails.addProperty("video_title",channelTitle);
        videoDetails.addProperty("timestamp",String.valueOf( epoch));
        videoDetails.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
        videoDetails.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
        videoDetails.addProperty("publisherid", SharedPreferenceUtility.getPublisher_id());

        try {
            Log.e("000##",": api call is about to be made:  "+eventType+" - ");
            AnalyticsApi.AnalyticsServiceScalar analyticsService = AnalyticsApi.createScalar();
            Call<String> callS = analyticsService.eventCall2(videoDetails);
            callS.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("000##","success: "+eventType+" - "+response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("000##","failure: "+eventType+" - "+t.toString());
                }
            });

        }catch(Exception ex){
            Log.e("000##","exception: "+eventType+" - "+ex);
        }
    }
}
