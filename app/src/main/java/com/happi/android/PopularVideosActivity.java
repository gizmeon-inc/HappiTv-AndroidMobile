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
import android.widget.Toast;

import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.adapters.VideoList_adapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ShowModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PopularVideosActivity extends BaseActivity implements VideoList_adapter.itemClickListener, ShowList_adapter.itemClickListener {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu, iv_back, iv_logo_text, iv_search;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_video_list;
    VideoList_adapter videoList_adapter;
    ShowList_adapter showList_adapter;
    private AnimationItem mSelectedItem;
    private Disposable internetDisposable;
    private CompositeDisposable compositeDisposable;
    SkeletonScreen loadingVideos;
    private String title = "empty";


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
        setContentView(R.layout.activity_popular_videos);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        tv_title = findViewById(R.id.tv_title);
        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        iv_search = findViewById(R.id.iv_search);

        tv_title.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        rv_video_list = findViewById(R.id.rv_video_list);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);

        if (getIntent().getStringExtra("title") != null) {
            title = getIntent().getStringExtra("title");
            if (title.equals("watchFree")) {
                tv_title.setText(R.string.free);
            } else if (title.equals("newRelease")) {
                tv_title.setText(R.string.new_releases);
            }

        } else {
            title = "empty";
        }
        //tv_title.setText(R.string.new_releases);



        compositeDisposable = new CompositeDisposable();
        setupRecyclerview();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void setupRecyclerview() {

        rv_video_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_video_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        showList_adapter = new ShowList_adapter(getApplicationContext(), this::onShowsItemClicked, true);
        //   videoList_adapter = new VideoList_adapter(getApplicationContext(),this::onItemClicked,true);
        // rv_video_list.setAdapter(videoList_adapter);
        rv_video_list.setAdapter(showList_adapter);

        loadingVideos = Skeleton.bind(rv_video_list)
                // .adapter(videoList_adapter)
                .adapter(showList_adapter)
                .load(R.layout.loading_videos_vertical)
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
                   // Log.d("^&^&^&", "POPVID" + isConnected);
                    if (isConnected) {
                      //  Log.d("^&^&^&", "POPVID" + "^&^&^&");
                        // loadVideoList();
                    } else {
                        // Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show();
                    }
                });

        if (!title.equals("empty")) {
            if (title.equals("watchFree")) {
                // tv_title.setText(R.string.free);
                getFreeVideos();
            } else if (title.equals("newRelease")) {
                // tv_title.setText(R.string.new_releases);
                getNewReleases();
            }
        } else {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            PopularVideosActivity.super.onBackPressed();
        }
    }

    private void getFreeVideos() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getFreeShowList(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showModelResponse -> {
                    if (showModelResponse.getShowModelList().size() != 0) {

                        updateShowDataList(showModelResponse.getShowModelList());

                    } else {
                        displayErrorLayout(getString(R.string.no_results_found));
                    }
                }, throwable -> {
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(showDisposable);
    }

    private void getNewReleases() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.NewReleases(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showResponse -> {

                    if (showResponse.getShowModelList().size() != 0) {
                        updateShowDataList(showResponse.getShowModelList());

                    } else {
                        displayErrorLayout(getString(R.string.no_results_found));
                    }

                }, throwable -> {

                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(videoDisposable);
    }

    private void loadVideoList() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.NewReleases(HappiApplication.getAppToken()
                , SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoResponse -> {
                            if (videoResponse.getShowModelList().size() != 0) {

                                //  updateVideoDataList(videoResponse.getShowModelList());
                            } else {

                                displayErrorLayout(getString(R.string.no_results_found));
                            }
                        },
                        throwable -> displayErrorLayout(getString(R.string.server_error)));
        compositeDisposable.add(videoDisposable);
    }

  /* private void updateVideoDataList(List<VideoModel> videoModelList) {
       videoList_adapter.clearAll();
       videoList_adapter.addAll(videoModelList);
       loadingVideos.hide();
       videoList_adapter.notifyDataSetChanged();
       runLayoutAnimation(rv_video_list, mSelectedItem);
       if(videoList_adapter.isEmpty()){

           rv_video_list.setVisibility(View.GONE);
       }
   }*/


    private void updateShowDataList(List<ShowModel> videoModelList) {

        showList_adapter.clearAll();
        showList_adapter.addAll(videoModelList);
        loadingVideos.hide();
        showList_adapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_list, mSelectedItem);
        if (showList_adapter.isEmpty()) {

            rv_video_list.setVisibility(View.GONE);
            displayErrorLayout(getString(R.string.no_results_found));
        }
    }

    private void displayErrorLayout(String message) {
        rv_video_list.setVisibility(View.GONE);

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }

    @Override
    public void onItemClicked(int adapterPosition) {

        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, videoList_adapter.getItem(adapterPosition).getShow_id());
       // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(internetDisposable, compositeDisposable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
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
        SharedPreferenceUtility.setShowId(showList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, showList_adapter.getItem(adapterPosition).getShow_id());
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);

    }
}
