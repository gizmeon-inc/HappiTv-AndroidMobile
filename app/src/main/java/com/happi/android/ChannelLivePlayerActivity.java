package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.happi.android.adapters.ChannelSuggestionAdapter;
import com.happi.android.adapters.LiveScheduleAdapter;
import com.happi.android.adapters.VideoListAdapter_New;
import com.happi.android.adapters.VideoList_adapter;
import com.happi.android.adapters.VodLiveAdapter;
import com.happi.android.cast.ExpandedControlsActivity;
import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.NotificationTriggerActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.LiveSchedulePopUpClass;
import com.happi.android.customviews.LoginRegisterAlert;
import com.happi.android.customviews.SpacesItemDecoration;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.exoplayercontroller.EventLogger;
import com.happi.android.exoplayercontroller.TrackSelectionHelper;
import com.happi.android.models.ASTVHome;
import com.happi.android.models.ChannelSubscriptionModel;
import com.happi.android.models.IPAddressModel;
import com.happi.android.models.ScheduleUpdatedModel;
import com.happi.android.models.TokenResponse;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.models.VideoModel;
import com.happi.android.models.VodToLiveModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.utils.FormatAdUrl;
import com.happi.android.webservice.AnalyticsApi;
import com.happi.android.webservice.ApiClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdError;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
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

import static android.view.View.GONE;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

public class ChannelLivePlayerActivity extends BaseActivity implements View.OnClickListener,
        VideoListAdapter_New.itemClickListener,
        VideoList_adapter.itemClickListener,
        ChannelSuggestionAdapter.SuggesteditemClickListener,
        AdErrorEvent.AdErrorListener, AdEvent.AdEventListener,
        VodLiveAdapter.itemClickListener,
        LiveScheduleAdapter.OnScheduleItemClick,
        LiveSchedulePopUpClass.onNotificationOn,
        LiveSchedulePopUpClass.onNotificationOff,
        LoginRegisterAlert.OnLoginRegisterUserNeutral, LoginRegisterAlert.OnLoginRegisterUserNegative,
        CustomAlertDialog.OnOkClick{

    /////////////////////////orientation//////////////////////////
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private CountDownTimer timerOrientation;
    private boolean isOrientationChange = false;
    ////////////////////////////////////////////////////////////////////////
    ASTVHome pHome;
    ASTVHome objectHome;
    String token = "";
    VideoList_adapter videoList_adapter;
    VideoListAdapter_New videoList_adapter_new;
    VodLiveAdapter vodLiveAdapter;
    RecyclerView.LayoutManager layoutManager;
    private CompositeDisposable compositeDisposable;
    TypefacedTextViewRegular tv_errormsg;
    TypefacedTextViewBold tv_more_videos;
    ImageView iv_errorimg;
    GridRecyclerView rv_video_grid;
    RelativeLayout rl_video_grid;
    TypefacedTextViewBold tv_channel_title;
    ImageView iv_back;
    ImageView iv_icon;
    PlayerView exo_player_view;
    DefaultTimeBar exo_progress;
    private ImageView exo_fullscreen_icon;
    private boolean isExoPlayerFullscreen = false;
    RelativeLayout rl_exoplayer_parent;
    RelativeLayout rl_toolbar;
    private DataSource.Factory mediaDataSourceFactory;
    SimpleExoPlayer exoPlayer;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private float volume;
    SkeletonScreen loadingVideos,loadingSchedule;
    LinearLayout popular_channel_videos;
    private AnimationItem mSelectedItem;
    ChannelSuggestionAdapter suggestAdapter;
    private int CHANNEL_ID;
    private int userId = 0;
    private List<ChannelSubscriptionModel> channelSubscriptionModelList;
    private List<String> subscriptionIds;
    ProgressDialog progressDialog;
    private List<ASTVHome> tHome;


    private String gmtStartTime = "";
    private ImaSdkFactory mSdkFactory;
    private AdsLoader mAdsLoader;
    private AdsManager mAdsManager;
    private boolean mIsAdDisplayed;
    private boolean isAdPlaying = false;
    private boolean isFirstAd = true;
    private boolean isFirstAdHandler = true;
    private MediaSource videoSource;
    private FirebaseAnalytics mFirebaseAnalytics;


    //play/pause
    private TypefacedTextViewBold tv_channel_title_live;

    //casting
    CastContext mCastContext;
    MediaRouteButton mMediaRouteButton;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private CastSession mCastSession;
    private MediaInfo mSelectedMedia;
    private static final String KEY_DESCRIPTION = "token";
    private static String TAG = "LiveProvider";

    //analytics
    boolean  isLivePlaying = false;
    boolean isLivePaused = false;
    boolean isTimerActive = false;
    boolean durationSet = false;
    private Timer timerSChedule = null;
    private String channelId = "";
    private String channelTitle = "";

    //schedule
    private LinearLayout ll_schedule;
    private RecyclerView rv_live_schedule;
    private TypefacedTextViewBold tv_live_now_title;
    private TypefacedTextViewRegular tv_schedule_timing;
    private LiveScheduleAdapter liveScheduleAdapter;
    private List<ScheduleUpdatedModel> liveScheduleList;
    private boolean isLiveAvailableForSchedule = false;
    private boolean isForceLogout = false;

    //bottom vnavigation view
    private RelativeLayout rl_btm_navigation_channel;
    private int actionBarHeight = 0;

    //ad
    private IPAddressModel ipAddressModel;
    private boolean shouldAutoPlay = true;
    private Long playerDuration  = 0L;
    private Long playerCurrentPosition  = 0L;
    @Override
    public void onClick(View view) {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_channel_home);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        if(SharedPreferenceUtility.getAdvertisingId().isEmpty()){
            new AdvertisingIdAsyncTask().execute();
        }
        if(HappiApplication.getIpAddress().isEmpty()){
            getNetworkIP();
        }

        SharedPreferenceUtility.setIsLiveVisible(true);
        setupCastListener();

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);

        timerSChedule = new Timer();
       /* timerVideoHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                timerVideoHandler.postDelayed(timeRunnable, delay *//*- SystemClock.elapsedRealtime() % 1000*//*);
                task();
            }
        };*/
        mCastContext = CastContext.getSharedInstance(this);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);
        CastStateListener mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    // showIntroductoryOverlay();
                }
            }
        };

        pHome = new ASTVHome();
        objectHome = new ASTVHome();
        channelSubscriptionModelList = new ArrayList<>();

        tHome = new ArrayList<>();

        compositeDisposable = new CompositeDisposable();



        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Intent intent = getIntent();
        CHANNEL_ID = intent.getIntExtra(ConstantUtils.CHANNEL_ID, -1);
        if(CHANNEL_ID == -1){
            CHANNEL_ID = SharedPreferenceUtility.getChannelId();
        }
        userId = SharedPreferenceUtility.getUserId();
        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];
        exo_player_view = findViewById(R.id.exo_player_view);
        rl_exoplayer_parent = findViewById(R.id.rl_exoplayer_parent);
        rl_toolbar = findViewById(R.id.rl_toolbar);
        rl_video_grid = findViewById(R.id.rl_video_grid);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        tv_more_videos = findViewById(R.id.tv_more_videos);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_channel_title = findViewById(R.id.tv_channel_title);
        iv_back = findViewById(R.id.iv_back);
        rv_video_grid = findViewById(R.id.rv_video_grid);
        popular_channel_videos = findViewById(R.id.ll_popular_videos);
        iv_icon = findViewById(R.id.iv_icon);
        ViewGroup mAdUiContainer = findViewById(R.id.exo_player_view);

        //btm nav
        rl_btm_navigation_channel = findViewById(R.id.rl_btm_navigation_channel);
        rl_btm_navigation_channel.setVisibility(View.VISIBLE);
        //schedule

        ll_schedule = findViewById(R.id.ll_schedule);
        rv_live_schedule = findViewById(R.id.rv_live_schedule);
        TextView tv_now_watching_label = findViewById(R.id.tv_now_watching_label);
        tv_live_now_title = findViewById(R.id.tv_live_now_title);
        tv_schedule_timing = findViewById(R.id.tv_schedule_timing);
        ImageView iv_schedule_icon = findViewById(R.id.iv_schedule_icon);
        liveScheduleList = new ArrayList<>();
        liveScheduleAdapter = new LiveScheduleAdapter(ChannelLivePlayerActivity.this, getApplicationContext(), this::onScheduleClicked);
        // rv_live_schedule.setLayoutManager(new GridLayoutManager(this, 1,GridLayoutManager.HORIZONTAL, false));
        rv_live_schedule.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //rv_live_schedule.setNestedScrollingEnabled(false);
        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.default_spacing_small);
        rv_live_schedule.addItemDecoration(new SpacesItemDecoration(spacingPixels));
        // rv_live_schedule.addItemDecoration(new ItemDecorationAlbumColumns(7, 1));
        rv_live_schedule.setAdapter(liveScheduleAdapter);
        iv_schedule_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(ChannelLivePlayerActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                SharedPreferenceUtility.setChannelId(CHANNEL_ID);
                Intent scheduleIntent = new Intent(ChannelLivePlayerActivity.this, LiveScheduleActivity.class);
                scheduleIntent.putExtra("channel_id",String.valueOf(CHANNEL_ID));
                scheduleIntent.putExtra("schedule_list", (Serializable)liveScheduleList);
                scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);
                ChannelLivePlayerActivity.this.finish();
                overridePendingTransition(0,0);

            }
        });

        setupRecyclerView();
        layoutManager = new LinearLayoutManager(this);


        subscriptionIds = HappiApplication.getSub_id();

        isForceLogout = false;
        if(SharedPreferenceUtility.getGuest()){
            showLoginOrRegisterAlert();
        }else{
            if (HappiApplication.getAppToken() != null && !HappiApplication.getAppToken().isEmpty()) {
                checkUserSubscription();

            } else {
                getSessionToken();
            }

        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        exo_fullscreen_icon = exo_player_view.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout exo_fullscreen_button = exo_player_view.findViewById(R.id.exo_fullscreen_button);
        exo_progress = exo_player_view.findViewById(R.id.exo_progress);
        exo_progress.setVisibility(View.INVISIBLE);
        tv_channel_title_live = exo_player_view.findViewById(R.id.tv_channel_title_live);
        tv_channel_title_live.setVisibility(View.VISIBLE);
        tv_channel_title_live.setSelected(true);


        exo_fullscreen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isExoPlayerFullscreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        OrientationEventListener orientationEventListener =
                new OrientationEventListener(this) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        int epsilon = 10;
                        int leftLandscape = 90;
                        int rightLandscape = 270;
                        int portrait = 0;
                        int portraitUpside = 180;
                        if(epsilonCheck(orientation, leftLandscape, epsilon) ||
                                epsilonCheck(orientation, rightLandscape, epsilon)){
                            //  Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER LAND", Toast.LENGTH_SHORT).show();
                            if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                                isOrientationChange = true;
                                //      Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER LAND CHANGE", Toast.LENGTH_SHORT).show();
                                //setTimer();

                            }



                        }else if(epsilonCheck(orientation, portrait, epsilon) ||
                                epsilonCheck(orientation, portraitUpside, epsilon)){
                            //   Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER PORT", Toast.LENGTH_SHORT).show();

                            if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                                isOrientationChange = true;
                                //    Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER PORT CHANGE", Toast.LENGTH_SHORT).show();
                                // setTimer();

                            }

                        }else{
                            isOrientationChange = false;
                            //   Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER ELSE", Toast.LENGTH_SHORT).show();

                        }

                    }

                    private boolean epsilonCheck(int a, int b, int epsilon) {
                        //return a > b - epsilon && a < b + epsilon;
                        // Math.abs(a - b) < epsilon;
                        // return a > Math.abs(b - epsilon) && a <  Math.abs(b + epsilon);
                        return Math.abs(a - b) < epsilon;
                    }
                };
        orientationEventListener.enable();

        if (savedInstanceState != null) {
            isExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

        mSdkFactory = ImaSdkFactory.getInstance();
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(exo_player_view.getOverlayFrameLayout());
        ImaSdkSettings settings = mSdkFactory.createImaSdkSettings();
        mAdsLoader = mSdkFactory.createAdsLoader(
                this, settings, adDisplayContainer
        );

    }

    private void setupCastListener() {
        Log.e("CAST","setupCastListener called");
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                invalidateOptionsMenu();


            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }


            private void onApplicationConnected(CastSession castSession) {
                Log.e("CAST","onApplicationConnected called");
                mCastSession = castSession;
                if (null != mSelectedMedia) {
                    loadRemoteMedia(0, true);
                    return;
                }

                invalidateOptionsMenu();
            }
        };

    }
    private void loadRemoteMedia(int position, boolean autoPlay) {
        Log.e("CAST","loadRemoteMedia called");
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }

           remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
                @Override
                public void onStatusUpdated() {
                    super.onStatusUpdated();
                    Log.v("okhttp","VIDEOPLAYER>>STATUS UPDTD");

//                    Intent intent = new Intent(VideoPlayerActivity.this, ExpandedControlsActivity.class);
//                    startActivity(intent);
                    remoteMediaClient.unregisterCallback(this);
                }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
