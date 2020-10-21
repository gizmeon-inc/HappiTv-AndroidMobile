package com.happi.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.happi.android.adapters.ShowDetailsAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.LoginRegisterAlert;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.exoplayercontroller.EventLogger;
import com.happi.android.exoplayercontroller.TrackSelectionHelper;
import com.happi.android.models.DataModel;
import com.happi.android.models.ShowMetaDataModel;
import com.happi.android.models.TokenResponse;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.models.VideoModelUpdated;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.utils.MySpannable;
import com.happi.android.webservice.ApiClient;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import at.blogc.android.views.ExpandableTextView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.gujun.android.taggroup.TagGroup;

public class ShowDetailsActivity extends BaseActivity implements LoginRegisterAlert.OnLoginRegisterUserNegative,
        LoginRegisterAlert.OnLoginRegisterUserNeutral {

    TagGroup tag_theme;
    //ImageView iv_menu;
    ImageView iv_back;
    //ImageView iv_logo_text;
    ImageView iv_show_image;
    TextView tv_show_name;
    TextView tv_resolution;
    TextView tv_producer;
    TextView tv_year;
    TextView tv_audio;
    TextView tv_director;
    TextView tv_cast;
    TextView tv_more_click;
    ExpandableTextView ex_synopsis;
    LinearLayout ll_synopsis_text;

    //labels
    TextView tv_producer_label;
    TextView tv_year_label;
    TextView tv_audio_label;
    TextView tv_theme_label;
    TextView tv_director_label;
    TextView tv_cast_label;
    TextView tv_synopsis_label;

    TypefacedTextViewRegular tv_title;
    //LinearLayout rl_end_icons;
    public RecyclerView rv_show_list;
    ShowDetailsAdapter showDetailsAdapter;
    String showId;
    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    //trailer
    private ProgressBar pb_trailer;
    private String token = "";
    private EventLogger eventLogger;
    private RelativeLayout rl_image;
    private PlayerView exo_player_view;
    private LinearLayout ll_watch_trailer;
    private LinearLayout ll_play_now;
    private LinearLayout ll_play_overlap;
    private boolean isPlayNow;
    private boolean isTrailerPlayable;
    private SimpleExoPlayer exoPlayer;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private DataSource.Factory mediaDataSourceFactory;
    private int resumeWindow;
    private long resumePosition;
    private boolean shouldAutoPlay = true;
    private ImageView exo_fullscreen_icon;
    private boolean isExoPlayerFullscreen = false;
    private RelativeLayout rl_toolbar;
    private RelativeLayout rl_details;
    private RelativeLayout rl_player;
    //private ScrollView sc_meta;
    private NestedScrollView sc_meta;
    private Integer videoId = 0;
    //save instance
    //public Integer scrollPosition = null;
    //public Parcelable recyclerViewState;
    //like, add to watchlist, share
    LinearLayout ll_icons;
    LinearLayout ll_like;
    LinearLayout ll_dislike;
    LinearLayout ll_watch_list;
    LinearLayout ll_share;
    ImageView iv_like;
    ImageView iv_dislike;
    TextView tv_dislike;
    TextView tv_like;
    ImageView iv_watch_list;
    boolean isLikeClicked = false;
    boolean isDisLikeClicked = false;
    boolean isAddToWatchLaterClicked = false;
    String descriptiontext = "";
    String showTitletext = "";
    public static Activity currentActivity;
    private boolean isFromWatchList = false;
    //linear layouts
    LinearLayout ll_producer;
    LinearLayout ll_year;
    LinearLayout ll_audio;
    LinearLayout ll_theme;
    LinearLayout ll_director;
    LinearLayout ll_cast;

    private int actionBarHeight = 0;
    //error layout
    private LinearLayout ll_error;
    private ImageView iv_errorimg;
    private TypefacedTextViewRegular tv_errormsg;

    //bottom navigation view
    private RelativeLayout rl_btm_navigation_show;

    private TextView tv_description;
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
        setContentView(R.layout.activity_show_details);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        currentActivity = this;

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String showDetails = "";
        if (getIntent().getStringExtra(ConstantUtils.SHOW_ID) != null) {
            showDetails = getIntent().getStringExtra(ConstantUtils.SHOW_ID);
        } else {
            showDetails = SharedPreferenceUtility.getShowId();
        }
        if (showDetails.contains("+")) {
            isFromWatchList = true;
            showId = showDetails.replace("+", "").trim();
        } else {
            isFromWatchList = false;
            showId = showDetails;
        }
        //like, add to watchlist, share
        ll_icons = findViewById(R.id.ll_icons);
        ll_like = findViewById(R.id.ll_like);
        ll_dislike = findViewById(R.id.ll_dislike);
        ll_watch_list = findViewById(R.id.ll_watch_list);
        ll_share = findViewById(R.id.ll_share);
        iv_like = findViewById(R.id.iv_like);
        iv_dislike = findViewById(R.id.iv_dislike);
        tv_dislike = findViewById(R.id.tv_dislike);
        tv_like = findViewById(R.id.tv_like);
        iv_watch_list = findViewById(R.id.iv_watch_list);

        ll_watch_list.setEnabled(false);
        ll_like.setEnabled(false);
        ll_dislike.setEnabled(false);


        compositeDisposable = new CompositeDisposable();


        tag_theme = findViewById(R.id.tag_theme);
      //  iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
     //   iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
      //  rl_end_icons = findViewById(R.id.rl_end_icons);
        rv_show_list = findViewById(R.id.rv_show_list);
        iv_show_image = findViewById(R.id.iv_show_image);
        tv_show_name = findViewById(R.id.tv_show_name);
        tv_resolution = findViewById(R.id.tv_resolution);
        tv_producer = findViewById(R.id.tv_producer);
        tv_year = findViewById(R.id.tv_year);
        tv_audio = findViewById(R.id.tv_audio);
        tv_director = findViewById(R.id.tv_director);
        tv_cast = findViewById(R.id.tv_cast);
        tv_more_click = findViewById(R.id.tv_more_click);
        ex_synopsis = findViewById(R.id.ex_synopsis);
        ll_synopsis_text = findViewById(R.id.ll_synopsis_text);

        tv_description = findViewById(R.id.tv_description);

        //ExpandableText
        tv_more_click.setVisibility(View.GONE);
        ex_synopsis.setVisibility(View.GONE);

        ll_producer = findViewById(R.id.ll_producer);
        ll_year = findViewById(R.id.ll_year);
        ll_audio = findViewById(R.id.ll_audio);
        ll_theme = findViewById(R.id.ll_theme);
        ll_director = findViewById(R.id.ll_director);
        ll_cast = findViewById(R.id.ll_cast);

        ll_producer.setVisibility(View.GONE);
        ll_audio.setVisibility(View.GONE);
        ll_year.setVisibility(View.GONE);

        tv_producer_label = findViewById(R.id.tv_producer_label);
        tv_year_label = findViewById(R.id.tv_year_label);
        tv_audio_label = findViewById(R.id.tv_audio_label);
        tv_theme_label = findViewById(R.id.tv_theme_label);
        tv_synopsis_label = findViewById(R.id.tv_synopsis_label);
        tv_director_label = findViewById(R.id.tv_director_label);
        tv_cast_label = findViewById(R.id.tv_cast_label);

        //iv_menu.setVisibility(View.GONE);
        //iv_logo_text.setVisibility(View.GONE);
        //rl_end_icons.setVisibility(View.GONE);
        tv_theme_label.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);

        tv_title.setText("");

        rl_image = findViewById(R.id.rl_image);
        exo_player_view = findViewById(R.id.exo_player_view);
        pb_trailer = findViewById(R.id.pb_trailer);
        ll_watch_trailer = findViewById(R.id.ll_watch_trailer);
       // ll_play_now = findViewById(R.id.ll_play_now);
        ll_play_overlap = findViewById(R.id.ll_play_overlap);

        pb_trailer.setVisibility(View.GONE);

        rl_btm_navigation_show = findViewById(R.id.rl_btm_navigation_show);
        rl_btm_navigation_show.setVisibility(View.VISIBLE);

        rl_toolbar = findViewById(R.id.rl_toolbar);
        rl_details = findViewById(R.id.rl_details);
        rl_player = findViewById(R.id.rl_player);
        sc_meta = findViewById(R.id.sc_meta);
        FrameLayout yt_fragment = findViewById(R.id.yt_fragment);


        exo_fullscreen_icon = exo_player_view.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout exo_fullscreen_button = exo_player_view.findViewById(R.id.exo_fullscreen_button);

        //main layout
        rl_details.setVisibility(View.VISIBLE);
        sc_meta.setVisibility(View.VISIBLE);

        //error layout
        ll_error = findViewById(R.id.ll_error);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        ll_error.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);

        isPlayNow = false;
        isTrailerPlayable = false;

        rv_show_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_show_list.setNestedScrollingEnabled(false);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });


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
        //like/dislike click
        ll_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferenceUtility.getGuest()) {
                    if (isFromWatchList) {
                        HappiApplication.setNeedsToBeRefreshed(true);
                    } else {
                        HappiApplication.setNeedsToBeRefreshed(false);
                    }
                    if (isLikeClicked) {
                        isLikeClicked = false;
                        iv_like.setImageResource(R.drawable.ic_like_empty);

                        ll_dislike.setEnabled(true);
                        iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                        tv_dislike.setTextColor(getResources().getColor(R.color.whiteThree));

                        likeOrUnlikeShow(0);
                    } else {
                        isLikeClicked = true;
                        iv_like.setImageResource(R.drawable.ic_like_fill_full);

                        ll_dislike.setEnabled(false);
                        iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        tv_dislike.setTextColor(getResources().getColor(R.color.coolGrey));

                        likeOrUnlikeShow(1);
                    }
                } else {
                    showLoginOrRegisterAlert();
                }

            }
        });
        ll_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPreferenceUtility.getGuest()) {
                    if (isFromWatchList) {
                        HappiApplication.setNeedsToBeRefreshed(true);
                    } else {
                        HappiApplication.setNeedsToBeRefreshed(false);
                    }
                    if (isDisLikeClicked) {
                        isDisLikeClicked = false;
                        iv_dislike.setImageResource(R.drawable.ic_thumbsdown_empty);

                        ll_like.setEnabled(true);
                        iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                        tv_like.setTextColor(getResources().getColor(R.color.whiteThree));

                        dislikeShow(0);
                    } else {
                        isDisLikeClicked = true;
                        iv_dislike.setImageResource(R.drawable.ic_thumbsdown_fill_full);

                        ll_like.setEnabled(false);
                        iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        tv_like.setTextColor(getResources().getColor(R.color.coolGrey));

                        dislikeShow(1);
                    }
                } else {
                    showLoginOrRegisterAlert();
                }
            }
        });
        ll_watch_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferenceUtility.getGuest()) {
                    if (isFromWatchList) {
                        HappiApplication.setNeedsToBeRefreshed(true);
                    } else {
                        HappiApplication.setNeedsToBeRefreshed(false);
                    }
                    if (isAddToWatchLaterClicked) {
                        isAddToWatchLaterClicked = false;
                        iv_watch_list.setImageResource(R.drawable.ic_add_to_watch_list);
                        addOrRemoveFromWatchList(0, 1);
                    } else {
                        isAddToWatchLaterClicked = true;
                        iv_watch_list.setImageResource(R.drawable.ic_checkmark);
                        addOrRemoveFromWatchList(1, 0);
                    }
                } else {
                    //showLoginAlert();
                    showLoginOrRegisterAlert();
                }
            }
        });
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferenceUtility.getGuest()) {
                    shareShow();
                } else {
                    //showLoginAlert();
                    showLoginOrRegisterAlert();
                }
            }
        });


        if (HappiApplication.getAppToken().isEmpty()) {
            getSession(showId);
        } else {
            getShowDetails(showId);
        }

    }

    private void displayErrorLayout() {
        progressDialogDismiss();
        releasePlayer();

        rl_details.setVisibility(View.GONE);
        sc_meta.setVisibility(View.GONE);

        ll_error.setVisibility(View.VISIBLE);
        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }


    private void showLoginOrRegisterAlert() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        String message = "Please Login or Register to use this feature.";
        LoginRegisterAlert alertDialog =
                new LoginRegisterAlert(this, message, "Ok", "Cancel", this::onLoginRegisterNegativeClick,
                        this::onLoginRegisterNeutralClick, true);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void goToLoginScreen() {
        releasePlayer();
        SharedPreferenceUtility.setShowId(showId);
        Intent loginIntent = new Intent(ShowDetailsActivity.this, SubscriptionLoginActivity.class);
        loginIntent.putExtra("from", "showDetails");
        startActivity(loginIntent);
        // finish();
    }

    private void isInWatchList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable watchFlagDisposable = usersService.watchListFlagApi(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), showId, SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(watchlistflagResponseModel -> {
                    if (watchlistflagResponseModel.getWatchListData().size() != 0) {
                        if (watchlistflagResponseModel.getWatchListData().get(0).getWatchlist_flag() == 1) {
                            isAddToWatchLaterClicked = true;
                            iv_watch_list.setImageResource(R.drawable.ic_checkmark);
                            ll_watch_list.setEnabled(true);
                        } else if (watchlistflagResponseModel.getWatchListData().get(0).getWatchlist_flag() == 0) {
                            isAddToWatchLaterClicked = false;
                            iv_watch_list.setImageResource(R.drawable.ic_add_to_watch_list);
                            ll_watch_list.setEnabled(true);
                        }
                    } else {
                        ll_watch_list.setEnabled(true);
                    }

                }, throwable -> {

                });
        compositeDisposable.add(watchFlagDisposable);
    }

    private void isLiked() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable likedDisposable = usersService.likedFlagApi(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), showId, SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(likedFlagResponseModel -> {
                    if (likedFlagResponseModel.getLikeFlagModelList().size() != 0) {
                        if (likedFlagResponseModel.getLikeFlagModelList().get(0).getLiked_flag() == 1) {
                            isLikeClicked = true;
                            iv_like.setImageResource(R.drawable.ic_like_fill_full);
                            ll_like.setEnabled(true);

                        } else if (likedFlagResponseModel.getLikeFlagModelList().get(0).getLiked_flag() == 0) {
                            isLikeClicked = false;
                            iv_like.setImageResource(R.drawable.ic_like_empty);
                            ll_like.setEnabled(true);
                        }
                    } else {
                        ll_like.setEnabled(true);
                    }

                }, throwable -> {

                });
        compositeDisposable.add(likedDisposable);
    }

    private void shareShow() {
        String sharetext = ConstantUtils.SHARE_URL.trim() + showId;
        if (!showTitletext.isEmpty()) {
            sharetext = sharetext + "\r\n\r\n" + showTitletext;
        }
        if (!descriptiontext.isEmpty()) {
            sharetext = sharetext + "\r\n\r\n" + descriptiontext;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
        startActivity(shareIntent);
    }

    private void addOrRemoveFromWatchList(int watchlistFlag, int deletestatus) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable watchDisposable = usersService.addToWatchList(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), showId, SharedPreferenceUtility.getUserId(), watchlistFlag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(watchResponse -> {
                    if (watchResponse.getStatus() == 100) {
                        //do nothing
                    } else {
                        if (watchlistFlag == 0) {
                            isAddToWatchLaterClicked = true;
                            iv_watch_list.setImageResource(R.drawable.ic_checkmark);

                        } else if (watchlistFlag == 1) {
                            isAddToWatchLaterClicked = false;
                            iv_watch_list.setImageResource(R.drawable.ic_add_to_watch_list);
                        }
                        Toast.makeText(this, "Can't connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    if (watchlistFlag == 0) {
                        isAddToWatchLaterClicked = true;
                        iv_watch_list.setImageResource(R.drawable.ic_checkmark);

                    } else if (watchlistFlag == 1) {
                        isAddToWatchLaterClicked = false;
                        iv_watch_list.setImageResource(R.drawable.ic_add_to_watch_list);
                    }
                });

        compositeDisposable.add(watchDisposable);
    }

    private void likeOrUnlikeShow(int likedFlag) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable likeDisposable = usersService.likeOrUnlikeShow(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(),
                showId, SharedPreferenceUtility.getUserId(), likedFlag, BuildConfig.VERSION_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(watchListShowsResponseModel -> {
                    if (watchListShowsResponseModel.getStatus() == 100) {
                        //do nothing
                    } else {
                        if (likedFlag == 0) {
                            isLikeClicked = true;
                            iv_like.setImageResource(R.drawable.ic_like_fill_full);

                            ll_dislike.setEnabled(false);
                            iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                            tv_dislike.setTextColor(getResources().getColor(R.color.coolGrey));
                        } else if (likedFlag == 1) {
                            isLikeClicked = false;
                            iv_like.setImageResource(R.drawable.ic_like_empty);

                            ll_dislike.setEnabled(true);
                            iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                            tv_dislike.setTextColor(getResources().getColor(R.color.whiteThree));
                        }
                        Toast.makeText(HappiApplication.getCurrentContext(), "Can't connect to server. Please try again later.", Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {
                    if (likedFlag == 0) {
                        isLikeClicked = true;
                        iv_like.setImageResource(R.drawable.ic_like_fill_full);

                        ll_dislike.setEnabled(false);
                        iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        tv_dislike.setTextColor(getResources().getColor(R.color.coolGrey));
                    } else if (likedFlag == 1) {
                        isLikeClicked = false;
                        iv_like.setImageResource(R.drawable.ic_like_empty);

                        ll_dislike.setEnabled(true);
                        iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                        tv_dislike.setTextColor(getResources().getColor(R.color.whiteThree));
                    }
                });
        compositeDisposable.add(likeDisposable);
    }

    private void dislikeShow(int dislikeFlag) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable likeDisposable = usersService.dislikeShow(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(),
                showId, SharedPreferenceUtility.getUserId(), dislikeFlag, BuildConfig.VERSION_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(watchListShowsResponseModel -> {
                    if (watchListShowsResponseModel.getStatus() == 100) {
                        //do nothing
                    } else {
                        if (dislikeFlag == 0) {
                            isDisLikeClicked = true;
                            iv_dislike.setImageResource(R.drawable.ic_thumbsdown_fill_full);

                            ll_like.setEnabled(false);
                            iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                            tv_like.setTextColor(getResources().getColor(R.color.coolGrey));
                        } else if (dislikeFlag == 1) {
                            isDisLikeClicked = false;
                            iv_dislike.setImageResource(R.drawable.ic_thumbsdown_empty);

                            ll_like.setEnabled(true);
                            iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                            tv_like.setTextColor(getResources().getColor(R.color.whiteThree));
                        }
                        Toast.makeText(HappiApplication.getCurrentContext(), "Can't connect to server. Please try again later.", Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {
                    if (dislikeFlag == 0) {
                        isDisLikeClicked = true;
                        iv_dislike.setImageResource(R.drawable.ic_thumbsdown_fill_full);

                        ll_like.setEnabled(false);
                        iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                        tv_like.setTextColor(getResources().getColor(R.color.coolGrey));
                    } else if (dislikeFlag == 1) {
                        isDisLikeClicked = false;
                        iv_dislike.setImageResource(R.drawable.ic_thumbsdown_empty);

                        ll_like.setEnabled(true);
                        iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
                        tv_like.setTextColor(getResources().getColor(R.color.whiteThree));
                    }
                });
        compositeDisposable.add(likeDisposable);
    }

    private void getSession(String showId) {
        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable = tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAppKey(),
                SharedPreferenceUtility.getBundleID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sessionTokenResponseModel -> {

                    HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                    SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());

                    getShowDetails(showId);


                }, throwable -> {
                    displayErrorLayout();
                    Toast.makeText(ShowDetailsActivity.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void getShowDetails(String id) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable disposable = usersService.getVideoDetailsByShow(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(), id,
                SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showVideoUpdatedResponseModel -> {
                    if (showVideoUpdatedResponseModel.getData().size() != 0) {
                        //descriptiontext = showVideoUpdatedResponseModel.getData().get(0).getMetaDataModelList().get(0).getSynopsis();
                        //showTitletext = showVideoUpdatedResponseModel.getData().get(0).getMetaDataModelList().get(0).getShow_name();
                        setShowDetails(showVideoUpdatedResponseModel.getData().get(0).getMetaDataModelList().get(0),
                                showVideoUpdatedResponseModel.getData().get(0).getVideoModelUpdatedList(),
                                showVideoUpdatedResponseModel.getData().get(0).getLanguageModelUpdatedList(),
                                showVideoUpdatedResponseModel.getData().get(0).getCategoryModelUpdatedList());
                    }else{
                        displayErrorLayout();
                    }
                }, throwable -> {
                    displayErrorLayout();

                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(disposable);
    }

    private void setShowDetails(ShowMetaDataModel showDetails,
                                List<VideoModelUpdated> videoModelUpdatedList,
                                List<DataModel.LanguageModelUpdated> languageModelUpdatedList,
                                List<DataModel.CategoryModelUpdated> categoryModelUpdatedList) {

        if (showDetails != null) {

            //show name
            if (showDetails.getShow_name() != null  && !showDetails.getShow_name().isEmpty()) {
                tv_title.setText(showDetails.getShow_name().trim());
                tv_show_name.setVisibility(View.VISIBLE);
                tv_show_name.setText(showDetails.getShow_name());
                showTitletext = showDetails.getShow_name().trim();

            } else {
                tv_show_name.setVisibility(View.GONE);
                showTitletext = "";
            }

            //show description
            if (showDetails.getSynopsis() != null && !showDetails.getSynopsis().isEmpty()) {
                String description = showDetails.getSynopsis().trim();
                if (description.contains("\r\n")) {
                    description = description.replace("\r\n", " ");
                }
                descriptiontext = description;
            }else{
                descriptiontext = "";
            }


            //make like/unlike, add to watch list, share icons visible
            if (!SharedPreferenceUtility.getGuest()) {
                if (showDetails.getWatchlist_flag() == 1) {
                    isAddToWatchLaterClicked = true;
                    iv_watch_list.setImageResource(R.drawable.ic_checkmark);
                    ll_watch_list.setEnabled(true);
                } else if (showDetails.getWatchlist_flag() == 0) {
                    isAddToWatchLaterClicked = false;
                    iv_watch_list.setImageResource(R.drawable.ic_add_to_watch_list);
                    ll_watch_list.setEnabled(true);
                }

                if (showDetails.getLiked_flag() == 1) {
                    isLikeClicked = true;
                    iv_like.setImageResource(R.drawable.ic_like_fill_full);
                } else if (showDetails.getLiked_flag() == 0) {
                    isLikeClicked = false;
                    iv_like.setImageResource(R.drawable.ic_like_empty);
                }

                if (showDetails.getDisliked_flag() == 1) {
                    isDisLikeClicked = true;
                    iv_dislike.setImageResource(R.drawable.ic_thumbsdown_fill_full);
                } else if (showDetails.getDisliked_flag() == 0) {
                    isDisLikeClicked = false;
                    iv_dislike.setImageResource(R.drawable.ic_thumbsdown_empty);
                }

                if (showDetails.getLiked_flag() == 0 && showDetails.getDisliked_flag() == 0) {
                    ll_like.setEnabled(true);
                    ll_dislike.setEnabled(true);
                } else if (showDetails.getLiked_flag() == 0 && showDetails.getDisliked_flag() == 1) {
                    ll_like.setEnabled(false);
                    ll_dislike.setEnabled(true);

                    iv_like.setColorFilter(iv_like.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    tv_like.setTextColor(getResources().getColor(R.color.coolGrey));
                } else if (showDetails.getLiked_flag() == 1 && showDetails.getDisliked_flag() == 0) {
                    ll_like.setEnabled(true);
                    ll_dislike.setEnabled(false);

                    iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    tv_dislike.setTextColor(getResources().getColor(R.color.coolGrey));
                } else {
                    ll_like.setEnabled(true);
                    ll_dislike.setEnabled(false);

                    iv_dislike.setColorFilter(iv_dislike.getContext().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                    tv_dislike.setTextColor(getResources().getColor(R.color.coolGrey));
                }
            } else {
                ll_like.setEnabled(true);
                ll_dislike.setEnabled(true);
                ll_watch_list.setEnabled(true);
            }
            ll_icons.setVisibility(View.VISIBLE);

            //show logo
            if (showDetails.getLogo() != null && !showDetails.getLogo().isEmpty()) {
                rl_image.setVisibility(View.VISIBLE);
                iv_show_image.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(ConstantUtils.RELEASE_THUMBNAIL + showDetails.getLogo())
                        .into(iv_show_image);
            } else {
                rl_image.setVisibility(View.GONE);
                iv_show_image.setVisibility(View.GONE);
            }



            // show resolution
            if (showDetails.getResolution() != null && !showDetails.getResolution().isEmpty()) {
                tv_resolution.setVisibility(View.VISIBLE);
                tv_resolution.setText(showDetails.getResolution());
            } else {
                tv_resolution.setVisibility(View.GONE);
            }

           /* //show producer
            if (showDetails.getProducer() != null && !showDetails.getProducer().isEmpty()) {
                ll_producer.setVisibility(View.VISIBLE);
                tv_producer.setVisibility(View.VISIBLE);
                tv_producer_label.setVisibility(View.VISIBLE);
                tv_producer.setText(" " + showDetails.getProducer());
                tv_producer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        releaseExoplayer();
                        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, showDetails.getProducer().trim());

                    }
                });
            } else {
                ll_producer.setVisibility(View.GONE);
                tv_producer.setVisibility(View.GONE);
                tv_producer_label.setVisibility(View.GONE);
            }*/

            //show year
            if (showDetails.getYear() != null && !showDetails.getYear().isEmpty()) {
                ll_year.setVisibility(View.GONE);
                tv_year_label.setVisibility(View.VISIBLE);
                tv_year.setVisibility(View.VISIBLE);
                tv_year.setText(" " + showDetails.getYear());
            } else {
                ll_year.setVisibility(View.GONE);
                tv_year.setVisibility(View.GONE);
                tv_year_label.setVisibility(View.GONE);
            }


            //show audio
            if (languageModelUpdatedList.size() != 0) {
                String audio = "";
                audio = languageModelUpdatedList.get(0).getAudio_language_name();

                for (int index = 1; index > languageModelUpdatedList.size(); index++) {
                    audio = audio + ", " + languageModelUpdatedList.get(index).getAudio_language_name();
                }
                ll_audio.setVisibility(View.GONE);
                tv_audio_label.setVisibility(View.VISIBLE);
                tv_audio.setVisibility(View.VISIBLE);
                tv_audio.setText(" " + audio);
            } else {
                ll_audio.setVisibility(View.GONE);
                tv_audio_label.setVisibility(View.GONE);
                tv_audio.setVisibility(View.GONE);
            }

            //show director
            if (showDetails.getDirector() != null && !showDetails.getDirector().isEmpty()) {
                ll_director.setVisibility(View.VISIBLE);
                tv_director_label.setVisibility(View.VISIBLE);
                tv_director.setVisibility(View.VISIBLE);
                tv_director.setText(" " + showDetails.getDirector());
            } else {
                ll_director.setVisibility(View.GONE);
                tv_director_label.setVisibility(View.GONE);
                tv_director.setVisibility(View.GONE);
            }

            //show cast
            if (showDetails.getShow_cast() != null) {
                if (!showDetails.getShow_cast().isEmpty() && !(showDetails.getShow_cast().equalsIgnoreCase("N/A"))) {
                    ll_cast.setVisibility(View.VISIBLE);
                    tv_cast_label.setVisibility(View.VISIBLE);
                    tv_cast.setVisibility(View.VISIBLE);
                    tv_cast.setText(" " + showDetails.getShow_cast());
                } else {
                    ll_cast.setVisibility(View.GONE);
                    tv_cast_label.setVisibility(View.GONE);
                    tv_cast.setVisibility(View.GONE);
                }

            } else {
                ll_cast.setVisibility(View.GONE);
                tv_cast_label.setVisibility(View.GONE);
                tv_cast.setVisibility(View.GONE);
            }

            //show theme
            if (categoryModelUpdatedList.size() != 0) {
                List<String> theme = new ArrayList<>();

                for (DataModel.CategoryModelUpdated category : categoryModelUpdatedList) {
                    theme.add(category.getCategory_name());
                }
                ll_theme.setVisibility(View.VISIBLE);
                tv_theme_label.setVisibility(View.GONE);
                tag_theme.setVisibility(View.VISIBLE);
                tag_theme.setTags(theme);
                tag_theme.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                    @Override
                    public void onTagClick(String tag) {
                        for (DataModel.CategoryModelUpdated category : categoryModelUpdatedList) {
                            if (tag.equals(category.getCategory_name())) {

                                releaseExoplayer();
                                ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, category.getCategory_id() + ";" + category.getCategory_name());
                                break;
                            }
                        }

                    }
                });
            } else {
                ll_theme.setVisibility(View.GONE);
                tv_theme_label.setVisibility(View.GONE);
                tag_theme.setVisibility(View.GONE);
            }

            //show video list
            if (videoModelUpdatedList.size() != 0) {
//                if (scrollPosition != null) {
//                    Objects.requireNonNull(rv_show_list.getLayoutManager()).onRestoreInstanceState(recyclerViewState);
//                }
                isPlayNow = true;
                videoId = videoModelUpdatedList.get(0).getVideo_id();
                if (videoModelUpdatedList.size() == 1) {
                    rv_show_list.setVisibility(View.GONE);
                } else {
                    rv_show_list.setVisibility(View.VISIBLE);
                    showDetailsAdapter = new ShowDetailsAdapter(this, ShowDetailsActivity.this, new ShowDetailsAdapter.itemClickListener() {
                        @Override
                        public void onFeaturedShowItemClicked(int adapterPosition) {
                            isPlayNow = true;
                            showDetailsAdapter.setSelected(adapterPosition);
                            videoId = showDetailsAdapter.getItem(adapterPosition).getVideo_id();
                            goToVideoPlayer(videoId);
                            // Toast.makeText(ShowDetailsActivity.this, "video item clicked", Toast.LENGTH_SHORT).show();
                        }
                    }, videoModelUpdatedList);
                    rv_show_list.setAdapter(showDetailsAdapter);
                }

            } else {
                rv_show_list.setVisibility(View.GONE);
                progressDialogDismiss();
            }


            //========================trailer====================//
            /*   teaser_status_flag  = 0 -> no teaser
                                       1 -> teaser available

                 teaser_flag         = 0 -> Youtube player
                                       1 -> Exoplayer
                                       3 -> No teaser       */
            if (showDetails.getTeaser_status_flag() != null && !showDetails.getTeaser_status_flag().isEmpty()) {
                if (showDetails.getTeaser_status_flag().equals("1")) {

                    if (showDetails.getTeaser_flag() != null && !showDetails.getTeaser_flag().isEmpty()) {
                        String teaserFlag = showDetails.getTeaser_flag();
                        if (teaserFlag.equals("0")) {
                            isTrailerPlayable = false;
                            //isYtPlayer = false;
                            ll_watch_trailer.setVisibility(View.GONE);
                            ll_play_overlap.setVisibility(View.VISIBLE);

                        } else if (teaserFlag.equals("1")) {
                            isTrailerPlayable = true;
                            //teaserUrl = showDetails.getTeaser();
                            //isYtPlayer = false;
                            ll_watch_trailer.setVisibility(View.VISIBLE);
                            ll_play_overlap.setVisibility(View.VISIBLE);
                        //    ll_play_now.setVisibility(View.VISIBLE);

                        } else if (teaserFlag.equals("3")) {
                            isTrailerPlayable = false;
                            //isYtPlayer = false;
                            ll_watch_trailer.setVisibility(View.GONE);
                            ll_play_overlap.setVisibility(View.VISIBLE);
                        }

                    } else {
                        ll_play_overlap.setVisibility(View.VISIBLE);
                    }
                } else if (showDetails.getTeaser_status_flag().equals("0")) {
                    ll_watch_trailer.setVisibility(View.GONE);
                    ll_play_overlap.setVisibility(View.VISIBLE);
                }
            } else {
                ll_play_overlap.setVisibility(View.VISIBLE);
            }

            ll_watch_trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTrailerPlayable) {
                        pb_trailer.setVisibility(View.VISIBLE);
                        String teaser = showDetails.getTeaser();
                        if (teaser == null || teaser.isEmpty()) {
                            trailerError();
                        } else {
                            getToken(teaser);
                        }
                        // initializePlayer(showDetails.getTeaser());
                    }

                }
            });
           /* ll_play_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlayNow) {
                        goToVideoPlayer(videoId);

                    }
                }
            });*/

            ll_play_overlap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlayNow) {
                        // isPlayNow = false;
                        goToVideoPlayer(videoId);

                    }
                }
            });


            //show description
            if (!descriptiontext.isEmpty()) {
                tv_synopsis_label.setVisibility(View.VISIBLE);
                ll_synopsis_text.setVisibility(View.VISIBLE);
                /*//Expandable text
                tv_more_click.setVisibility(View.VISIBLE);
                ex_synopsis.setVisibility(View.VISIBLE);*/


                String description = descriptiontext;
                if(description.contains("\r\n")){
                    description = description.replace("\r\n"," ");
                }

                //for truncating description
                ex_synopsis.setText(description);
                //ex_synopsis.setText(showDetails.getSynopsis());


                //------------------------------testing--------------//
                tv_more_click.setVisibility(View.GONE);
                ex_synopsis.setVisibility(View.GONE);
                ll_synopsis_text.setVisibility(View.VISIBLE);
                tv_synopsis_label.setVisibility(View.VISIBLE);
                tv_description.setVisibility(View.VISIBLE);
                tv_description.setText(description);
                truncateDescription(description);

                //-------------------------------------------------------//



                progressDialogDismiss();
                // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
                ex_synopsis.setAnimationDuration(750L);

                // set interpolators for both expanding and collapsing animations
                ex_synopsis.setInterpolator(new OvershootInterpolator());
// toggle the ExpandableTextView
                tv_more_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        // buttonToggle.setText(expandableTextView.isExpanded() ? R.string.expand : R.string.collapse);
                        ex_synopsis.toggle();
                        tv_more_click.setVisibility(View.GONE);
                    }
                });



            } else {

                tv_synopsis_label.setVisibility(View.GONE);
                ll_synopsis_text.setVisibility(View.GONE);
                tv_more_click.setVisibility(View.GONE);


                tv_description.setVisibility(View.GONE);

                progressDialogDismiss();
            }

        } else {
            progressDialogDismiss();
            Toast.makeText(this, "Error occured. Please try again", Toast.LENGTH_SHORT).show();
            displayErrorLayout();
        }

    }


    private void truncateDescription(String fullDescription){
        if (tv_description.getTag() == null) {
            tv_description.setTag(tv_description.getText());
        }
        ViewTreeObserver vto = tv_description.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv_description.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

                int lineCount = tv_description.getLayout().getLineCount();
                String more = "...More";
                if (lineCount > 5) {
                    int index = tv_description.getLayout().getLineEnd(4);
                   // String atIndex = tv_description.getText().
                    String truncated = tv_description.getText().subSequence(0, index - more.length() + 1) + " ";
                    tv_description.setText(Html.fromHtml(truncated + "<font color='#34A7CD'>...More</font>"));
                    tv_description.setVisibility(View.VISIBLE);
                    progressDialogDismiss();
                    tv_description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_description.setText(fullDescription);
                            tv_description.setVisibility(View.VISIBLE);
                            progressDialogDismiss();
                        }
                    });


                }
            }
        });

    }
    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore, String fullText) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setTextSize(14);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, fullText), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setTextSize(14);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, fullText), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setTextSize(14);
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore, fullText), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText,
                                                                            final boolean viewMore, String fullText) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setTextSize(14);
                        tv.setTextColor(Color.parseColor("#FFFFFF"));
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "...Less", false, fullText);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setTextSize(14);
                        tv.setTextColor(Color.parseColor("#FFFFFF"));
                        tv.setText(fullText);
                        tv.invalidate();
                        // makeTextViewResizable(tv, 7, "...More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    private TextView setSpanForSynopsis(String text, TextView textView) {
        /*String synopsisLimitedText = text.substring(0,250);
       // tv_description.setText(synopsisLimitedText+"...");
        SpannableString spannableString = new SpannableString(" More");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                tv_description.setText(text);
            }
        };
        spannableString.setSpan(clickableSpan,0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_description.setText(synopsisLimitedText+"..."+spannableString);*/

        /*SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(synopsisLimitedText+"... More");
         spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED),
                 254, 257,
                 Spannable.SPAN_EXCLUSIVE_INCLUSIVE);*/

        String limitedText = "";
        if (text.length() > 298) {
            limitedText = text.substring(0, 298) + "...";
            textView.setText(Html.fromHtml(limitedText + "<font color='#F2743C'>More</font>"));
        } else {
            textView.setText(text);
        }
        return textView;

    }

    public void progressDialogDismiss() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void goToVideoPlayer(int videoId) {
        releaseExoplayer();
        SharedPreferenceUtility.setVideoId(videoId);
        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, videoId);
        // finish();
    }
    private void releaseExoplayer(){
        if(exoPlayer!=null){
            isTrailerPlayable = true;
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.removeListener(eventLogger);
            exoPlayer.removeMetadataOutput(eventLogger);
            exoPlayer.removeAudioDebugListener(eventLogger);
            exoPlayer.removeVideoDebugListener(eventLogger);
            exoPlayer = null;
            exo_player_view.setPlayer(null);
        }
        rl_player.setVisibility(View.GONE);
        rl_image.setVisibility(View.VISIBLE);
    }
    private void trailerError() {
        pb_trailer.setVisibility(View.GONE);
        Toast.makeText(this, "Oops!! Can't play trailer. Please try again later.", Toast.LENGTH_SHORT).show();
        if (exoPlayer != null) {
            isTrailerPlayable = true;
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.removeListener(eventLogger);
            exoPlayer.removeMetadataOutput(eventLogger);
            exoPlayer.removeAudioDebugListener(eventLogger);
            exoPlayer.removeVideoDebugListener(eventLogger);
            exoPlayer = null;
            exo_player_view.setPlayer(null);
        }

        rl_player.setVisibility(View.GONE);
        rl_image.setVisibility(View.VISIBLE);
    }

    private void getToken(String teaser) {

        ApiClient.UsersService usersService = ApiClient.createToken();
        Disposable tokenDisposable = usersService.getVideoToken(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenResponse>() {
                    @Override
                    public void accept(TokenResponse tvexcelResponse) {

                        token = tvexcelResponse.getData().trim();
                        initializePlayer(teaser);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        trailerError();
                    }
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void initializePlayer(String teaser) {
        // rl_image.setVisibility(View.GONE);
        rl_image.setVisibility(View.GONE);
        rl_player.setVisibility(View.VISIBLE);
        exo_player_view.setVisibility(View.VISIBLE);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtStartTime = df.format(new Date());
        mediaDataSourceFactory = buildDataSourceFactory(true);
        Handler mainHandler = new Handler();


        boolean needNewPlayer = exoPlayer == null;
        if (teaser != null && !teaser.isEmpty()) {
            pb_trailer.setVisibility(View.GONE);
            try {

                Uri teaserUri = Uri.parse(teaser.trim());

                if (needNewPlayer) {
                    TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                    DefaultTrackSelector trackSelector1 = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
                    TrackSelectionHelper trackSelectionHelper = new TrackSelectionHelper(trackSelector1, adaptiveTrackSelectionFactory);
                    eventLogger = new EventLogger(trackSelector1);

                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
                    DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                            this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector);
                    exoPlayer.addListener(eventLogger);
                    exoPlayer.addMetadataOutput(eventLogger);
                    exoPlayer.addAudioDebugListener(eventLogger);
                    exoPlayer.addVideoDebugListener(eventLogger);
                    exo_player_view.setPlayer(exoPlayer);
                    exoPlayer.setPlayWhenReady(shouldAutoPlay);
                    //  exo_player_view.findViewById(R.id.exo_fullscreen_icon).setVisibility(View.GONE);
                    /* exo_player_view.findViewById(R.id.exo_fullscreen_button).setVisibility(View.GONE);*/
                    exo_player_view.findViewById(R.id.exo_fullscreen_button).setVisibility(View.VISIBLE);
                    exo_player_view.findViewById(R.id.exo_fullscreen_icon);
                    exoPlayer.addListener(new Player.DefaultEventListener() {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            // super.onPlayerStateChanged(playWhenReady, playbackState);
                            if (playbackState == Player.STATE_ENDED) {
                                if (isExoPlayerFullscreen) {
                                    closeFullscreen();
                                }
                                exoPlayer.setPlayWhenReady(false);
                            }
                        }
                    });
                    MediaSource mediaSource = buildMediaSource(teaserUri, null, mainHandler, eventLogger);
                    boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
                    if (haveResumePosition) {
                        exoPlayer.seekTo(resumeWindow, resumePosition);
                    }
                    exoPlayer.prepare(mediaSource);


                }
            } catch (Exception e) {
                trailerError();
                Log.e("PLAYER", ": exception" + e.getMessage());
            }
        } else {
            trailerError();
        }

        // setupMedia(teaser);
    }

    /*  public void setupMedia(String teaser){
          Log.e("CAST","setupMedia called");
          mSelectedMedia = buildMediaInfo(videoModelz.getVideo_title(),token, "Adventure sports TV", "", 333, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/DesigningForGoogleCast.m3u8",
                  "application/x-mpegurl", ConstantUtils.THUMBNAIL_URL + videoModelz.getThumbnail(), ConstantUtils.THUMBNAIL_URL + videoModelz.getThumbnail(), null);
      }*/
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        //Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
        factory.getDefaultRequestProperties().set("token", token);
        return factory;
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

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void openFullscreen() {

        isExoPlayerFullscreen = true;
        actionBarHeight = rl_toolbar.getHeight();
        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ShowDetailsActivity.this, R.drawable.ic_fullscreen_skrink));
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
        rl_player.setPadding(0, 0, 0, 0);
        rl_details.setPadding(0, 0, 0, 0);

        rl_player.setLayoutParams(params);
        rl_details.setLayoutParams(params);
        rl_btm_navigation_show.setVisibility(View.GONE);
        rl_toolbar.setVisibility(View.GONE);
        sc_meta.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void closeFullscreen() {


        isExoPlayerFullscreen = false;

        exo_fullscreen_icon.setImageDrawable(ContextCompat.getDrawable(ShowDetailsActivity.this, R.drawable.ic_fullscreen_white));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout.LayoutParams exo_params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
                //(int) getResources().getDimension(R.dimen.dimen_200dp));

        //exo_params.addRule(RelativeLayout.BELOW, R.id.rl_video_title);
        exo_player_view.setLayoutParams(exo_params);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
                //(int) getResources().getDimension(R.dimen.dimen_250dp));
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.dimen_250dp));

        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.dimen_200dp));

        params.setMargins(0, actionBarHeight, 0, 0);
        params2.setMargins(0, 0, 0, 0);
        paramsImage.setMargins(5, 5, 5, 5);

        rl_player.setPadding(0, 0, 0, 0);
        rl_details.setPadding(0, 0, 0, 0);
        rl_image.setPadding(5, 5, 5, 5);

        rl_player.setLayoutParams(params2);
        rl_details.setLayoutParams(params);
        rl_image.setLayoutParams(paramsImage);
        rl_details.setVisibility(View.VISIBLE);
        rl_player.setVisibility(View.VISIBLE);
        rl_toolbar.setVisibility(View.VISIBLE);
        sc_meta.setVisibility(View.VISIBLE);
        rl_btm_navigation_show.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {

        isFromWatchList = false;
        isTrailerPlayable = false;
        isPlayNow = false;
        // isYtPlayer = false;
        safelyDispose(compositeDisposable);
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        resumePlayer();
       /* if(!SharedPreferenceUtility.getGuest()){
            isInWatchList();
            isLiked();
        }else{
            ll_watch_list.setEnabled(true);
            ll_like.setEnabled(true);
        }*/

        super.onResume();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backNavigation();
    }

    private void backNavigation() {
        isFromWatchList = false;
        if (isExoPlayerFullscreen) {
            closeFullscreen();
        }else {

            releasePlayer();
            if (exoPlayer != null) {
                exoPlayer.stop();
            }
            super.onBackPressed();
            finish();
            overridePendingTransition(0,0);
        }

    }


    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    @Override
    public void onLoginRegisterNegativeClick() {
        goToLoginScreen();
    }

    @Override
    public void onLoginRegisterNeutralClick() {

    }
}
