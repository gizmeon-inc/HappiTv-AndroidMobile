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

import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.models.ShowModel;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.SearchResultsAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
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

public class CategoryViewActivity extends BaseActivity implements SearchResultsAdapter.itemClickListener,ShowList_adapter.itemClickListener {

    TypefacedTextViewRegular tv_title;
    //ImageView iv_menu;
    ImageView iv_back;
    //ImageView iv_logo_text;
    //ImageView iv_search;

    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_video_list;
    //SearchResultsAdapter videoListAdapter;
    ShowList_adapter showList_adapter;
    private AnimationItem mSelectedItem;
    private CompositeDisposable compositeDisposable;
    SkeletonScreen loadingVideos;


    private int userId = 0;

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
        setContentView(R.layout.activity_category_view);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
       // updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        //categoryId = HappiApplication.getCategoryId();

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        tv_title = findViewById(R.id.tv_title);
        //iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        //iv_logo_text = findViewById(R.id.iv_logo_text);
        //iv_search = findViewById(R.id.iv_search);

        tv_title.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);

        //iv_menu.setVisibility(View.GONE);
       // iv_logo_text.setVisibility(View.GONE);
       // iv_search.setVisibility(View.GONE);

        rv_video_list = findViewById(R.id.rv_video_list);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);

        userId = SharedPreferenceUtility.getUserId();

        compositeDisposable = new CompositeDisposable();
        setupRecyclerview();
        String categoryId = "";
        String category = getIntent().getStringExtra(ConstantUtils.CATEGORY_DETAILS);

        if (category != null && !category.isEmpty()) {
            if(category.contains(";")){
                String categoryid = category.split(";")[0];
                categoryId = categoryid;
                String categorytitle = category.split(";")[1];
                tv_title.setText(categorytitle);
                getShowByCategory(categoryId);
            }else{
                String producername = category;
                /*if(producername.length() > 26){
                    String title = producername.substring(0,26)+"...";
                    tv_title.setText(title);
                }else{
                    tv_title.setText(producername);
                }*/
                tv_title.setText(producername);
                getShowByproducer(producername);
            }

            //loadVideoList(categoryid);
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });

    }

    private void backNavigation(){
       /* if(HappiApplication.getRedirect() != null){
            String redirect = HappiApplication.getRedirect();
            if(redirect.equals("home")){
                startActivity(new Intent(this,HomeActivity.class));
            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }*/
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
    private void getShowByproducer(String prodName){

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showsDisposable = usersService.getShowByProducer(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(),
                "android-phone",SharedPreferenceUtility.getCountryCode(),prodName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showByProdResponse -> {
                    if(showByProdResponse.getShowModelList().size() != 0){
                        updateShowList(showByProdResponse.getShowModelList());
                    }else{
                        displayErrorLayout(getString(R.string.no_results_found));
                    }


                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout(getString(R.string.error_message_loading_users));
                    }
                });

        compositeDisposable.add(showsDisposable);

    }
    private void getShowByCategory(String id){

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getShowByCategory(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(),SharedPreferenceUtility.getCountryCode(),
                id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showByCategoryResponse -> {
                    if(showByCategoryResponse.getShowModelList().size() != 0){
                        updateShowList(showByCategoryResponse.getShowModelList());
                    }else{
                        displayErrorLayout(getString(R.string.no_results_found));
                    }


                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout(getString(R.string.error_message_loading_users));
                    }
                });

        compositeDisposable.add(showDisposable);
    }

    private void loadVideoList(int categoryId) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.GetvideoByCategory(HappiApplication
                .getAppToken(), categoryId, userId, SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoResponse -> {

                    if (videoResponse.getData().isEmpty()) {

                        displayErrorLayout(getString(R.string.no_results_found));
                    } else {

                        //updateVideoDataList(videoResponse.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                        displayErrorLayout(getString(R.string.error_message_loading_users));
                    }
                });
        compositeDisposable.add(videoDisposable);
    }


    /*private void updateVideoDataList(List<VideoModel> videoModelList) {
        videoListAdapter.clearAll();
        videoListAdapter.addAll(showModelList);
        loadingVideos.hide();
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_video_list.setVisibility(View.VISIBLE);
        videoListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_list, mSelectedItem);
        if (videoListAdapter.isEmpty()) {
            rv_video_list.setVisibility(View.GONE);
        }
    }*/

    private void updateShowList(List<ShowModel> showModelList) {
        showList_adapter.clearAll();
        showList_adapter.addAll(showModelList);
        loadingVideos.hide();
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_video_list.setVisibility(View.VISIBLE);
        showList_adapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_list, mSelectedItem);
        if (showList_adapter.isEmpty()) {
            rv_video_list.setVisibility(View.GONE);
            displayErrorLayout(getString(R.string.no_results_found));
        }
    }

    private void displayErrorLayout(String errorMessage) {
        rv_video_list.setVisibility(View.GONE);
        loadingVideos.hide();

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(errorMessage);
    }

    private void setupRecyclerview() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        rv_video_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_video_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        showList_adapter = new ShowList_adapter(this,
                this::onShowsItemClicked, true, width);
        rv_video_list.setAdapter(showList_adapter);

        loadingVideos = Skeleton.bind(rv_video_list)
                .adapter(showList_adapter)
                .load(R.layout.loading_videos_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
    }



    @Override
    public void onSearchItemClicked(int adapterPosition) {
        //releasePlayer();

//      ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, videoListAdapter
//              .getItem(adapterPosition).getVideo_id());
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
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(compositeDisposable);
    }

    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        backNavigation();
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
       /* Intent showIntent = new Intent(CategoryViewActivity.this, ShowDetailsActivity.class);
        ShowModel showModel  = showList_adapter.getItem(adapterPosition);
        HappiApplication.setIsFeaturedShow(false);
        HappiApplication.setShowModel(showModel);
        HappiApplication.setRedirect("category");
        startActivity(showIntent);
        finish();*/
       SharedPreferenceUtility.setShowId(showList_adapter.getItem(adapterPosition).getShow_id());
       ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY,showList_adapter.getItem(adapterPosition).getShow_id());
    }
}