//        remoteMediaClient.load(mSelectedMedia, autoPlay, position);
        MediaLoadOptions mediaLoadOptions = new MediaLoadOptions.Builder().build();
        try {
            Log.d(TAG, "loading media");
            remoteMediaClient.load(mSelectedMedia, mediaLoadOptions).setResultCallback(
                    new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
                        @Override
                        public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                            if (mediaChannelResult.getStatus().isSuccess()) {
                                // Since the player starts playing automatically we do not want to
                                // request the ad again in Chromecast except for VMAP because there
                                // are multiple ad breaks. To request a single ad use same the same
                                // message with current time as 0.

                                // sendMessage("requestAd," + mAdTagUrl + "," + 0);

//                                if(selectedVideoModel.getVideo_name() != null && !selectedVideoModel.getVideo_name().isEmpty())
//                                    sendMessage("requestAd," + selectedVideoModel.getVideo_name() + "," + 0);

                            } else {
                                Log.e(TAG, "Error loading Media : "
                                        + mediaChannelResult.getStatus().getStatusCode());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Problem opening media during loading", e);
        }
    }



    public void setupMedia(ASTVHome astvHome){
        Log.e("CAST","setupMedia called");
        //   mSelectedMedia = buildMediaInfo(videoModelz.getVideo_title(),token, "Adventure sports TV", "", 333, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/DesigningForGoogleCast.m3u8",
        mSelectedMedia = buildMediaInfo(astvHome.getChannelName(),token, "Adventure sports TV", "", 333, astvHome.getLiveLink().trim(),
                "application/x-mpegurl", ConstantUtils.CHANNEL_NEW_THUMBNAIL + astvHome.getLogo(), ConstantUtils.CHANNEL_NEW_THUMBNAIL + astvHome.getLogo(), null);
    }
    private static MediaInfo buildMediaInfo(String title,String tokenVal, String studio, String subTitle,
                                            int duration, String url, String mimeType, String imgUrl, String bigImageUrl,
                                            List<MediaTrack> tracks) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, studio);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        movieMetadata.addImage(new WebImage(Uri.parse(imgUrl)));
        movieMetadata.addImage(new WebImage(Uri.parse(bigImageUrl)));
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject();
            jsonObj.put(KEY_DESCRIPTION, tokenVal);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to add token to the json object", e);
        }

        return new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(mimeType)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .setStreamDuration(duration * 1000)
                .setCustomData(jsonObj)
                .build();
    }
    @Override
    protected void onResume() {
        shouldAutoPlay = true;
        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);

        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }

        if((mCastSession != null) && mCastSession.isConnected()){
            // mMediaRouteButton.setVisibility(View.VISIBLE);
            mMediaRouteButton.setVisibility(View.INVISIBLE);
        }else{
            //  mMediaRouteButton.setVisibility(View.VISIBLE);
            mMediaRouteButton.setVisibility(View.INVISIBLE);
        }
        if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.resume();
        } else{
            resumePlayer();
        }
        super.onResume();


    }

    public void goToHome() {
        Intent intentH = new Intent(ChannelLivePlayerActivity.this, MainHomeActivity.class);
       // Intent intentH = new Intent(ChannelLivePlayerActivity.this, HomeActivity.class);
        intentH.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentH);
        finish();
        overridePendingTransition(0,0);

    }

    private void showProgressDialog() {
        exo_player_view.setVisibility(View.INVISIBLE);
        tv_more_videos.setVisibility(View.INVISIBLE);
        rv_video_grid.setVisibility(View.INVISIBLE);

        progressDialog.show();
    }


    private void setupRecyclerView() {

        rv_live_schedule.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        liveScheduleAdapter = new LiveScheduleAdapter(ChannelLivePlayerActivity.this, getApplicationContext(), this::onScheduleClicked);
        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.default_spacing_small);
        rv_live_schedule.setAdapter(liveScheduleAdapter);
        loadingSchedule = Skeleton.bind(rv_live_schedule)
                .adapter(liveScheduleAdapter)
                .load(R.layout.item_live_schedule)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();


        rv_video_grid.setNestedScrollingEnabled(false);
        rv_video_grid.setLayoutManager(new GridLayoutManager(this, 3));
        rv_video_grid.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
