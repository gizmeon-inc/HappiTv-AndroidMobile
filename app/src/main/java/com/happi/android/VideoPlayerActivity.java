package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.cast.ExpandedControlsActivity;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.LoginRegisterAlert;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.exoplayercontroller.EventLogger;
import com.happi.android.exoplayercontroller.TrackSelectionHelper;
import com.happi.android.models.IPAddressModel;
import com.happi.android.models.SelectedVideoModel;
import com.happi.android.models.ShowModel;
import com.happi.android.models.TokenResponse;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.models.VideoSubscriptionModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.AnalyticsApi;
import com.happi.android.webservice.ApiClient;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdError;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.UiElement;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.WebImage;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener,
        AdErrorEvent.AdErrorListener, AdEvent.AdEventListener, Cast.MessageReceivedCallback,
        ShowList_adapter.itemClickListener,
        LoginRegisterAlert.OnLoginRegisterUserNeutral, LoginRegisterAlert.OnLoginRegisterUserNegative,
        CustomAlertDialog.OnOkClick{

    private String token = "";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private PlayerView exo_player_view;
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory mediaDataSourceFactory;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private Handler mainHandler;
    private EventLogger eventLogger;
    private boolean shouldAutoPlay = true;
    private int resumeWindow;
    private long resumePosition;
    private AdsLoader adsLoader;
    private ViewGroup adUiViewGroup;
    private boolean isExoPlayerFullscreen = false;
    private ImageView exo_fullscreen_icon;
    private RelativeLayout rl_exoplayer_parent;
    private RelativeLayout rl_toolbar;
    private LinearLayout ll_video_actions;
    private RelativeLayout rl_video_grid;
    private LinearLayout popular_channel_videos;
    private RelativeLayout rl_channel_title;
    private TypefacedTextViewRegular tv_more_videos;
    private CheckBox iv_heart;
    private GridRecyclerView rv_more_videos;
    private TypefacedTextViewRegular tv_errormsg;
    private ShowList_adapter showsAdapter;
    private CompositeDisposable compositeDisposable;
    private SkeletonScreen loadingChannels;
    private AnimationItem mSelectedItem;
    private int videoId = 0;
    private int userId = 0;
    private SelectedVideoModel videoModel;
    private SelectedVideoModel selectedVideoModel;
    private String gmtStartTime = "";
    private ImaSdkFactory mSdkFactory;
    private com.google.ads.interactivemedia.v3.api.AdsLoader mAdsLoader;
    private AdsManager mAdsManager;
    private boolean mIsAdDisplayed;
    private List<VideoSubscriptionModel> videoSubscriptionModelList;
    private ProgressDialog progressDialog;
    private MediaInfo mSelectedMedia;
    //cast
    private boolean isCasting = false;
    private CastContext mCastContext;
    private MediaRouteButton mMediaRouteButton;
    private FirebaseAnalytics mFirebaseAnalytics;
    private CastStateListener mCastStateListener;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private CastSession mCastSession;
    public static final String KEY_DESCRIPTION = "token";
    private static final String TAG = "VideoProvider";
    private static final String NAMESPACE = "urn:x-cast:com.google.ads.ima.cast";
    // private static final String NAMESPACE = "urn:x-cast:"+ConstantUtils.BASE_URL;

    /*Analytics*/

    private boolean durationSet = false;
    private boolean isVideoPlaying = false;
    private boolean isVideoPaused = false;
    private boolean isTimerActive = false;
    private boolean isVideoEnd = false;
    private boolean isVideoEndT = false;
    private Timer timerSChedule = null;
    private String videoTitle;
    private String videoIdEvent;
    private String channelId;
    private String category = "";

    //video description
    private RelativeLayout rl_video_metadata;
    private TypefacedTextViewSemiBold tv_video_name;
    private LinearLayout ll_drop_arrow;
    private ImageView iv_dropdown;
    private RelativeLayout rl_video_desc;
    private TypefacedTextViewRegular tv_video_desc;
    private boolean isVideoDescVisible = false;
    private boolean isVideoDescAvailable = false;
    //bottom navigation view
    private RelativeLayout rl_btm_navigation_video;
    private boolean isForceLogout = false;

    public void loadRemoteMedia() {
        Log.e("CAST", "loadRemoteMedia called");
        Log.v("okhttp","VIDEOPLAYER>>LOAD RMT MEDIA");

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

                Intent intent = new Intent(VideoPlayerActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                remoteMediaClient.unregisterCallback(this);
            }

            @Override
            public void onMetadataUpdated() {
                super.onMetadataUpdated();
            }

            @Override
            public void onQueueStatusUpdated() {
                super.onQueueStatusUpdated();
            }

            @Override
            public void onPreloadStatusUpdated() {
                super.onPreloadStatusUpdated();
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                super.onSendingRemoteMediaRequest();
            }

            @Override
            public void onAdBreakStatusUpdated() {
                super.onAdBreakStatusUpdated();
            }
        });


        MediaLoadOptions mediaLoadOptions = new MediaLoadOptions.Builder().build();
        try {
            Log.d(TAG, "loading media");
            Log.v("okhttp","VIDEOPLAYER>>.LOAD");

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
                                Log.v("okhttp","VIDEOPLAYER>> ERR"+mediaChannelResult.getStatus().getStatusCode());

                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Problem opening media during loading", e);
            Log.v("okhttp","VIDEOPLAYER>>.LOAD CATCH");
        }
    }


    public void setupMedia(SelectedVideoModel videoModelz) {
        Log.e("CAST", "setupMedia called");
        String studio = getResources().getString(R.string.app_name);
        String imageUrl = ConstantUtils.THUMBNAIL_URL + videoModelz.getThumbnail();
        //   mSelectedMedia = buildMediaInfo(videoModelz.getVideo_title(),token, "Adventure sports TV", "", 333, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/DesigningForGoogleCast.m3u8",
        mSelectedMedia = buildMediaInfo(videoModelz.getVideo_title(), token, studio, "", 333, videoModelz.getVideo_name().trim(),
                "application/x-mpegurl", imageUrl, imageUrl, null);
    }

    private static MediaInfo buildMediaInfo(String title, String tokenVal, String studio, String subTitle,
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("okhttp","VIDEOPLAYER>>ONCREATE");
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
        setContentView(R.layout.activity_video_player);


        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();

        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }
        if (HappiApplication.getIpAddress().isEmpty()) {
            getNetworkIP();
        }
        isForceLogout = false;
        //casting
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(this, mMediaRouteButton);

        videoModel = new SelectedVideoModel();
        compositeDisposable = new CompositeDisposable();

        videoSubscriptionModelList = new ArrayList<>();



        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        /*//for ad
         float mAspectRatio = 72f / 128;
         String mAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonly&cmsid=496&vid=short_onecue&correlator=";*/

        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);

        rl_btm_navigation_video = findViewById(R.id.rl_btm_navigation_video);
        rl_btm_navigation_video.setVisibility(View.VISIBLE);


        userId = SharedPreferenceUtility.getUserId();
        Intent intent = getIntent();
        videoId = intent.getIntExtra(ConstantUtils.VIDEO_DETAILS, 0);
        if (videoId == 0) {
            videoId = SharedPreferenceUtility.getVideoId();
        }
        exo_player_view = findViewById(R.id.exo_player_view);
        rl_exoplayer_parent = findViewById(R.id.rl_exoplayer_parent);
        rl_toolbar = findViewById(R.id.rl_toolbar);
        ll_video_actions = findViewById(R.id.ll_video_actions);
        rl_video_grid = findViewById(R.id.rl_video_grid);
        popular_channel_videos = findViewById(R.id.ll_popular_videos);

        TypefacedTextViewSemiBold tv_video_title = findViewById(R.id.tv_video_title);
        rl_channel_title = findViewById(R.id.rl_channel_title);
        TypefacedTextViewRegular tv_count = findViewById(R.id.tv_count);
        tv_more_videos = findViewById(R.id.tv_more_videos);
        iv_heart = findViewById(R.id.iv_heart);
        iv_heart.setVisibility(View.INVISIBLE);
        tv_video_title.setVisibility(View.INVISIBLE);
        tv_count.setVisibility(View.INVISIBLE);
        rl_channel_title.setVisibility(View.GONE);

        rv_more_videos = findViewById(R.id.rv_more_videos);
        tv_errormsg = findViewById(R.id.tv_errormsg);

        //video description
        rl_video_metadata = findViewById(R.id.rl_video_metadata);
        tv_video_name = findViewById(R.id.tv_video_name);
        ll_drop_arrow = findViewById(R.id.ll_drop_arrow);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        rl_video_desc = findViewById(R.id.rl_video_desc);
        tv_video_desc = findViewById(R.id.tv_video_desc);
        rl_video_metadata.setVisibility(View.GONE);
        rl_video_desc.setVisibility(View.GONE);
        isVideoDescVisible = false;
        isVideoDescAvailable = false;
        rl_video_metadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoDescAvailable) {
                    if (isVideoDescVisible) {
                        isVideoDescVisible = false;
                        iv_dropdown.setImageResource(R.drawable.ic_arrow_drop_down);
                        rl_video_desc.setVisibility(View.GONE);
                    } else {
                        isVideoDescVisible = true;
                        iv_dropdown.setImageResource(R.drawable.ic_arrow_drop_up);
                        rl_video_desc.setVisibility(View.VISIBLE);
                    }
                } else {
                    rl_video_desc.setVisibility(View.GONE);
                }

            }
        });

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        showProgressDialog();


        //Similar Videos List RecyclerView


        exo_fullscreen_icon = exo_player_view.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout exo_fullscreen_button = exo_player_view.findViewById(R.id.exo_fullscreen_button);
        exo_fullscreen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExoPlayerFullscreen) {
                    openFullscreen();


                } else {

                    closeFullscreen();
                }
            }
        });

        if (savedInstanceState != null) {
            isExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

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

        iv_heart.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (iv_heart.isPressed()) {

                likeDislikeApiCall(isChecked);
            }
        });
        RelativeLayout rl_video_details = findViewById(R.id.rl_video_details);
        ImageView iv_arrow_dropdown = findViewById(R.id.iv_arrow_dropdown);
        iv_arrow_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (iv_arrow_dropdown.getRotation() == 0) {

                    iv_arrow_dropdown.animate().rotation(180).start();
                    expand(rl_video_details);
                } else if (iv_arrow_dropdown.getRotation() == 180) {

                    iv_arrow_dropdown.animate().rotation(0).start();
                    collapse(rl_video_details);
                }
            }
        });


        ViewGroup mAdUiContainer = findViewById(R.id.exo_player_view);


        mSdkFactory = ImaSdkFactory.getInstance();

        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(mAdUiContainer);
        ImaSdkSettings settings = mSdkFactory.createImaSdkSettings();
        mAdsLoader = mSdkFactory.createAdsLoader(
                this, settings, adDisplayContainer);
        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(new com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.

                AdsRenderingSettings adsRenderingSettings = mSdkFactory.createAdsRenderingSettings();
                //adsRenderingSettings.setEnablePreloading(true);
                // adsRenderingSettings.setDisableUi(true);
                Set<UiElement> var = Collections.emptySet();
                adsRenderingSettings.setUiElements(var);

                mAdsManager = adsManagerLoadedEvent.getAdsManager();
                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(VideoPlayerActivity.this);
                mAdsManager.addAdEventListener(VideoPlayerActivity.this);
                mAdsManager.init(adsRenderingSettings);
            }
        });

        timerSChedule = new Timer();

    }

    private void showProgressDialog() {
        exo_player_view.setVisibility(View.INVISIBLE);
        tv_more_videos.setVisibility(View.INVISIBLE);

        progressDialog.show();
    }


    public static void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics()
                .density) * 2);
        //a.setDuration(500);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics()
                .density) * 2);
        v.startAnimation(a);
    }

    private void likeDislikeApiCall(boolean isChecked) {

        int likeUnlike = 0;
        if (isChecked) {
            likeUnlike = 1;
        }

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable = usersService.LikeVideo(HappiApplication.getAppToken(),
                videoId, userId, likeUnlike, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(likeUnlikeModel -> {

                    if (likeUnlikeModel.getLiked_flag() == 0) {

                        videoModel.setLiked_flag(0);
                    } else if (likeUnlikeModel.getLiked_flag() == 1) {

                        videoModel.setLiked_flag(1);
                    }

                }, throwable -> {

                    if (iv_heart.isChecked()) {
                        iv_heart.setChecked(false);
                    } else {
                        iv_heart.setChecked(true);
                    }
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void getToken(SelectedVideoModel videoModel) {

        ApiClient.UsersService usersService = ApiClient.createToken();
        Disposable tokenDisposable = usersService.getVideoToken(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenResponse>() {
                    @Override
                    public void accept(TokenResponse tvexcelResponse) {

                        token = tvexcelResponse.getData().trim();
                        Log.e("TOKEN", token);

                        if(!isForceLogout){
                            initializePlayer(videoModel);
                        }else{
                            loginExceededAlertSubscription();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout();
                    }
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void ipAddressApiCall() {

        ApiClient.IpAddressApiService ipAddressApiService = ApiClient.createIPService();
        Disposable ipDisposable = ipAddressApiService.fetchIPAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ipAddressModel -> {
                    HappiApplication.setIpAddress(ipAddressModel.getQuery());
                    IPAddressModel ipAddressModelLocal = ipAddressModel;
                }, throwable -> {

                });
        compositeDisposable.add(ipDisposable);
    }

    private void setupRecyclerview() {
        tv_more_videos.setVisibility(View.VISIBLE);

        showsAdapter = new ShowList_adapter(getApplicationContext(), this::onShowsItemClicked, true);
        rv_more_videos.setNestedScrollingEnabled(false);
        rv_more_videos.setLayoutManager(new GridLayoutManager(this, 3));
        rv_more_videos.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        rv_more_videos.setAdapter(showsAdapter);

        loadingChannels = Skeleton.bind(rv_more_videos)
                .adapter(showsAdapter)
                .load(R.layout.loading_videos_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();

    }


    //load similar shows
    private void loadSimilarShowList() {
        tv_more_videos.setVisibility(View.VISIBLE);

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable similarShowDisposable = usersService.getSimilarShows(HappiApplication.getAppToken(),
                String.valueOf(userId), SharedPreferenceUtility.getPublisher_id(), String.valueOf(videoId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showListResponseModel -> {
                    if (showListResponseModel.getShowModelList().size() != 0) {
                        updateShowDataList(showListResponseModel.getShowModelList());
                    } else {
                        displayErrorLayout();
                    }

                }, throwable -> {
                    displayErrorLayout();
                });
        compositeDisposable.add(similarShowDisposable);
    }

    //Update show Adapter
    private void updateShowDataList(List<ShowModel> showModelList) {

        if (!showsAdapter.isEmpty()) {
            showsAdapter.clearAll();
        }

        showsAdapter.addAll(showModelList);
        loadingChannels.hide();
        runLayoutAnimation(rv_more_videos, mSelectedItem);
        if (showsAdapter.isEmpty()) {
            displayErrorLayout();
        }

    }

    private void displayErrorLayout() {

        rv_more_videos.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(getString(R.string.no_results_found));
    }

    private void updateWatchlist() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.updateWatchlist(HappiApplication.getAppToken
                (), videoId, userId, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(basicResponse -> {

                }, throwable -> {

                });
        compositeDisposable.add(videoDisposable);
    }

    private void initializePlayer(SelectedVideoModel videoModel) {
        mSelectedMedia = null;
        exo_player_view.setVisibility(View.VISIBLE);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        gmtStartTime = df.format(new Date());
        //Log.e("1234###","gmtStartTime: "+gmtStartTime);

        Bundle params = new Bundle();
        params.putString("user_id", String.valueOf(userId));
        params.putString("video_id", String.valueOf(videoModel.getVideo_id()));
        params.putString("video_title", videoModel.getVideo_title());
        params.putString("video_channel_id", videoModel.getChannel_id());
        params.putString("video_channel_name", videoModel.getChannel_name());
        params.putString("video_start_time", gmtStartTime);
        mFirebaseAnalytics.logEvent("watch_video_start", params);

        updateWatchlist();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        try {

            if ((videoModel.getVideo_name() != null) && (videoModel.getVideo_name().length() > 0)) {
                Uri videoURI = Uri.parse(videoModel.getVideo_name().trim());
                // Uri videoURI = Uri.parse("https://content.uplynk.com/channel/e1e04b2670174e93b5d5499ee73de095.m3u8");
                // Uri videoURI = Uri.parse("http://34.198.11.177:3001/275/playlist.m3u8");

                //  Uri videoURI = Uri.parse("https://content.uplynk.com/channel/e1e04b2670174e93b5d5499ee73de095.m3u8");
                // Uri videoURI = Uri.parse("http://playertest.longtailvideo.com/adaptive/oceans_aes/oceans_aes.m3u8");

                String adTagUriString = "";
                try {
                    // adTagUriString = FormatAdUrl.formatAdUrl(videoModel, ipAddressModel);
                    Log.e("ADTAG", adTagUriString);

                } catch (NullPointerException e) {
                    // Log.e("ADTAGCatch", "catch");
                    e.printStackTrace();
                }

                //  String adTagUriString = FormatAdUrl.formatAdUrl(videoModel, ipAddressModel);
                //String adTagUriString ="https://ads.poppo.tv/vmap?pid=194&width=480&height=854&dnt=0&ip=202.83.55.194&lat=10.005351&lon=76.3459466&ua=Mozilla%2F5.0+%28Linux%3B+Android+7.0%3B+N5001L+Build%2FNRD90M%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chrome%2F73.0.3683.90+Mobile+Safari%2F537.36&advid=ef5ffc62-8d83-4fca-ae50-7bbeec384175&uuid=[UUID]&country=IN&deviceid=a42d262f50218f&kwds=[KEYWORDS]&device_model=N5001L&device_make=NUU&channelid=194&videoid=947&bundleid=com.poppotv.android&appname=poppotv&totalduration=[DURATION]&description_url=https://play.google.com/store/apps/details?id=com.poppotv.android&hl=en";
                //  String adTagUriString ="https://pubads.g.doubiileclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonly&cmsid=496&vid=short_onecue&correlator=";

                boolean needNewPlayer = exoPlayer == null;

                if (needNewPlayer) {
                    TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                    DefaultTrackSelector trackSelector1 = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
                    TrackSelectionHelper trackSelectionHelper = new TrackSelectionHelper(trackSelector1, adaptiveTrackSelectionFactory);
                    eventLogger = new EventLogger(trackSelector1);

                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
                    //boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);
                    boolean preferExtensionDecoders = false;
                    DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
                            null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF); // drmSessionManager = null

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
                    exoPlayer.addListener(eventLogger);
                    exoPlayer.addMetadataOutput(eventLogger);
                    exoPlayer.addAudioDebugListener(eventLogger);
                    exoPlayer.addVideoDebugListener(eventLogger);
                    exo_player_view.setPlayer(exoPlayer);
                    exoPlayer.setPlayWhenReady(shouldAutoPlay);
                    exo_player_view.findViewById(R.id.exo_fullscreen_icon);


                    exoPlayer.addListener(new Player.DefaultEventListener() {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                            if (playbackState == Player.STATE_ENDED) {

                                if (isExoPlayerFullscreen) {
                                    closeFullscreen();
                                }
                                exoPlayer.setPlayWhenReady(false);
                                try {
                                    isVideoPlaying = false;
                                    isVideoPaused = false;
                                    isVideoEnd = true;
                                    isVideoEndT = true;
                                    if (timerSChedule != null) {
                                        isTimerActive = false;
                                        timerSChedule.cancel();
                                    }
                                    //video end
                                    if (isVideoEnd) {
                                        isVideoEnd = false;
                                        videoEventAnalyticsApiCall("POP05");
                                    }
                                } catch (Exception ex) {
                                    Log.e("QWERT3", "EXCEPTION : END : " + ex.getMessage());
                                }




                            } else if (playWhenReady && playbackState == Player.STATE_READY) {
                                // media actually playing
                                try {
                                    isVideoPlaying = true;
                                    isVideoPaused = false;
                                    isVideoEndT = false;

                                    //video start
                                    if (!durationSet) {
                                        durationSet = true;
                                        videoEventAnalyticsApiCall("POP02");
                                    }
                                    if (isVideoPlaying) {
                                        if (!isTimerActive) {
                                            initializeTimerScheduler("POP03");
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.e("QWERT3", "EXCEPTION : END : " + ex.getMessage());
                                }


                            } else if (playWhenReady) {
                                Log.e("1234###", "duration : ");
                                // might be idle (plays after prepare()),
                                // buffering (plays when data available)
                                // or ended (plays when seek away from end)
                            } else {
                                // player paused in any state
                                try {
                                    isVideoPlaying = false;
                                    isVideoPaused = true;

                                    //video paused

                                    if (isVideoPaused) {
                                        isVideoPaused = false;
                                        if (timerSChedule != null) {
                                            isTimerActive = false;
                                            timerSChedule.cancel();
                                        }
                                        if (!isVideoEndT) {
                                            videoEventAnalyticsApiCall("POP04");
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.e("QWERT3", "EXCEPTION : END : " + ex.getMessage());
                                }

                            }

                        }
                    });


                }

                MediaSource mediaSource = buildMediaSource(videoURI, null, mainHandler, eventLogger);



                boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
                if (haveResumePosition) {
                    exoPlayer.seekTo(resumeWindow, resumePosition);
                }
                exoPlayer.prepare(mediaSource);
                durationSet = false;
                isVideoEnd = false;
                isVideoEndT = false;
                isTimerActive = false;

                if (videoModel.getCategory_name().size() != 0) {
                    category = videoModel.getCategory_name().get(0);
                }
                if (videoModel.getCategory_name().size() > 0) {
                    for (int i = 1; i < videoModel.getCategory_name().size(); i++) {
                        category = category + ", " + videoModel.getCategory_name().get(i);
                    }
                }
                videoTitle = videoModel.getVideo_title();
                videoIdEvent = videoModel.getVideo_id();
                channelId = videoModel.getChannel_id();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        setupMedia(videoModel);
    }

    private void initializeTimerScheduler(String event) {//edit timer
        timerSChedule = new Timer();
        timerSChedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isVideoPlaying) {
                    Log.e("########", "TRUE  ");
                    isTimerActive = true;
                    videoEventAnalyticsApiCall(event);
                }
            }
        }, 0, 60000);
    }


    private void videoEventAnalyticsApiCall(String eventType) {

        /*Uncomment to enable analytics api call for VIDEO*/

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;

        String device_id = SharedPreferenceUtility.getAdvertisingId();

        JsonObject videoDetails = new JsonObject();
        videoDetails.addProperty("device_id", device_id);
        videoDetails.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
        videoDetails.addProperty("event_type", eventType);
        videoDetails.addProperty("video_id", videoIdEvent);
        videoDetails.addProperty("video_title", videoTitle);
        videoDetails.addProperty("channel_id", channelId);
        videoDetails.addProperty("category", String.valueOf(category));
        videoDetails.addProperty("timestamp", String.valueOf(epoch));
        videoDetails.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
        videoDetails.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
        videoDetails.addProperty("publisherid", SharedPreferenceUtility.getPublisher_id());

        try {
            Log.e("000##", ": api call is about to be made:  " + eventType + " - ");
            AnalyticsApi.AnalyticsServiceScalar analyticsService = AnalyticsApi.createScalar();
            Call<String> callS = analyticsService.eventCall2(videoDetails);
            callS.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("000##", "success: " + eventType + " - " + response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("000##", "failure: " + eventType + " - " + t.toString());
                }
            });

        } catch (Exception ex) {
            Log.e("000##", "exception: " + eventType + " - " + ex);
        }
    }


    private void requestAds(String adTagUrl) {
        // Create the ads request.

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


        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }

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

    private MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) throws Exception {
        Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
        if (adsLoader == null) {
            adsLoader = (AdsLoader) loaderClass.getConstructor(Context.class, Uri.class)
                    .newInstance(this, adTagUri);
            adUiViewGroup = new FrameLayout(this);
            exo_player_view.getOverlayFrameLayout().addView(adUiViewGroup);
        }


        AdsMediaSource.MediaSourceFactory adMediaSourceFactory =
                new AdsMediaSource.MediaSourceFactory() {
                    @Override
                    public MediaSource createMediaSource(
                            Uri uri, @Nullable Handler handler, @Nullable MediaSourceEventListener listener) {
                        return VideoPlayerActivity.this.buildMediaSource(
                                uri, /* overrideExtension= */ null, handler, listener);
                    }

                    @Override
                    public int[] getSupportedTypes() {
                        return new int[]{C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                    }
                };
        return new AdsMediaSource(
                mediaSource, adMediaSourceFactory, adsLoader, adUiViewGroup, mainHandler, eventLogger);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        factory.getDefaultRequestProperties().set("token", token);
        return factory;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        shouldAutoPlay = true;
        clearResumePosition();
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("okhttp","VIDEOPLAYER>>ONSTART");
        if (Util.SDK_INT > 23) {


        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        HappiApplication.setCurrentContext(this);

        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
        Log.v("okhttp","VIDEOPLAYER>>ONRESUME");
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastContext.addCastStateListener(mCastStateListener);
        if (mCastSession == null) {
            //  Log.v("okhttp","VIDEOPLAYER>>CAST SESS NULL");
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }

        if ((mCastSession != null) && mCastSession.isConnected()) {
            //   Log.v("okhttp","VIDEOPLAYER>>CAST SESS CONN"+mCastSession.isConnected());
            mMediaRouteButton.setVisibility(View.VISIBLE);
        } else {
            mMediaRouteButton.setVisibility(View.VISIBLE);
        }


        /*if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.resume();
        } else if (exoPlayer != null) {
            resumePlayer();
        }*/

        if(!isCasting){
            resumePlayer();
        }

        super.onResume();



    }

    private void getSelectedVideo(int videoId) {


        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.getSelectedVideo(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), "android-phone", videoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedVideoResponseModel -> {

                    videoModel = selectedVideoResponseModel.getSelectedVideoModelList().get(0);
                    selectedVideoModel = videoModel;
                    checkPremium(selectedVideoModel);


                }, throwable -> {

                    Toast.makeText(this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                });
        compositeDisposable.add(videoDisposable);

    }

    private void checkPremium(SelectedVideoModel selectedVideoModel) {

        String premiumFlag = "";
        String payPerViewFlag = "";
        String rentalFlag = "";
        premiumFlag = selectedVideoModel.getPremium_flag();
        payPerViewFlag = selectedVideoModel.getPayper_flag();
        rentalFlag = selectedVideoModel.getRental_flag();

        if ((premiumFlag != null && !premiumFlag.isEmpty() && premiumFlag.equals("1")) ||
                (payPerViewFlag != null && !payPerViewFlag.isEmpty() && payPerViewFlag.equals("1")) ||
                (rentalFlag != null && !rentalFlag.isEmpty() && rentalFlag.equals("1"))) {
            ApiClient.UsersService usersService = ApiClient.create();
            Disposable videoSubscriptionDisposable = usersService.getVideoSubscriptions(HappiApplication.getAppToken(),
                    videoId, userId, "android-phone",
                    SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(videosubscriptionResponseModel -> {

                        if (videosubscriptionResponseModel.getData().size() != 0) {
                            videoSubscriptionModelList = videosubscriptionResponseModel.getData();

                            if (videoSubscriptionModelList.size() != 0) {
                                ArrayList<String> subscriptionItemList = new ArrayList<>();

                                for (VideoSubscriptionModel item : videoSubscriptionModelList) {
                                    subscriptionItemList.add(item.getSubscription_id());
                                }

                                SharedPreferenceUtility.setSubscriptionItemIdList(subscriptionItemList);
                            }

                            Log.d("videoSubsc", "" + videoSubscriptionModelList.size());
                            List<String> subscriptionIds = HappiApplication.getSub_id();
                            if (subscriptionIds.size() != 0) {
                                boolean hasSubscribed = false;
                                for (VideoSubscriptionModel videoSubscriptionModelItem : videoSubscriptionModelList) {

                                    for (String subId : subscriptionIds) {

                                        if (videoSubscriptionModelItem.getSubscription_id().equals(subId)) {
                                            hasSubscribed = true;
                                            break;

                                        }
                                    }//inner for loop

                                }//outer for loop
                                if (hasSubscribed) {

                                    playVideo(selectedVideoModel);
                                } else {
                                    //not subscribed : show premium details page
                                    goToPremiumContent();

                                }
                            } else {
                                //subIdList empty
                                //show premium details page

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
                        Toast.makeText(this, "Unable to fetch data now. Please try again", Toast.LENGTH_SHORT).show();
                        super.onBackPressed();

                    });
            compositeDisposable.add(videoSubscriptionDisposable);
        } else {
            playVideo(selectedVideoModel);
        }


    }

    private void playVideo(SelectedVideoModel chosenVideoModel) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
        exo_player_view.setVisibility(View.VISIBLE);
        tv_more_videos.setVisibility(View.VISIBLE);
        HappiApplication.setIsNewSubscriber(false);
        setVideoDetails();
        getToken(chosenVideoModel);
        ipAddressApiCall();
        setupRecyclerview();
        loadSimilarShowList();
    }

    private void goToPremiumContent() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
        SharedPreferenceUtility.setVideoId(videoId);
        //Intent intent = new Intent(VideoPlayerActivity.this, PremiumVideoDetailsActivity.class);
        Intent intent = new Intent(VideoPlayerActivity.this, SubscriptionActivity.class);
        intent.putExtra("from", "videoplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);

    }

    private void setupCastListener() {
        Log.e("CAST", "setupCastListener called");
        Log.v("okhttp","VIDEOPLAYER>>CAST LIST");

        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                Log.v("okhttp","VIDEOPLAYER>>SESS ENDED");

                invalidateOptionsMenu();


            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                Log.v("okhttp","VIDEOPLAYER>>SESS RESUMED");

                onApplicationConnected(session);
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                Log.v("okhttp","VIDEOPLAYER>>SESS STARTED");

                onApplicationConnected(session);
            }

            @Override
            public void onSessionStarting(CastSession session) {
                Log.v("okhttp","VIDEOPLAYER>>SESS STRNG");
            }



            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                Log.v("okhttp","VIDEOPLAYER>>SESS FAILED");

                invalidateOptionsMenu();
            }

            @Override
            public void onSessionEnding(CastSession session) {
                Log.v("okhttp","VIDEOPLAYER>>SESS ENDNG");

            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
                Log.v("okhttp","VIDEOPLAYER>>SESS RESMNG");

            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                Log.v("okhttp","VIDEOPLAYER>>SESS RES FLD");

                invalidateOptionsMenu();
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
                Log.v("okhttp","VIDEOPLAYER>>SESS SUSPND");

            }


            private void onApplicationConnected(CastSession castSession) {
                Log.e("CAST", "onApplicationConnected called");
                mCastSession = castSession;
                if (null != mSelectedMedia) {
                    loadRemoteMedia();
                    return;
                }

                invalidateOptionsMenu();
            }
        };
        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                switch (newState) {
                    case CastState.CONNECTED:
                        //disable exoplayer
                        isCasting = true;
                        if (exoPlayer != null) {
                            exoPlayer.setPlayWhenReady(false);
                        }
                        break;
                    case CastState.NOT_CONNECTED:
                        //enable exoplayer
                        isCasting = false;
                        if (exoPlayer != null) {
                            exoPlayer.setPlayWhenReady(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void setVideoDetails() {

        if ((videoModel.getVideo_title() != null) && (videoModel.getVideo_title().length() > 0)) {
            rl_video_metadata.setVisibility(View.VISIBLE);
            tv_video_name.setText(videoModel.getVideo_title());
        } else {
            rl_video_metadata.setVisibility(View.GONE);
        }

        if ((videoModel.getVideo_description() != null) && (videoModel.getVideo_description().length() > 0)) {

            isVideoDescAvailable = true;
            ll_drop_arrow.setVisibility(View.VISIBLE);
            tv_video_desc.setText(videoModel.getVideo_description());
        } else {
            isVideoDescAvailable = false;
            ll_drop_arrow.setVisibility(View.INVISIBLE);
            tv_video_desc.setText("");
        }

    }

    @Override
    public void onPause() {

        Log.v("okhttp","VIDEOPLAYER>>ONPAUSE");
        mCastContext.removeCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);

       /* if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.pause();
        } else if (Util.SDK_INT <= 23) {
            releasePlayer();
        }*/
        releasePlayer();
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        //if (Util.SDK_INT > 23) {
        releasePlayer();
        // }
        Log.v("okhttp","VIDEOPLAYER>>ONSTOP");
    }

    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            Uri loadedAdTagUri = null;
            exo_player_view.getOverlayFrameLayout().removeAllViews();
        }
    }

    private void resumePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void updateResumePosition() {
        resumeWindow = exoPlayer.getCurrentWindowIndex();
        resumePosition = Math.max(0, exoPlayer.getContentPosition());
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    private void openFullscreen() {
        isExoPlayerFullscreen = true;
        // adContainer.setVisibility(View.GONE);
        //  moPubBannerView.setVisibility(View.GONE);
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_fullscreen_skrink));
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

        rl_btm_navigation_video.setVisibility(View.GONE);
        rl_toolbar.setVisibility(View.GONE);
        ll_video_actions.setVisibility(View.GONE);
        rl_video_grid.setVisibility(View.GONE);
        rl_channel_title.setVisibility(View.GONE);
        popular_channel_videos.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void closeFullscreen() {

        int actionBarHeight;
        isExoPlayerFullscreen = false;

        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_fullscreen_white));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout.LayoutParams exo_params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.dimen_250dp)
        );
        //exo_params.addRule(RelativeLayout.BELOW, R.id.rl_video_title);
        exo_player_view.setLayoutParams(exo_params);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        actionBarHeight = rl_toolbar.getHeight();
        params.setMargins(0, actionBarHeight, 0, 0);
        rl_exoplayer_parent.setPadding(0, 0, 0, 0);

        rl_exoplayer_parent.setLayoutParams(params);
        rl_btm_navigation_video.setVisibility(View.VISIBLE);
        rl_toolbar.setVisibility(View.VISIBLE);
        ll_video_actions.setVisibility(View.GONE);
        rl_video_grid.setVisibility(View.VISIBLE);
        rl_channel_title.setVisibility(View.GONE);
        popular_channel_videos.setVisibility(View.VISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //  adContainer.setVisibility(View.VISIBLE);
        //  moPubBannerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (timerSChedule != null) {
            timerSChedule.cancel();
        }
        if (isExoPlayerFullscreen)
            closeFullscreen();
        else {

            releasePlayer();
            if (exoPlayer != null) {
                exoPlayer.stop();
            }

            if (HappiApplication.isIsNewSubscriber()) {
                goToHome();
            } else {
                super.onBackPressed();
                finish();
                overridePendingTransition(0,0);
            }

        }
    }

    public void goToHome() {
       // Intent intentH = new Intent(VideoPlayerActivity.this, HomeActivity.class);
        Intent intentH = new Intent(VideoPlayerActivity.this, MainHomeActivity.class);
        intentH.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentH);
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        if (timerSChedule != null) {
            timerSChedule.cancel();
        }
        isVideoPlaying = false;
        isVideoEnd = false;
        isVideoPaused = false;
        isVideoEndT = false;

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtTime = df.format(new Date());

        Bundle params = new Bundle();
        params.putString("user_id", String.valueOf(userId));
        params.putString("video_id", String.valueOf(videoModel.getVideo_id()));
        params.putString("video_title", videoModel.getVideo_title());
        params.putString("video_channel_id", videoModel.getChannel_id());
        params.putString("video_channel_name", videoModel.getChannel_name());
        params.putString("video_start_time", gmtStartTime);
        params.putString("video_end_time", gmtTime);
        mFirebaseAnalytics.logEvent("watch_video_end", params);

        //releaseAdsLoader();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        mMediaRouteButton.setVisibility(View.INVISIBLE);
        safelyDispose(compositeDisposable);
        Log.v("okhttp","VIDEOPLAYER>>ONDESTROY");
        super.onDestroy();
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
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
    public void onAdEvent(AdEvent adEvent) {


        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {
            case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.

                mAdsManager.start();
                break;
            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                mIsAdDisplayed = true;
                exoPlayer.setPlayWhenReady(false);
                break;
            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.


                exoPlayer.setPlayWhenReady(true);
                mIsAdDisplayed = false;

                break;
            case ALL_ADS_COMPLETED:
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e("Error", adErrorEvent.toString());
        exoPlayer.setPlayWhenReady(true);

        //call analytics
        AdError adError = adErrorEvent.getError();

        callAddErrorAnalyticsApi(String.valueOf(adError.getErrorCode().getErrorNumber()), adError.getMessage().toString());
    }
    private void showLoginOrRegisterAlert() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        SharedPreferenceUtility.setVideoId(videoId);
        String message = "Please Login or Register to continue.";
        LoginRegisterAlert alertDialog =
                new LoginRegisterAlert(this, message, "Ok", "Cancel", this::onLoginRegisterNegativeClick,
                        this::onLoginRegisterNeutralClick, false);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();


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
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());

                            checkUserSubscription();

                        }, throwable -> {
                            Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                            super.onBackPressed();
                        });
        compositeDisposable.add(tokenDisposable);
    }

    @Override
    public void onClick(View v) {

    }

    private void sendMessage(String message) {
        try {
            Log.d(TAG, "Sending message: " + message);
            mCastSession.sendMessage(NAMESPACE, message)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status result) {
                            if (!result.isSuccess()) {
                                Log.e(TAG, "Sending message failed");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while sending message", e);
        }
    }


    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        Log.e("CAST", "onMessageReceived: " + message);
    }


    @Override
    public void onShowsItemClicked(int adapterPosition) {
        SharedPreferenceUtility.setShowId(showsAdapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, showsAdapter.getItem(adapterPosition).getShow_id());
        finish();
    }

    private void callAddErrorAnalyticsApi(String errorCode, String errorMessage) {
        //uncomment for analytics api call

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;

        String device_id = SharedPreferenceUtility.getAdvertisingId();

        JsonObject errorDetails = new JsonObject();
        errorDetails.addProperty("device_id", device_id);
        errorDetails.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
        errorDetails.addProperty("event_type", "POP08");
        errorDetails.addProperty("error_code", errorCode);
        errorDetails.addProperty("error_message", errorMessage);
        errorDetails.addProperty("timestamp", String.valueOf(epoch));
        errorDetails.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
        errorDetails.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
        errorDetails.addProperty("video_id", videoIdEvent);
        errorDetails.addProperty("video_title", videoTitle);
        errorDetails.addProperty("channel_id ", channelId);
        errorDetails.addProperty("publisherid ", SharedPreferenceUtility.getPublisher_id());
        try {
            Log.e("000##", ": api call is about to be made:  " + "POP08" + " - ");

            AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
            Call<String> stringCall = analyticsServiceScalar.eventCall2(errorDetails);
            stringCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("000##", "success: " + "POP08" + " - " + response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("000##", "failure: " + "POP08" + " - " + t.getMessage());
                }
            });
        } catch (Exception ex) {
            Log.e("000##", ": exception :  " + "POP08" + " - " + ex.toString());
        }
    }



    private void goToLoginScreen(){

        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        SharedPreferenceUtility.setVideoId(videoId);
        Intent intent = new Intent(VideoPlayerActivity.this, SubscriptionLoginActivity.class);
        intent.putExtra("from" , "videoplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        VideoPlayerActivity.this.finish();
    }
    private void goToLogin(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Intent intent = new Intent(VideoPlayerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        VideoPlayerActivity.this.finish();
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

    private void checkUserSubscription() {

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
                    if(isForceLogout){
                        loginExceededAlertSubscription();
                    }else{
                        getSelectedVideo(videoId);
                    }
                    HappiApplication.setSub_id(subids);


                }, throwable -> {
                    Toast.makeText(VideoPlayerActivity.this, "Server Error. Please try again after sometime.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(subscriptionDisposable);
    }

    public void loginExceededAlertSubscription() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String message = "You are no longer Logged in this device. Please Login again to access.";
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(VideoPlayerActivity.this, "ok", message, "Ok", "", null, null, this::onOkClickNeutral, null);
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

    @Override
    public void onLoginRegisterNegativeClick() {
        goToLoginScreen();
    }
    @Override
    public void onLoginRegisterNeutralClick() {
        VideoPlayerActivity.this.finish();
    }
}