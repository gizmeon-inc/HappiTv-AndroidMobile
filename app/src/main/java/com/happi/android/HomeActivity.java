package com.happi.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.happi.android.adapters.BannerAdapter;
import com.happi.android.adapters.CategoriesHomeListAdapter;
import com.happi.android.adapters.CategoryCircleViewAdapter;
import com.happi.android.adapters.CategoryListAdapter;
import com.happi.android.adapters.ChannelListAdapter;
import com.happi.android.adapters.ChannelSuggestionAdapter;
import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.adapters.ShowsAdapter;
import com.happi.android.adapters.VideoList_adapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.AdvertisingIdClient;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.customviews.ViewPagerCustomDuration;
import com.happi.android.models.CategoriesHomeListVideoModel;
import com.happi.android.models.CategoryModel;
import com.happi.android.models.ChannelModel;
import com.happi.android.models.FeaturedShowsModel;
import com.happi.android.models.ShowModel;
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
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements LogoutAlertDialog.onLogoutClickListener, View.OnClickListener,
        VideoList_adapter.itemClickListener,
        ShowList_adapter.itemClickListener,
        ChannelSuggestionAdapter.SuggesteditemClickListener, CategoryListAdapter
                .itemClickListener, BannerAdapter.itemClickListener,
        ShowsAdapter.itemClickListener, ChannelListAdapter.itemClickListener,
        RewardedVideoAdListener, CategoriesHomeListAdapter.ICallAdmobAd,
        CategoryCircleViewAdapter.itemClickListenerForCategory,
        ChannelListAdapter.RedirectToLive {


    VideoList_adapter videoList_adapter;
    ShowList_adapter freeShowList_adapter;
    CategoryListAdapter categoryList_adapter;
    ChannelSuggestionAdapter channelListAdapter;
    ChannelListAdapter liveChannelsAdapter;
    RecyclerView.LayoutManager layoutManager;
    private CompositeDisposable compositeDisposable;
    //error layout
    private LinearLayout ll_error_home;
    private TypefacedTextViewRegular tv_errormsg;
    private ImageView iv_errorimg;

    SkeletonScreen loadingVideos, loadingCategories, loadingChannels, loadingLive, loadingFreeShows;
    LinearLayout ll_popular_channels, ll_category_list, ll_popular_videos,
            ll_popular_live, ll_watch_free;
    GridRecyclerView rv_video_grid, rv_category_list, rv_more_channels, rv_categories_home_list, rv_live, rv_watch_free;
    CardView cv_banner;

    ImageView iv_search;
    private AnimationItem mSelectedItem;
    int currentPage = 0;
    private int userId = 0;
    Timer timer;
    ViewPagerCustomDuration pager;
    BannerAdapter pagerAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean homeLoaded = false;

    //get user subscription id list
    private List<UserSubscriptionModel> subscriptionModelList;
    private List<String> subids;


    //RewardedVideoAd and InterstitialAd variables
    private RewardedVideoAd mRewardedVideoAd;
    private int commonVideoOrChannelId = 0;

    private String showId = "empty";
    private boolean isFromSubsc = false;

    int apiErrorCount = 0;
    int adapterEmptyCount = 0;

    CategoryCircleViewAdapter circleViewAdapter;

    public int channelId = 0;
    public boolean isRedirectToLive = false;
    public int uniqueId = 0;
    //display metrics
    private static int width;
    private int modifiedHeight = 0;
    private static int height;
    private static WifiManager wifiManager;
    private static ConnectivityManager connectivityManager;
    private static SharedPreferences sharedPreferences;
    private DisplayMetrics displayMetrics;

    @Override
    public void onClick(View view) {
    }


    @SuppressLint("ClickableViewAccessibility")
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
        setContentView(R.layout.activity_home);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();

        if (getIntent() != null && HappiApplication.isIsFromLink()) {
            if (getIntent().getStringExtra("show") != null && !getIntent().getStringExtra("show").isEmpty()) {
                showId = getIntent().getStringExtra("show");
            } else {
                showId = "empty";
            }
        }
        if (getIntent() != null) {
            isRedirectToLive = getIntent().getBooleanExtra("notification", false);
            if (isRedirectToLive) {
                channelId = getIntent().getIntExtra(ConstantUtils.CHANNEL_ID, 0);
                uniqueId = getIntent().getIntExtra(ConstantUtils.UNIQUE_ID, 0);
            }
        }
        isFromSubsc = false;
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        sharedPreferences = getSharedPreferences(HappiApplication.getCurrentContext().getString(R.string.USER_PREFERENCES),
                Context.MODE_PRIVATE);
        getDisplayMetrics();

        compositeDisposable = new CompositeDisposable();

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        onCreateDrawer();

        RelativeLayout rl_exoplayer_parent = findViewById(R.id.rl_exoplayer_parent);
        RelativeLayout rl_toolbar = findViewById(R.id.rl_toolbar);
        rv_watch_free = findViewById(R.id.rv_watch_free);
        ll_error_home = findViewById(R.id.ll_error_home);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        ll_error_home.setVisibility(View.GONE);
        rv_live = findViewById(R.id.rv_live);
        rv_video_grid = findViewById(R.id.rv_video_grid);
        rv_category_list = findViewById(R.id.rv_category_list);
        rv_more_channels = findViewById(R.id.rv_more_channels);
        rv_categories_home_list = findViewById(R.id.rv_categories_home_list);
        ll_popular_channels = findViewById(R.id.ll_popular_channels);
        ll_category_list = findViewById(R.id.ll_category_list);
        ll_popular_videos = findViewById(R.id.ll_popular_videos);
        ll_watch_free = findViewById(R.id.ll_watch_free);
        ll_popular_live = findViewById(R.id.ll_popular_live);
        cv_banner = findViewById(R.id.cv_banner);
        iv_search = findViewById(R.id.iv_search);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setDistanceToTriggerSync(250);

        userId = SharedPreferenceUtility.getUserId();

        layoutManager = new LinearLayoutManager(this);

        //get user subscription id list
        subscriptionModelList = new ArrayList<>();
        subids = new ArrayList<>();

        adapterEmptyCount = 0;

        // getSessionToken();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        // cv_banner.setLayoutParams(new RelativeLayout.LayoutParams(modifiedHeight,width ));
        apiErrorCount = 0;
        getSessionToken();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            currentPage = 0;
            mSwipeRefreshLayout.setRefreshing(false);
            finish();
            startActivity(getIntent());
            //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0, 0);
            //setupRecyclerView();
        });

        iv_search.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            intent.putExtra("search_type", "show");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            overridePendingTransition(0, 0);
        });

      /*  ll_popular_shows.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, ShowsListActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });*/
        ll_watch_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PopularVideosActivity.class);
                intent.putExtra("title", "watchFree");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                overridePendingTransition(0, 0);
            }
        });
        ll_popular_live.setOnClickListener(v -> {

            Intent intent = new Intent(HappiApplication.getCurrentContext(), LiveVideoListingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0, 0);
        });

        ll_category_list.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, CategoriesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0, 0);
        });

        ll_popular_videos.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, PopularVideosActivity.class);
            intent.putExtra("title", "newRelease");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0, 0);
        });

        ll_popular_channels.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, PopularChannelsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            overridePendingTransition(0, 0);
        });

        homeLoaded = false;
        //setupRecyclerView();


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        // Log.e("access-token  : ", " " + HappiApplication.getAppToken());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        HappiApplication.setCurrentContext(this);
        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
        updateMenuItem(0);

        setUserName();

        if (!homeLoaded) {

            setupRecyclerView();

        }

        recallHomeApis();

        Handler handler = new Handler();
        Runnable update = new Runnable() {
            public void run() {

                if (currentPage == 5) {

                    currentPage = 0;
                }
                pager.setCurrentItem(currentPage++, true);
                pager.setScrollDuration(750);
            }
        };

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 4000);

        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
        //analytics
        if (!SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            setSessionId();
            firstTimeInstallAnalyticsApiCall();
            appLaunchAnalyticsApiCall();
        } else {
            new AdvertisingIdAsyncTask().execute();
        }

        super.onResume();
    }


    private void setupRecyclerView() {

        ll_error_home.setVisibility(View.GONE);
        //-----------------------------------------live----------------------------------------------------//
        rv_live.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        liveChannelsAdapter = new ChannelListAdapter(getApplicationContext(),
                this::onChannelItemClicked, false, HomeActivity.this, this::onRedirectionToLive);
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
        //------------------------------------------------------------------------------------------------//

        rv_video_grid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videoList_adapter = new VideoList_adapter(getApplicationContext(), this::onItemClicked, false);
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

        //-----------------------------------------------watch for free---------------------------------------------------------//
        ll_watch_free.setVisibility(View.VISIBLE);
        rv_watch_free.setVisibility(View.VISIBLE);

        rv_watch_free.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        freeShowList_adapter = new ShowList_adapter(getApplicationContext(), this::onShowsItemClicked, false);
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

        //--------------------------------------------------------------------------------------------------------------------//

        rv_category_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList_adapter = new CategoryListAdapter(getApplicationContext(),
                this::onCategoryItemClicked, false);
        //  rv_category_list.setAdapter(categoryList_adapter);

        circleViewAdapter = new CategoryCircleViewAdapter(getApplicationContext(), this::onCategoryItemClickedForCircleView);
        rv_category_list.setAdapter(circleViewAdapter);

        loadingCategories = Skeleton.bind(rv_category_list)
                //.adapter(categoryList_adapter)
                .adapter(circleViewAdapter)
                //.load(R.layout.loading_categories_horizontal)
                .load(R.layout.item_category_circle_loading)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();


        //rv_more_channels.addItemDecoration(new ItemOffsetDecoration(spacing));
        rv_more_channels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        channelListAdapter = new ChannelSuggestionAdapter(getApplicationContext(),
                this::onSuggestedItemClicked, false);
        rv_more_channels.setAdapter(channelListAdapter);
        loadingChannels = Skeleton.bind(rv_more_channels)
                .adapter(channelListAdapter)
                .load(R.layout.loading_channel_horizontal)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

        pager = findViewById(R.id.vp_viewpager);
        pagerAdapter = new BannerAdapter(getApplicationContext(), this::onBannerItemClicked);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tl_tablayout);
        tabLayout.setupWithViewPager(pager, true);


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
                            getFeaturedVideos();
                            categoryApiCall();
                            watchForFreeShowList();
                            newReleases();
                            liveNow();
                            loadCategoriesHomeList();


                            //setLandingPage();
                          /*  if( !showId.equalsIgnoreCase("empty") && HappiApplication.isIsFromLink()){
                                HappiApplication.setIsFromLink(false);
                                Intent show = new Intent(HomeActivity.this, ShowDetailsActivity.class);
                                show.putExtra(ConstantUtils.SHOW_ID,showId);
                                startActivity(show);
                                showId = "empty";
                            }*/


                        }, throwable -> {
                            // Log.w("KKK###","session");
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
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
                        isFromSubsc = true;
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
                    // Toast.makeText(HomeActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }

    private void loginExceededAlertSubscription() {
        LogoutAlertDialog alertDialog = new LogoutAlertDialog(HappiApplication.getCurrentActivity(), this);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void getFeaturedVideos() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.GetFeaturedvideo(HappiApplication.getAppToken(),
                userId, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showResponse -> {

                    if (showResponse.getShowModelList().size() != 0) {
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(width, modifiedHeight);
                        // cv_banner.setLayoutParams(rlp);
                        //  cv_banner.setPadding(-10,0,-10,0);
                        updateBannerTiles(showResponse.getShowModelList());
                    } else {

                        cv_banner.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    apiErrorCount++;
                    //  Log.e("HOME HOME", "feature error");
                    // displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(videoDisposable);
    }

    private void watchForFreeShowList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getFreeShowList(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
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
        Disposable videoDisposable = usersService.NewReleases(HappiApplication.getAppToken(),
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
                    //loadCategories();
                }, throwable -> {
                    // Log.w("KKK###","new");
                    apiErrorCount++;
                    //  Log.e("HOME HOME", "NEW REL error");
                    // displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(videoDisposable);
    }

    private void liveNow() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable liveDisposable = usersService.getChannels(HappiApplication.getAppToken(), SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelListResponse -> {
                    if (channelListResponse.getData().size() != 0) {
                        if (channelListResponse.getData().size() >= 10) {
                            updateLiveVideos(channelListResponse.getData().subList(0, 10));
                        } else {
                            updateLiveVideos(channelListResponse.getData());
                        }

                    } else {
                        rv_live.setVisibility(View.GONE);
                        ll_popular_live.setVisibility(View.GONE);
                    }

                }, throwable -> {
                    // Log.w("KKK###","live");
                    apiErrorCount++;
                    // Log.e("HOME HOME", "LIVE error");
                    // displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(liveDisposable);
    }

    private void categoryApiCall() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.GetTheme(HappiApplication.getAppToken(),
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
                    Toast.makeText(HomeActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);

        if (!showId.equalsIgnoreCase("empty") && HappiApplication.isIsFromLink()) {
            HappiApplication.setIsFromLink(false);
            Intent show = new Intent(HomeActivity.this, ShowDetailsActivity.class);
            show.putExtra(ConstantUtils.SHOW_ID, showId);
            show.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(show);
            overridePendingTransition(0, 0);
            showId = "empty";
        }
    }

    private void loadCategoriesHomeList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable homeVideoDisposable = usersService.GetHomeVideo(HappiApplication.getAppToken(),
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
                        ll_popular_channels.setVisibility(View.GONE);
                        rv_more_channels.setVisibility(View.GONE);
                        rv_categories_home_list.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    // Log.w("KKK###","cathomelist");
                    apiErrorCount++;
                    // Log.e("HOME HOME", "CATEGORIES error");
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(homeVideoDisposable);
    }

    private void recallHomeApis() {
        if (!HappiApplication.getAppToken().isEmpty()) {
            if (pagerAdapter == null || pagerAdapter.isEmpty()) {
                getFeaturedVideos();
            } else if (circleViewAdapter == null || circleViewAdapter.isEmpty()) {
                categoryApiCall();
            } else if (SharedPreferenceUtility.getGuest()) {
                if (freeShowList_adapter == null || freeShowList_adapter.isEmpty()) {
                    watchForFreeShowList();
                }
            } else if (videoList_adapter == null || videoList_adapter.isEmpty()) {
                newReleases();
            } else if (liveChannelsAdapter == null || liveChannelsAdapter.isEmpty()) {
                liveNow();
                // }else if(categoryList_adapter==null || categoryList_adapter.isEmpty()){
            } else if (!homeLoaded) {
                loadCategoriesHomeList();
            }
        } else {
            getSessionToken();
        }
    }

    private void updateBannerTiles(List<FeaturedShowsModel> showModel) {

        if (showModel.size() > 5) {
            pagerAdapter.addAll(showModel.subList(0, 5));
        } else {
            pagerAdapter.addAll(showModel);
        }
        pagerAdapter.notifyDataSetChanged();
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
        CategoriesHomeListAdapter adapter = new CategoriesHomeListAdapter(categoriesHomeListVideoModelList, this);
        rv_categories_home_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_categories_home_list.setAdapter(adapter);
        homeLoaded = true;

        //return true;
    }


    private void displayErrorLayout(String message) {
        if (apiErrorCount == 6) {
            cv_banner.setVisibility(View.GONE);
            rv_more_channels.setVisibility(View.GONE);
            rv_video_grid.setVisibility(View.GONE);
            rv_category_list.setVisibility(View.GONE);
            rv_live.setVisibility(View.GONE);
            rv_categories_home_list.setVisibility(View.GONE);
            ll_popular_channels.setVisibility(View.GONE);
            ll_category_list.setVisibility(View.GONE);
            ll_popular_videos.setVisibility(View.GONE);
            ll_popular_live.setVisibility(View.GONE);

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
        super.onPause();
        timer.cancel();
    }

    public void onBackPressed() {

        if (isDrawerOpened) {

            drawer.closeDrawer(Gravity.LEFT);
        } else {

            AppUtils.exitApp();
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
    public void onShowsItemClicked(int adapterPosition) {
        HappiApplication.setRedirect("");
        SharedPreferenceUtility.setShowId(freeShowList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, freeShowList_adapter.getItem(adapterPosition).getShow_id());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClicked(int adapterPosition) {
        HappiApplication.setRedirect("");
        SharedPreferenceUtility.setShowId(videoList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, videoList_adapter.getItem(adapterPosition).getShow_id());

        overridePendingTransition(0, 0);

    }

    @Override
    public void onCategoryItemClicked(int adapterPosition) {
        HappiApplication.setRedirect("");
        HappiApplication.setCategoryId(categoryList_adapter.getItem(adapterPosition).getCategoryid()
                + ";" + categoryList_adapter.getItem(adapterPosition).getCategory());
        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, categoryList_adapter
                .getItem(adapterPosition).getCategoryid() + ";" + categoryList_adapter.getItem
                (adapterPosition).getCategory());


        overridePendingTransition(0, 0);
    }

    @Override
    public void onSuggestedItemClicked(int adapterPosition) {
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, channelListAdapter.getItem(adapterPosition).getChannelId());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBannerItemClicked(int adapterPosition) {
        HappiApplication.setRedirect("");
        SharedPreferenceUtility.setShowId(pagerAdapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, pagerAdapter.getItem(adapterPosition).getShow_id());

        overridePendingTransition(0, 0);

    }

    @Override
    public void onShowItemClicked(int adapterPosition) {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChannelItemClicked(int adapterPosition) {

        SharedPreferenceUtility.setChannelId(liveChannelsAdapter.getItem(adapterPosition)
                .getChannelId());
        SharedPreferenceUtility.setChannelTimeZone(liveChannelsAdapter.getItem(adapterPosition).getTimezone());
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, liveChannelsAdapter.getItem(adapterPosition)
                .getChannelId());
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

    private static void firstTimeInstallAnalyticsApiCall() {

        //Uncomment to enable analytics api call

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
            if (ipAddress.isEmpty()) {
                ipAddress = getNetworkIP();
            }
            String userAgent = new WebView(HappiApplication.getCurrentContext()).getSettings().getUserAgentString();
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


            try {
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

            } catch (Exception e) {
                Log.e("000##", "exception: " + "FRST" + " - " + e.toString());
            }
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


            try {
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

            } catch (Exception e) {
                Log.e("000##", "exception: " + "POP01" + " - " + e.toString());
            }
        }
    }

    @Override
    public void onLogoutClicked() {
        if (isFromSubsc) {
            // logoutApiCall();
        }
    }

    @Override
    public void onLogoutAllClicked() {
        //  logoutAllApiCall();
    }

    @Override
    public void onCategoryItemClickedForCircleView(int adapterPosition) {
        HappiApplication.setRedirect("");
        HappiApplication.setCategoryId(circleViewAdapter.getItem(adapterPosition).getCategoryid()
                + ";" + circleViewAdapter.getItem(adapterPosition).getCategory());
        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, circleViewAdapter
                .getItem(adapterPosition).getCategoryid() + ";" + circleViewAdapter.getItem
                (adapterPosition).getCategory());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onRedirectionToLive(ChannelModel channelModel) {
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
            firstTimeInstallAnalyticsApiCall();
            appLaunchAnalyticsApiCall();
        }
    }

    private void getDisplayMetrics() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        modifiedHeight = (int) ((3 * height) / 4);
        String a = "xdd";
    }

    private static void setSessionId() {
        String sessionId = "";
        String device_id = SharedPreferenceUtility.getAdvertisingId();
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;
        sessionId = String.valueOf(epoch) + device_id;
        SharedPreferenceUtility.setSession_Id(sessionId);
    }
}