//        rv_video_grid.setNestedScrollingEnabled(false);
//        rv_video_grid.setLayoutManager(new GridLayoutManager(this, 1,GridLayoutManager.HORIZONTAL, false));
        // vodLiveAdapter = new VodLiveAdapter(getApplicationContext(), this::onVodItemClicked, false);
        vodLiveAdapter = new VodLiveAdapter(getApplicationContext(), this::onVodItemClicked, true);
        rv_video_grid.setAdapter(vodLiveAdapter);

        loadingVideos = Skeleton.bind(rv_video_grid)
                .adapter(vodLiveAdapter)
                .load(R.layout.loading_videos_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

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

  /*  private void loadHome() {
//"android-phone",
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable homeDisposable = usersService.getChannelHome(HappiApplication.getAppToken()
                , CHANNEL_ID, userId,
                SharedPreferenceUtility.getCountryCode(), "android-phone",
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelResponse -> {

                            if (channelResponse.getData().size() != 0) {
                                tHome = channelResponse.getData();
                                // populateHome(tvexcelResponse.getData());
                                checkPremium(tHome);
                                Log.v("TestHome", "Test");
                            } else {
                                displayErrorLayout(getString(R.string.no_results_found));
                            }

                        },
                        throwable -> {

                            displayErrorLayout(getString(R.string.server_error));
                        }
                );
        compositeDisposable.add(homeDisposable);
    }*/

    private void loadChannelDetails() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable channelDisposable = usersService.getChannelHome(HappiApplication.getAppToken(),
                CHANNEL_ID, userId, SharedPreferenceUtility.getCountryCode(), "android-phone",
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(astvHomeResponse -> {
                    if (astvHomeResponse.getData().size() != 0) {
                        tHome = astvHomeResponse.getData();
                        checkPremium(tHome);
                    } else {
                        displayErrorLayout(getString(R.string.no_results_found));
                    }
                }, throwable -> {
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(channelDisposable);

    }

    private void populateHome(List<ASTVHome> data) {
        String LIVE_URL="";
        boolean isLiveAvailable = false;
        pHome = data.get(0);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        gmtStartTime = df.format(new Date());
        Bundle params = new Bundle();
        params.putString("user_id", String.valueOf(userId));
        params.putString("channel_id", String.valueOf(pHome.getChannelId()));
        params.putString("channel_name", pHome.getChannelName());
        params.putString("channel_start_time", gmtStartTime);
        mFirebaseAnalytics.logEvent("watch_channel_start", params);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(pHome.getChannelId()));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pHome.getChannelName());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "channel");
        // tv_channel_title.setText(pHome.getChannelName());
        tv_channel_title_live.setText("");
        Glide.with(ChannelLivePlayerActivity.this)
                .load(ConstantUtils.CHANNEL_NEW_THUMBNAIL + pHome.getLogo())
                .error(Glide.with(ChannelLivePlayerActivity.this).load(ContextCompat.getDrawable(ChannelLivePlayerActivity.this, R.drawable.ic_placeholder)))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(centerCropTransform())
                .into(iv_icon);
        //xxc    // if (pHome.getLiveFlag() == 1) {
        if (pHome.getLiveFlag() == 1) {
            isLiveAvailable = true;
            exo_player_view.setVisibility(View.VISIBLE);
            if (exoPlayer == null) {
                LIVE_URL = pHome.getLiveLink();
                //getToken(pHome);
                ipAddressApiCall(pHome);
            }
        } else {
            isLiveAvailable = false;
            exo_player_view.setVisibility(GONE);
        }
        /* if (*//*Util.SDK_INT <= 23 ||*//* exoPlayer == null) {
            if (isLiveAvailable) LIVE_URL = pHome.getLiveLink();
            getToken(pHome);
        }*/

    }

    private void loadVodToLive() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable vodToLiveDisposable = usersService.getSimilarListForLive(HappiApplication.getAppToken(),SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vodToLiveResponseModel -> {
                    if (vodToLiveResponseModel.getData().size() != 0) {
                        updateLiveVideoList(vodToLiveResponseModel.getData());
                    } else {
                        //displayErrorLayout(getString(R.string.no_results_found));
                        displayErrorLayout("");
                    }
                }, throwable -> {
                    //displayErrorLayout(getString(R.string.server_error));
                    displayErrorLayout("");
                });
        compositeDisposable.add(vodToLiveDisposable);
    }

    private void updateLiveVideoList(List<VodToLiveModel> liveVideoList) {
        rv_video_grid.setVisibility(View.VISIBLE);
        tv_more_videos.setVisibility(View.VISIBLE);

        vodLiveAdapter.clearAll();
        vodLiveAdapter.addAll(liveVideoList);
        loadingVideos.hide();
        vodLiveAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_grid, mSelectedItem);
        if (vodLiveAdapter.isEmpty()) {

            rv_video_grid.setVisibility(GONE);
            //displayErrorLayout(getString(R.string.no_results_found));
            displayErrorLayout("");
        }

    }

    private void updateVideoDataList(List<VideoModel> videoModelList) {
        rv_video_grid.setVisibility(View.VISIBLE);
        tv_more_videos.setVisibility(View.VISIBLE);

        videoList_adapter_new.clearAll();
        videoList_adapter_new.addAll(videoModelList);
        loadingVideos.hide();
        videoList_adapter_new.notifyDataSetChanged();
        runLayoutAnimation(rv_video_grid, mSelectedItem);
        if (videoList_adapter_new.isEmpty()) {

            rv_video_grid.setVisibility(GONE);
        }
    }

    private void displayErrorLayout(String message) {
        //popular_channels.setVisibility(View.GONE);
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        tv_more_videos.setVisibility(GONE);
        popular_channel_videos.setVisibility(GONE);
        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }

    @Override
    protected void onDestroy() {
        shouldAutoPlay = false;
        if(timerSChedule != null) {
            timerSChedule.cancel();
        }
        if(timerOrientation != null){
            timerOrientation.cancel();
        }
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtTime = df.format(new Date());

        Bundle params = new Bundle();
        params.putString("user_id", String.valueOf(userId));
        params.putString("channel_id", String.valueOf(pHome.getChannelId()));
        params.putString("channel_start_time", gmtStartTime);
        params.putString("channel_end_time", gmtTime);
        params.putString("channel_name", pHome.getChannelName());
        mFirebaseAnalytics.logEvent("watch_channel_end", params);

        safelyDispose(compositeDisposable);

        if(mAdsManager != null) {
            mAdsManager.removeAdEventListener(this);
            mAdsLoader.removeAdErrorListener(this);
            mAdsManager.destroy();
            mAdsManager = null;
            Log.d("ima_ads", "ondestroy");
        }
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        SharedPreferenceUtility.setIsLiveVisible(false);
        //mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void resumePlayer() {
        exo_player_view.findViewById(R.id.ll_exoplayer_parent_live).setVisibility(View.VISIBLE);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {

        shouldAutoPlay = false;
        //AD INSERTION ***************
        if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.pause();
        } else {
            releasePlayer();
        }
        //mRewardedVideoAd.pause(this);
        super.onPause();

    }

    private void getToken(ASTVHome pHome) {
        objectHome = pHome;
        ApiClient.UsersService usersService = ApiClient.createToken();
        Disposable tokenDisposable = usersService.getVideoToken(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenResponse>() {
                    @Override
                    public void accept(TokenResponse tvexcelResponse) {
                        token = tvexcelResponse.getData().trim();
                        // checkPremium();
                        if(!isForceLogout){
                            initializePlayer(objectHome);
                        }else{
                            loginExceededAlertSubscription();
                        }

                        // demoPlayer(objectHome);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {


                    }
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void checkPremium(List<ASTVHome> tHome) {

        pHome = tHome.get(0);

        String premiumFlag = "";
        String payPerViewFlag = "";
        String rentalFlag = "";
        premiumFlag = pHome.getPremium_flag();
        payPerViewFlag = pHome.getPayper_flag();
        rentalFlag = pHome.getRental_flag();

        if ((premiumFlag != null && !premiumFlag.isEmpty() && premiumFlag.equals("1")) ||
                (payPerViewFlag != null && !payPerViewFlag.isEmpty() && payPerViewFlag.equals("1")) ||
                (rentalFlag != null && !rentalFlag.isEmpty() && rentalFlag.equals("1"))){

            ApiClient.UsersService usersService = ApiClient.create();
            Disposable channelSubscriptionDisposable = usersService.getChannelSubscriptions(HappiApplication.getAppToken(), pHome.getChannelId(), userId, "android-phone", SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(channelsubscriptionResponseModel -> {
                        if (channelsubscriptionResponseModel.getData().size() != 0) {

                            boolean hasSubscribed = false;
                            channelSubscriptionModelList = channelsubscriptionResponseModel.getData();


                            if (channelSubscriptionModelList.size() != 0) {
                                ArrayList<String> subscriptionItemList = new ArrayList<>();

                                for (ChannelSubscriptionModel item : channelSubscriptionModelList) {
                                    subscriptionItemList.add(item.getSubscription_id());
                                }

                                SharedPreferenceUtility.setSubscriptionItemIdList(subscriptionItemList);
                            }


                            if (subscriptionIds.size() != 0) {

                                for (ChannelSubscriptionModel channelSubscriptionModel : channelSubscriptionModelList) {

                                    for (String subId : subscriptionIds) {

                                        if (channelSubscriptionModel.getSubscription_id().equals(subId)) {
                                            hasSubscribed = true;
                                            break;

                                        }
                                    }//inner for

                                }//outer for
                                if (hasSubscribed) {
                                    HappiApplication.setIsNewSubscriber(false);
                                    playVideo(tHome);

                                } else {

                                    goToPremiumContent();
                                }
                            } else {
                                //subidlist is 0
                                goToPremiumContent();
                            }
                        } else {
                            //response size is 0
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(this, "Unable to fetch data now. Please try again", Toast.LENGTH_SHORT).show();

                        }


                    }, throwable -> {
                        //no response
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(this, "Subscription Error", Toast.LENGTH_SHORT).show();
                        super.onBackPressed();
                    });
            compositeDisposable.add(channelSubscriptionDisposable);

        }else{
            playVideo(tHome);
        }
    }

    private void playVideo(List<ASTVHome> tHome) {
        exo_player_view.setVisibility(View.VISIBLE);
        tv_more_videos.setVisibility(View.VISIBLE);
        rv_video_grid.setVisibility(View.VISIBLE);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // initializePlayer(pHome);
        //loadHome();
        populateHome(tHome);
        // loadVideoList();
        loadVodToLive();
        // loadScheduleDetails();
        //ipAddressApiCall();
    }
    private void loadScheduleDetails(){

        Calendar currentCal = Calendar.getInstance(Locale.getDefault());
        Date currentDate = currentCal.getTime();

        String today = getDate(currentDate);
        String start = "00:00:00";
        String end = "23:59:59";
        String startOfSelectedDate = today +" "+ start;
        String endOfSelectedDate = today + " "+ end;

        String tzDateStart = getDateFromLocalToTimezone(startOfSelectedDate);
        String tzDateEnd = getDateFromLocalToTimezone(endOfSelectedDate);



        //  Date dateUtc = getTimeLocalToUTC(currentDate);
        //  String dateForTz = getTimeUtcToTimeZone(dateUtc);

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable scheduleDisposable = usersService.getScheduleUpdated(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), String.valueOf(CHANNEL_ID), tzDateStart, tzDateEnd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scheduleUpdatedResponseModel -> {

                    if(scheduleUpdatedResponseModel.getData().size() != 0){
                        liveScheduleList = scheduleUpdatedResponseModel.getData();
                        updateNowWatching(liveScheduleList);
                        List<ScheduleUpdatedModel> updatedList = updateScheduleList(liveScheduleList);
                        liveScheduleList.clear();
                        liveScheduleList.addAll(updatedList);
                        updateSchedule(updatedList);
                        // updateSchedule(liveScheduleList);

                    }else{
                        // ll_schedule.setVisibility(View.GONE);
                        rv_live_schedule.setVisibility(GONE);
                    }

                },throwable -> {
                    ll_schedule.setVisibility(GONE);
                    Log.e("!@#$%^&*","exception : ");
                });

        compositeDisposable.add(scheduleDisposable);
    }
    private void updateSchedule(List<ScheduleUpdatedModel> scheduleList){
        rv_live_schedule.setVisibility(View.VISIBLE);

        //liveScheduleAdapter = new LiveScheduleAdapter(getApplicationContext(),scheduleList, this::onScheduleClicked);
        // rv_live_schedule.setAdapter(liveScheduleAdapter);

        runLayoutAnimation(rv_live_schedule, mSelectedItem);
        liveScheduleAdapter.clearAll();
        liveScheduleAdapter.addAll(scheduleList);
        loadingSchedule.hide();
        liveScheduleAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_live_schedule, mSelectedItem);
        if (liveScheduleAdapter.isEmpty()) {

            rv_live_schedule.setVisibility(GONE);
        }

    }
    private List<ScheduleUpdatedModel> updateScheduleList(List<ScheduleUpdatedModel> listForUpdation){
        List<ScheduleUpdatedModel> updatedList = new ArrayList<>();
        int nextIndex = 0;
      /*  for(int i = 0; i<listForUpdation.size(); i++){
            if(updatedList.get(i).isNext()){
                break;
            }else{
                updatedList.remove(i);
            }
        }*/
       /* for(ScheduleUpdatedModel model : updatedList){
            if(model.isNext()){
                break;
            }else{
                updatedList.remove(model);
            }
        }*/

        for(int i = 0; i<listForUpdation.size(); i++){
            if(listForUpdation.get(i).isNext()){
                nextIndex = i;
                break;
            }else{
                String fdf="dvdf";
            }
        }
        if(nextIndex != 0) {
            for (int index = nextIndex; index < listForUpdation.size(); index++) {
                updatedList.add(listForUpdation.get(index));
            }
        }else{
            updatedList = listForUpdation;
        }


        return updatedList;
    }
    private void updateNowWatching(List<ScheduleUpdatedModel> listModel){
        if(listModel.size() != 0){
            ll_schedule.setVisibility(View.VISIBLE);
            for(int index = 0; index < listModel.size(); index++){
                boolean live = isLive(listModel.get(index), index);
                if(live){
                    String time = getShowTime(listModel.get(index).getStart_date_time())
                            +" - "+ getShowTime(listModel.get(index).getEnd_date_time());
                    tv_live_now_title.setText(listModel.get(index).getVideo_title());
                    tv_schedule_timing.setText(time);
                    if(index++ <= liveScheduleList.size()){
                        liveScheduleList.get(index++).setNext(true);
                        break;
                    }
                    break;
                }
            }
            if(!isLiveAvailableForSchedule){
                ll_schedule.setVisibility(GONE);
            }
        }else{
            ll_schedule.setVisibility(GONE);
        }
    }
    private void goToPremiumContent() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        SharedPreferenceUtility.setChannelId(CHANNEL_ID);
        Intent intent = new Intent(ChannelLivePlayerActivity.this, SubscriptionActivity.class);
        intent.putExtra("from", "channelplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }


    private void openFullscreen() {

        isExoPlayerFullscreen = true;
        actionBarHeight = rl_toolbar.getHeight();

        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ChannelLivePlayerActivity.this, R.drawable.ic_fullscreen_skrink));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            exo_player_view.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        } else {

            exo_player_view.setLayoutParams(new RelativeLayout.LayoutParams(height, width));
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 0, 0, 0);
        rl_exoplayer_parent.setPadding(0, 0, 0, 0);

        rl_exoplayer_parent.setLayoutParams(params);
        rl_btm_navigation_channel.setVisibility(GONE);
        rl_toolbar.setVisibility(GONE);
        rl_video_grid.setVisibility(GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hideNavigationBar();
    }

    private void closeFullscreen() {


        isExoPlayerFullscreen = false;

        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ChannelLivePlayerActivity.this, R.drawable.ic_fullscreen_white));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout.LayoutParams exo_params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.dimen_player_250dp));

        //exo_params.addRule(RelativeLayout.BELOW, R.id.about_layout);
        exo_player_view.setLayoutParams(exo_params);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, actionBarHeight, 0, actionBarHeight);
        rl_exoplayer_parent.setPadding(0, 0, 0, 0);
        rl_exoplayer_parent.setLayoutParams(params);
        rl_btm_navigation_channel.setVisibility(View.VISIBLE);
        rl_toolbar.setVisibility(View.VISIBLE);
        rl_video_grid.setVisibility(View.VISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

   /* class NetworkThread extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... arg0) {
            //Your implementation
            try{
                URL url = new URL(arg0[0]);
                InputStream inpStrm = (InputStream) url.getContent();
                HashMap<String,Integer> data=parseHLSMetadata(inpStrm);
                Log.e("^^^^",data.toString());
            }catch(Exception e){
                Log.e("^^^^",e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            // TODO: do something with the feed
        }
    }*/

    private void initializePlayer(ASTVHome pHome) {
        mSelectedMedia = null;
        mediaDataSourceFactory = buildDataSourceFactory(true);
        Handler mainHandler = new Handler();
        try {
            Uri videoURI = Uri.parse(pHome.getLiveLink().trim());
            //Uri videoURI = Uri.parse("https://gizmeon.s.llnwi.net/vod/PUB-50023/202009291601356793/playlist~360p.m3u8");


            //Uri videoURI = Uri.parse("https://content.uplynk.com/channel/e1e04b2670174e93b5d5499ee73de095.m3u8");

            // Uri videoURI = Uri.parse("https://gizmeon.s.llnwi.net/livechannel/playlist.m3u8");
            //                          https://gizmeon.s.llnwi.net/livehls/ngrp:CHANNEL275_all/playlist.m3u8
            //   Uri videoURI = Uri.parse("http://gizmeon.s.llnwi.net/livehls/ngrp:CHANNEL275_all/playlist.m3u8");
            //  Uri videoURI = Uri.parse("https://giz.poppo.tv/live/275/playlist.m3u8");
//            Uri videoURI = Uri.parse("http://34.198.11.177:3002/1/playlist~360p.m3u8");
//            Uri videoURI = Uri.parse("https://gizmeon.s.llnwi.net/vod/201911051572939358/playlist.m3u8");

//            Uri videoURI = Uri.parse("http://34.198.11.177:3001/275/playlist.m3u8");
//            Uri videoURI = Uri.parse("http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8");
            // Uri videoURI = Uri.parse("https://gizmeon.s.llnwi.net/vod/ht9izz/playlist~1080p.m3u8");

            String adTagUriString = "";

            try {
                Log.d("ima_ads", "getAd_link>>"+pHome.getAdLink());
                  adTagUriString = FormatAdUrl.formatChannelAdUrl(pHome, ipAddressModel);
                  Log.e("ADTAG", adTagUriString);

                 initVastAd(adTagUriString);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            boolean needNewPlayer = exoPlayer == null;

            Log.e("needNewPlayer", needNewPlayer + "");
            /*try {
                 adpodURL = pHome.getAd_pod_url().trim();
                // Log.e("adpodURLTry", adpodURL + "");
            } catch (NullPointerException e) {
                // Log.e("adpodURLCatch", adpodURL + "adpodURLCatch");
                e.printStackTrace();

            }*/

            Log.e("ADTAG", adTagUriString);
            if (needNewPlayer) {
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
                exo_player_view.setPlayer(exoPlayer);
                exoPlayer.setPlayWhenReady(shouldAutoPlay);
                exo_player_view.findViewById(R.id.exo_fullscreen_icon);

                volume = exoPlayer.getVolume();
            }
            DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
            String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
            DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeterA);
            factory.getDefaultRequestProperties().set("token", token);

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
            Log.e("MainAcvtivity error", " exoplayer error " + e.toString());
        }
      /*  exoPlayer.addMetadataOutput(new MetadataOutput() {
            @Override
            public void onMetadata(Metadata metadata) {

                for (int i = 0; i < metadata.length(); i++) {
                    Metadata.Entry entry = metadata.get(i);
                    if (entry instanceof TextInformationFrame) {
                        TextInformationFrame textFrame = (TextInformationFrame) entry;
                        if ("TXXX".equals(textFrame.id)) {
                            if (textFrame.value.equalsIgnoreCase("start") && !isAdPlaying) {

                               // requestAds(adpodURL);

                                if (mAdsManager != null) {
                                    mAdsManager.start();
                                    isAdPlaying = true;
                                }

                            } else if (textFrame.value.equalsIgnoreCase("stop") && isAdPlaying) {
                                if (mAdsManager != null) {
                                    mAdsLoader.contentComplete();
                                    mAdsManager.destroy();
                                    mAdsManager = null;
                                }
                                isAdPlaying = false;
                                exoPlayer.setVolume(volume);
                                //requestAds(adpodURL);

                            }
                        }
                    }
                }
            }
        });*/

        exoPlayer.prepare(videoSource);
        durationSet = false;
        isTimerActive = false;
        channelId = String.valueOf(pHome.getChannelId());
        channelTitle = pHome.getChannelName();
        // setupMedia(pHome);

    }


  /*  private HashMap<String, Integer> parseHLSMetadata(InputStream i ){

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(i, "UTF-8"));
            String line;
            HashMap<String, Integer> segmentsMap = null;
            String digitRegex = "\\d+";
            Pattern p = Pattern.compile(digitRegex);

            while((line = r.readLine())!=null){
                if(line.equals("#EXTM3U")){ //start of m3u8
                    segmentsMap = new HashMap<String, Integer>();
                }else if(line.contains("#EXTINF")){ //once found EXTINFO use runner to get the next line which contains the media file, parse duration of the segment
                    Matcher matcher = p.matcher(line);
                    matcher.find(); //find the first matching digit, which represents the duration of the segment, dont call .find() again that will throw digit which may be contained in the description.
                    segmentsMap.put(r.readLine(), Integer.parseInt(matcher.group(0)));
                }
            }
            r.close();
            return segmentsMap;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/

   /* private void demoPlayer(ASTVHome pHome) {
        Handler mainHandler = new Handler();
        boolean needNewPlayer = exoPlayer == null;
       *//* if (needNewPlayer) {
            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);
            eventLogger = new EventLogger(trackSelector);
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
            boolean preferExtensionDecoders = false;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF); // drmSessionManager = null
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            exoPlayer.addListener(eventLogger);
            exoPlayer.addMetadataOutput(eventLogger);
            exoPlayer.addAudioDebugListener(eventLogger);
            exoPlayer.addVideoDebugListener(eventLogger);
            exo_player_view.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(shouldAutoPlay);
            exo_player_view.findViewById(R.id.exo_fullscreen_icon);

            volume = exoPlayer.getVolume();
        }*//*


//        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
//        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeterA);
//        factory.getDefaultRequestProperties().set("token", token);

        if (needNewPlayer) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

               *//* new DefaultBandwidthMeter.Builder(this)
                .setEventListener(mainHandler, bandwidthMeterEventListener)
                .build();*//*

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            exoPlayer.setPlayWhenReady(true);
            exo_player_view.findViewById(R.id.exo_fullscreen_icon);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getResources().getString(R.string.app_name)), bandwidthMeter);
            // HlsMediaSource videoSource = new HlsMediaSource(Uri.parse("http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8"), dataSourceFactory, 5, mainHandler, null);

            Uri videoURI = Uri.parse("http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8");
            videoSource = new HlsMediaSource(videoURI, dataSourceFactory, 1, null, null);
            exoPlayer.prepare(videoSource);
        }
    }*/

    private MediaSource buildMediaSource(Uri uri, String overrideExtension, @Nullable Handler handler, @Nullable MediaSourceEventListener listener) {
        @C.ContentType int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri) : Util.inferContentType("." + overrideExtension);

        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri, handler, listener);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri, handler, listener);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri, handler, listener);

            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri, handler, listener);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }


/*
    private void requestAds(String adTagUrl) {

        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        mAdsLoader.contentComplete();

        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mIsAdDisplayed || exoPlayer == null || exoPlayer.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(exoPlayer.getCurrentPosition(),
                        exoPlayer.getDuration());
            }
        });
        mAdsLoader.requestAds(request);
    }
*/

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {

        /*String username = "username";
        String password = "password";
        byte[] toEncrypt = (username + ":" + password).getBytes();
        String encoded = Base64.encodeToString(toEncrypt, Base64.DEFAULT);*/
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        // factory.getDefaultRequestProperties().set("Authorization","Basic "+encoded);
        return factory;
    }

    @Override
    public void onSuggestedItemClicked(int adapterPosition) {
        // releasePlayer();
        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY, suggestAdapter.getItem(adapterPosition).getChannelId());

    }

    @Override
    public void onItemClicked(int adapterPosition) {
        // releasePlayer();
        SharedPreferenceUtility.setVideoId(Integer.parseInt(videoList_adapter
                .getItem(adapterPosition).getShow_id()));
        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, videoList_adapter
                .getItem(adapterPosition).getShow_id());

    }
    private void initVastAd(String adRulesURL) {
        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(new com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();
                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(ChannelLivePlayerActivity.this);
                mAdsManager.addAdEventListener(ChannelLivePlayerActivity.this);
                mAdsManager.init();
            }
        });
        requestAds(adRulesURL);
    }
    private void requestAds(String adTagUrl) { // Create the ads request.
        if (adTagUrl.length() > 0) {
            //  if (!isAdcalling) {
            //      isAdcalling = true;
            Log.d("ima_ads", "adTagUrl>>"+adTagUrl);
            AdsRequest request = mSdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setContentProgressProvider(new ContentProgressProvider() {
                @Override
                public VideoProgressUpdate getContentProgress() {

                    onPlayerSeekPosition();
                    if (mIsAdDisplayed || playerDuration <= 0) {
                        return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                    } else {
                        return new VideoProgressUpdate(
                                playerCurrentPosition,
                                playerDuration );
                    }
                }
            });

            // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
            mAdsLoader.requestAds(request);
            // }

        }
    }
    void onPlayerSeekPosition(
    ) {
        playerCurrentPosition = exoPlayer.getCurrentPosition();
        playerDuration = exoPlayer.getDuration();
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {

       /* if (mAdsManager != null) {
            mAdsManager.destroy();
            mAdsManager = null;
        }
        if (exoPlayer != null) {

            if (isFirstAdHandler) {
                exoPlayer.prepare(videoSource);
                isFirstAdHandler = false;
            }
            exoPlayer.setVolume(volume);
            isAdPlaying = false;
            // requestAds(adpodURL);

        }

        AdError adError = adErrorEvent.getError();
        callAddErrorAnalyticsApi(String.valueOf(adError.getErrorCode().getErrorNumber()), adError.getMessage().toString());*/

        try {
            int errorCodeNumber = adErrorEvent.getError().getErrorCodeNumber();
            int errorNumber = adErrorEvent.getError().getErrorCode().getErrorNumber();
            String errorType = adErrorEvent.getError().getErrorType().toString();
            String errorMessage = adErrorEvent.getError().getMessage();
            Log.d("ima_ads", "onAdError: " + errorCodeNumber + ">" + errorNumber + ">" + errorType + ">" + errorMessage);

            //call analytics
            AdError adError = adErrorEvent.getError();
            callAddErrorAnalyticsApi(String.valueOf(adError.getErrorCode().getErrorNumber()), adError.getMessage().toString());


            //if (isAdcalling) {
            Log.d(
                    "ima_ads",
                    "adEvent errorCodeNumber: " + errorCodeNumber + " errorNumber: " + errorNumber + " errorType: " + errorType + " errorMessage: " + errorMessage
            );
        } catch (Exception ex) {
            Log.d("ima_ads", "onAdError: catch");

        }

        //isAdcalling = false

        playVideo();
    }
    private void pauseVideo() {
        Log.d("ima_ads", "pauseVideo");
        exo_player_view.findViewById(R.id.ll_exoplayer_parent_live).setVisibility(View.INVISIBLE);
        if(exoPlayer != null && exoPlayer.getPlayWhenReady()){
            exoPlayer.setPlayWhenReady(false);
        }
    }
    private void playVideo() {
        Log.d("ima_ads", "playVideo");
        exo_player_view.findViewById(R.id.ll_exoplayer_parent_live).setVisibility(View.VISIBLE);
        if(exoPlayer != null && !exoPlayer.getPlayWhenReady() && shouldAutoPlay){
            exoPlayer.setPlayWhenReady(true);
        }
    }
    @Override
    public void onAdEvent(AdEvent adEvent) {
      /*  switch (adEvent.getType()) {
            case LOADED:
                mAdsManager.start();
                *//*if(isFirstAd){
                    mAdsManager.start();
                    isFirstAd=false;
                }*//*
                break;
            case CONTENT_PAUSE_REQUESTED:
                mIsAdDisplayed = true;
                volume = exoPlayer.getVolume();
                exoPlayer.setVolume(0);
                break;
            case CONTENT_RESUME_REQUESTED:
                isAdPlaying = false;
                if (isFirstAdHandler) {
                    exoPlayer.prepare(videoSource);
                    isFirstAdHandler = false;
                }
                exoPlayer.setVolume(volume);
                //requestAds(adpodURL);
                break;
            case ALL_ADS_COMPLETED:
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                //requestAds(adpodURL);
                break;
            default:
                break;
        }*/
        switch (adEvent.getType()) {

            case LOADED : {
                Log.d("ima_ads", "adEvent LOADED");
                if(shouldAutoPlay){
                    mAdsManager.start();
                    mIsAdDisplayed = true;
                    Log.d("ima_ads", "adEvent LOADED:started");
                }

                break;
            }
            case STARTED : {

                Log.d("ima_ads", "adEvent STARTED");
                if(!shouldAutoPlay && mAdsManager != null){
                    mAdsManager.pause();
                    //mIsAdDisplayed = false;
                    Log.d("ima_ads", "adEvent STARTED:pause");
                }
                break;
            }
            case FIRST_QUARTILE : {

                Log.d("ima_ads", "adEvent FIRST_QUARTILE");
                if(!shouldAutoPlay && mAdsManager != null){
                    mAdsManager.pause();
                    //mIsAdDisplayed = false;
                    Log.d("ima_ads", "adEvent FIRST_QUARTILE:pause");
                }
                break;
            }
            case THIRD_QUARTILE : {

                Log.d("ima_ads", "adEvent THIRD_QUARTILE");
                if(!shouldAutoPlay && mAdsManager != null){
                    mAdsManager.pause();
                    // mIsAdDisplayed = false;
                    Log.d("ima_ads", "adEvent THIRD_QUARTILE:pause");
                }
                break;
            }
            case CONTENT_PAUSE_REQUESTED : {

                Log.d("ima_ads", "adEvent CONTENT_PAUSE_REQUESTED");
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video ad is played.

                //isAdcalling = true;
                if(!shouldAutoPlay && mAdsManager != null){
                    mAdsManager.pause();
                    //mIsAdDisplayed = false;
                    Log.d("ima_ads", "adEvent CONTENT_PAUSE_REQUESTED:pause");
                }else{
                    mIsAdDisplayed = true;
                    pauseVideo();
                }


                break;
            }
            case CONTENT_RESUME_REQUESTED : {
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.

                Log.d("ima_ads", "adEvent CONTENT_RESUME_REQUESTED");
                mIsAdDisplayed = false;
                // isAdcalling = false;

                playVideo();

                break;
            }
            case COMPLETED : {
                Log.d("ima_ads", "adEvent COMPLETED");

                break;
            }
            case ALL_ADS_COMPLETED : {
                Log.d("ima_ads", "adEvent ALL_ADS_COMPLETED");
                if(mAdsManager != null){
                    mIsAdDisplayed = false;
                    mAdsManager.destroy();
                    mAdsManager = null;
                    Log.d("ima_ads", "adEvent ALL_ADS_COMPLETED:pause");
                }

                break;
            }
        }
    }

    private void getSessionToken() {
        showProgressDialog();
        rl_exoplayer_parent.setVisibility(View.VISIBLE);
        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();

        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), appKey, bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {
                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());

                            //  loadHome();
                            checkUserSubscription();
                            // loadVideoList();
                            //  ipAddressApiCall();
                        }, throwable -> {
                            displayErrorLayout("Some error occurred. Please try again after sometime.");

                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void ipAddressApiCall(ASTVHome data) {

        ApiClient.IpAddressApiService ipAddressApiService = ApiClient.createIPService();
        Disposable ipDisposable = ipAddressApiService.fetchIPAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ipAddressModelResponse -> {
                    HappiApplication.setIpAddress(ipAddressModelResponse.getQuery());
                    ipAddressModel = ipAddressModelResponse;
                    getToken(data);
                }, throwable -> {

                });
        compositeDisposable.add(ipDisposable);
    }

    @Override
    public void onBackPressed() {

        if(timerSChedule != null) {
            timerSChedule.cancel();
        }

        if (isExoPlayerFullscreen) {
           // closeFullscreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            if(HappiApplication.isIsNewSubscriber()){
                goToHome();
            }else{
                super.onBackPressed();
                finish();
                overridePendingTransition(0,0);
            }

        }

    }


    @Override
    public void onVodItemClicked(int adapterPosition) {
        try {
            Integer vid = vodLiveAdapter.getItem(adapterPosition).getVideo_id();
            SharedPreferenceUtility.setVideoId(vid);
            ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, vid);
            finish();
            overridePendingTransition(0,0);
        } catch (Exception e) {
            Log.e("DDDD", e.getMessage());
        }

    }
    private void task(){
        TextView timerTV = exo_player_view.findViewById(R.id.exo_position);
        String getTime = timerTV.getText().toString();
        if(isLivePlaying){

            //  Log.e("########","getTime:  "+getTime);
            long milliseconds = 0L;
            String[] timeArray = getTime.split(":");
            if(timeArray.length == 3){

                int hr = Integer.parseInt(timeArray[0]);
                int min = Integer.parseInt(timeArray[1]);
                int sec = Integer.parseInt(timeArray[2]);
                //  milliseconds = ((hr * 60 * 60)+(min * 60)+sec) * 1000; //for time in milliseconds
                milliseconds = ((hr * 60 * 60)+(min * 60)+sec);

            }else if(timeArray.length == 2){

                int min = Integer.parseInt(timeArray[0]);
                int sec = Integer.parseInt(timeArray[1]);
                //milliseconds = ((min * 60)+sec) * 1000;  //for time in milliseconds
                milliseconds = ((min * 60)+sec);

            }
            if(milliseconds != 0L && milliseconds % 60 == 0){
                if(isLivePlaying){
                    Toast.makeText(this, " TRUE  : "+milliseconds,Toast.LENGTH_SHORT).show();
                    liveEventAnalyticsApiCall("POP03");

                    // Log.e("########","TRUE "+milliseconds);
                }

            }else{
                // Toast.makeText(this, "FALSE  :  "+milliseconds,Toast.LENGTH_SHORT).show();
                //  Log.e("########","FALSE "+milliseconds);
            }
        }
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
        videoDetails.addProperty("channel_id",channelId);
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
    private void callAddErrorAnalyticsApi(String errorCode, String errorMessage){
        //Uncomment to enable analytics api call
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;

        String device_id = SharedPreferenceUtility.getAdvertisingId();

        JsonObject errorDetails = new JsonObject();
        errorDetails.addProperty("device_id",device_id);
        errorDetails.addProperty("user_id",String.valueOf(SharedPreferenceUtility.getUserId()));
        errorDetails.addProperty("event_type","POP08");
        errorDetails.addProperty("error_code",errorCode);
        errorDetails.addProperty("error_message",errorMessage);
        errorDetails.addProperty("timestamp",String.valueOf(epoch));
        errorDetails.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
        errorDetails.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
        errorDetails.addProperty("video_id","0");
        errorDetails.addProperty("channel_id",channelId);
        errorDetails.addProperty("video_title",channelTitle);
        errorDetails.addProperty("publisherid",SharedPreferenceUtility.getPublisher_id());

        try{
            Log.e("000##",": api call is about to be made:  "+"POP08"+" - ");

            AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
            Call<String> stringCall = analyticsServiceScalar.eventCall2(errorDetails);
            stringCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("000##","success: "+"POP08"+" - "+response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("000##","failure: "+"POP08"+" - "+t.getMessage());
                }
            });
        }catch(Exception ex){
            Log.e("000##",": exception :  "+"POP08"+" - "+ex.toString());
        }
    }

    @Override
    public void onScheduleClicked(int adapterPosition) {
        //Toast.makeText(this,"item id : "+liveScheduleList.get(adapterPosition).getUniqueid(), Toast.LENGTH_SHORT).show();
        //popupwindow
        LiveSchedulePopUpClass liveSchedulePopUpClass = new LiveSchedulePopUpClass(HappiApplication.getCurrentContext(), liveScheduleList.get(adapterPosition),this::notificationOn, this::notificationOff);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        liveSchedulePopUpClass.show();
    }
    public String getDate(Date current){
        String today = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            today = sdf.format(current);
        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }


        return today;
    }

    public String getDateFromLocalToTimezone(String local){
        String tz = SharedPreferenceUtility.getChannelTimeZone();
        String tzDate = "";
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone t = TimeZone.getDefault();
        sdfLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdfTZ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfTZ.setTimeZone(TimeZone.getTimeZone(tz));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            Date d1 = sdf.parse(local);
            String d2 = sdfTZ.format(d1);
            Date d3 = sdfLocal.parse(d2);
            tzDate = sdf.format(d3);

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }
        return tzDate;
    }

    private String getShowTime(String time){

        String localDate = "";
        localDate = getTimeTzToLocal(time);


        return localDate;
    }
    private boolean isLive(ScheduleUpdatedModel scheduleItem, int position){
        boolean isPgmLive = false;
        Calendar today = Calendar.getInstance();

        String startDate = scheduleItem.getStart_date_time();
        String endDate = scheduleItem.getEnd_date_time();

        Calendar localDateStart = getDateTzToLocal(startDate);
        Calendar localDateEnd = getDateTzToLocal(endDate);

        Date todayDate = today.getTime();
        try{

            if(todayDate.after(localDateStart.getTime()) && todayDate.before(localDateEnd.getTime())){
                liveScheduleList.get(position).setLive(true);
                isPgmLive = true;
                isLiveAvailableForSchedule = true;
            }else{
                liveScheduleList.get(position).setLive(false);
                isPgmLive = false;
            }
        }catch(Exception ex){
            Log.e("DATE ERROR", ""+ex.getMessage());
        }
        return isPgmLive;
    }


    public Calendar getDateTzToLocal(String time){
        Calendar local = Calendar.getInstance();
        String tz = SharedPreferenceUtility.getChannelTimeZone();
        SimpleDateFormat sdfTz = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfTz.setTimeZone(TimeZone.getTimeZone(tz));
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone t = TimeZone.getDefault();
        sdfLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            Date d1 = sdfLocal.parse(time);
            String d2 = sdfLocal.format(d1);
            Date d3 = sdfTz.parse(d2);
            local.setTime(d3);

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }


        return local;
    }
    public String getTimeTzToLocal(String tzTime){
        String local = "";
        String tz = SharedPreferenceUtility.getChannelTimeZone();
        SimpleDateFormat sdfTz = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfTz.setTimeZone(TimeZone.getTimeZone(tz));
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone t = TimeZone.getDefault();
        sdfLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");

        try{
            Date d1 = sdfLocal.parse(tzTime);
            String d2 = sdfLocal.format(d1);
            Date d3 = sdfTz.parse(d2);
            String d4 = sdf.format(d3);
            String d5 = sdf2.format(sdf.parse(d4));
           /* String[] d5 = d4.split(" ");
            if(d5.length > 0){
                local = d5[0] +" ";
                String[] d6 = d5[1].split(":");
                if(d6.length > 0){
                    local = local + d6[0] +":"+ d6[1] +" ";
                }
                local = local + d5[2];
            }*/
            local = d5;

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }

        return local;
    }


    @Override
    public void notificationOn(ScheduleUpdatedModel scheduleUpdatedModel) {
        long epoch = 0L;
        String videoTitle = "";
        int channelId = -1;
        int uniqueId = 0;
        epoch = scheduleUpdatedModel.getEpoch();
        videoTitle = scheduleUpdatedModel.getVideo_title();
        channelId = scheduleUpdatedModel.getChannel_id();
        uniqueId = scheduleUpdatedModel.getUniqueid();
//        Calendar curr = Calendar.getInstance();
//        curr.add(Calendar.MINUTE,5);
//        epoch = curr.getTime().getTime();
        NotificationTriggerActivity.sendNotifications(epoch, videoTitle, channelId, uniqueId,ChannelLivePlayerActivity.this);
        ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
        listOfIds.add(uniqueId);
        SharedPreferenceUtility.setNotificationIds(listOfIds);
    }

    @Override
    public void notificationOff(ScheduleUpdatedModel scheduleUpdatedModel) {

        int unique_id = 0;
        unique_id = scheduleUpdatedModel.getUniqueid();
        NotificationTriggerActivity.clearNotification(unique_id, ChannelLivePlayerActivity.this);
        ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
        int index = listOfIds.indexOf(unique_id);
        if(index!=-1) {
            listOfIds.remove(index);
            SharedPreferenceUtility.setNotificationIds(listOfIds);
        }
    }

    private void showLoginOrRegisterAlert() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        rl_exoplayer_parent.setVisibility(GONE);
        SharedPreferenceUtility.setChannelId(CHANNEL_ID);
        String message = "Please Login or Register to continue.";
        LoginRegisterAlert alertDialog =
                new LoginRegisterAlert(this, message, "Ok", "Cancel", this::onLoginRegisterNegativeClick,
                        this::onLoginRegisterNeutralClick, false);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onLoginRegisterNegativeClick() {
        goToLoginScreen();
    }

    @Override
    public void onLoginRegisterNeutralClick() {
        ChannelLivePlayerActivity.this.finish();
        overridePendingTransition(0,0);
    }

    private void goToLoginScreen(){

        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        SharedPreferenceUtility.setChannelId(CHANNEL_ID);
        Intent intent = new Intent(ChannelLivePlayerActivity.this, SubscriptionLoginActivity.class);
        intent.putExtra("from" , "channelplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        ChannelLivePlayerActivity.this.finish();
        overridePendingTransition(0,0);
    }



    private void checkUserSubscription() {
        showProgressDialog();
        rl_exoplayer_parent.setVisibility(View.VISIBLE);
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel -> {
                    isForceLogout = subscriptionResponseModel.isForcibleLogout();

                    List<String> subids = new ArrayList<>();
                    if (subscriptionResponseModel.getData().size() != 0) {

                        List<UserSubscriptionModel> userSubscriptionModelList = subscriptionResponseModel.getData();

                        if (userSubscriptionModelList.size() != 0) {
                            for (UserSubscriptionModel model : userSubscriptionModelList) {
                                subids.add(model.getSub_id());
                            }

                        }

                    }
                    HappiApplication.setSub_id(subids);
                    if(isForceLogout){
                        loginExceededAlertSubscription();
                    }else{
                        loadChannelDetails();
                    }

                }, throwable -> {
                    displayErrorLayout("Some error occurred. Please try again after sometime.");
                   // Toast.makeText(ChannelLivePlayerActivity.this, "Server Error. Please try again after sometime.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(subscriptionDisposable);
    }
    public void loginExceededAlertSubscription() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String message = "You are no longer Logged in this device. Please Login again to access.";
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(ChannelLivePlayerActivity.this, "ok", message, "Ok", "", null, null, this::onOkClickNeutral, null);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
        releasePlayer();
    }

    @Override
    public void onOkClickNeutral() {
        logoutApiCall();
    }
    private void logoutApiCall() {

        progressDialog.show();
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

                        releasePlayer();
                        goToLogin();
                    } else {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(this, "Unable to logout. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("Logout", "api call failed");
                    }

                }, throwable -> {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, "Unable to logout. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("Logout", "api call failed");
                });

        compositeDisposable.add(logoutDisposable);
    }
    private void goToLogin(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Intent intent = new Intent(ChannelLivePlayerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        ChannelLivePlayerActivity.this.finish();
        overridePendingTransition(0,0);
    }

    private void getNetworkIP() {
        boolean isMobileData = false;
        boolean isWifi = false;
        String ipAddress = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
            HappiApplication.setIpAddress(ipAddress);
        }
        if (isMobileData) {
            ipAddress = getMobileIpAddress();
            HappiApplication.setIpAddress(ipAddress);
        }
        //Log.e("1234###","ipAddressFinal: "+ipAddressFinal);
    }

    private String getWifiIpAddress() {
        @SuppressWarnings("deprecation")
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    private String getMobileIpAddress() {
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
            Log.e("mobipaddr", "exception: " + ex.toString());
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isExoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            openFullscreenDialog();
        }else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            closeFullscreenDialog();
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private void openFullscreenDialog() {

        isExoPlayerFullscreen = true;
        actionBarHeight = rl_toolbar.getHeight();
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ChannelLivePlayerActivity.this, R.drawable.ic_fullscreen_skrink));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exo_player_view.getLayoutParams();
        params.width = params.MATCH_PARENT;
        params.height = params.MATCH_PARENT;

        params.setMargins(0, 0, 0, 0);
        rl_exoplayer_parent.setPadding(0, 0, 0, 0);
        rl_exoplayer_parent.setLayoutParams(params);

        exo_player_view.setLayoutParams(params);


        rl_btm_navigation_channel.setVisibility(GONE);
        rl_toolbar.setVisibility(GONE);
        rl_video_grid.setVisibility(GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startTimer();
    }


    private void closeFullscreenDialog() {

        isExoPlayerFullscreen = false;
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ChannelLivePlayerActivity.this, R.drawable.ic_fullscreen_white));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exo_player_view.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = (int) getResources().getDimension(R.dimen.dimen_player_250dp);

        exo_player_view.setLayoutParams(params);


        RelativeLayout.LayoutParams paramsParent = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        paramsParent.setMargins(0, actionBarHeight, 0, actionBarHeight);
        rl_exoplayer_parent.setPadding(0, 0, 0, 0);

        rl_exoplayer_parent.setLayoutParams(paramsParent);

        rl_btm_navigation_channel.setVisibility(View.VISIBLE);
        rl_toolbar.setVisibility(View.VISIBLE);
        rl_video_grid.setVisibility(View.VISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startTimer();

    }
    private void startTimer(){
        /*Handler handler = new Handler();
        Runnable update = new Runnable() {
            public void run() {
                if(isOrientationChange){
                    Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER FIN", Toast.LENGTH_SHORT).show();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    isOrientationChange = false;
                    if (orientationTimer != null) {
                        orientationTimer.cancel();
                    }
                }

            }
        };

        if (orientationTimer != null) {
            orientationTimer.cancel();
        }
        orientationTimer = new Timer();
        orientationTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 4000);*/




        if(timerOrientation != null){
            timerOrientation.cancel();
        }

        timerOrientation = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(isOrientationChange){
                    //    Toast.makeText(HappiApplication.getCurrentContext(), "LISTNER FIN", Toast.LENGTH_SHORT).show();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    isOrientationChange = false;
                }
                if(timerOrientation != null){
                    timerOrientation.cancel();
                }

            }
        }.start();

    }

    @Override
    protected void onStop() {
        shouldAutoPlay = false;
        releasePlayer();
        super.onStop();
    }
}
