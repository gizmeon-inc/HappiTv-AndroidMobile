package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.PayPerViewVideoListAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.GetPayperviewVideoListModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PayPerViewVideoListActivity extends BaseActivity implements PayPerViewVideoListAdapter.OnItemClicked {

    private CompositeDisposable compositeDisposable;
    private List<GetPayperviewVideoListModel> payPerViewVideoList;
    private PayPerViewVideoListAdapter payPerViewVideoListAdapter;
    private GridRecyclerView rv_payperviewvideoList;
    ImageView iv_menu;
    ImageView iv_back;
    ImageView iv_logo_text;
    TypefacedTextViewRegular tv_title;
    LinearLayout rl_end_icons;
    ProgressDialog progressDialog;
    SkeletonScreen loadingVideos;
    private AnimationItem[] mAnimationItems;
    private AnimationItem mSelectedItem;
    private ImageView iv_errorimg;
    private TypefacedTextViewRegular tv_errormsg;
    LinearLayout ll_error;
    private SwipeRefreshLayout sw_list;



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
        setContentView(R.layout.activity_watch_list);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        progressDialog = new ProgressDialog(PayPerViewVideoListActivity.this, R.style.MyTheme);
        progressDialog.setCancelable(false);
       // progressDialog.show();

        compositeDisposable = new CompositeDisposable();

        payPerViewVideoList = new ArrayList<>();
        rv_payperviewvideoList = findViewById(R.id.rv_watchList);
        rv_payperviewvideoList.setLayoutManager(new GridLayoutManager(this, 3));
        rv_payperviewvideoList.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        rv_payperviewvideoList.setLongClickable(true);
        payPerViewVideoListAdapter = new PayPerViewVideoListAdapter(payPerViewVideoList, getApplicationContext(), this::onShowItemClicked);
        rv_payperviewvideoList.setAdapter(payPerViewVideoListAdapter);
        rv_payperviewvideoList.setVisibility(View.GONE);

        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
        rl_end_icons = findViewById(R.id.rl_end_icons);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        ll_error = findViewById(R.id.ll_error);

        sw_list = findViewById(R.id.sw_list);
        sw_list.setColorSchemeResources(R.color.colorPrimaryDark);
        sw_list.setDistanceToTriggerSync(250);


        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        rl_end_icons.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        ll_error.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);

        tv_title.setText("My Videos");
        setupRecyclerView();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sw_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sw_list.setRefreshing(true);
                loadData();
            }
        });

    }

    private void loadData(){
        if( SharedPreferenceUtility.getGuest()){
            displayErrorLayout("Oops!! You don't have any data.");
            if(sw_list.isRefreshing()){
                sw_list.setRefreshing(false);
            }
        }else{
            if(HappiApplication.getAppToken()== null || HappiApplication.getAppToken().isEmpty()){
                getSessionToken();
            }else{
                getPaypervideoList();
            }
        }

    }

    private void setupRecyclerView() {
        rv_payperviewvideoList.setVisibility(View.VISIBLE);

        loadingVideos = Skeleton.bind(rv_payperviewvideoList)
                .adapter(payPerViewVideoListAdapter)
                .load(R.layout.loading_videos_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
        loadData();
    }

    private AnimationItem[] getAnimationItems() {
        return new AnimationItem[]{
                new AnimationItem("Slide from bottom", R.anim.grid_layout_animation_from_bottom),
                new AnimationItem("Scale", R.anim.grid_layout_animation_scale),
                new AnimationItem("Scale random", R.anim.grid_layout_animation_scale_random)
        };
    }

    private void getSessionToken() {
        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable = tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAppKey(),
                SharedPreferenceUtility.getBundleID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sessionTokenResponseModel -> {

                    HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                    SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());
                    getPaypervideoList();

                }, throwable -> {
                    displayErrorLayout("Oops!! Please try again after sometime.");
                   // Toast.makeText(PayPerViewVideoListActivity.this, "Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void getPaypervideoList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable payperviewListDisposable = usersService.getPayperviewVideolist(HappiApplication.getAppToken(), SharedPreferenceUtility.getUserId(),"android-phone",SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getWatchListResponseModel -> {
                    if (getWatchListResponseModel.getData().size() != 0) {
                        payPerViewVideoList = getWatchListResponseModel.getData();
                        populateList(payPerViewVideoList);
                        dismissProgress();
                    } else {
                        rv_payperviewvideoList.setVisibility(View.GONE);
                        dismissProgress();
                        displayErrorLayout("Oops!! Your Video List is empty.");
                    }

                }, throwable -> {
                    displayErrorLayout("Oops!! Please try again after sometime.");
                    //Toast.makeText(PayPerViewVideoListActivity.this, "Please try again after sometime.", Toast.LENGTH_SHORT).show();

                });
       // dismissProgress();
        compositeDisposable.add(payperviewListDisposable);
    }
    private void populateList(List<GetPayperviewVideoListModel> payperviewVideoList) {

        dismissProgress();
        rv_payperviewvideoList.setVisibility(View.VISIBLE);
        payPerViewVideoListAdapter.clearAll();
        payPerViewVideoListAdapter.addAll(payperviewVideoList);
        loadingVideos.hide();
        payPerViewVideoListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_payperviewvideoList, mSelectedItem);
        if(payPerViewVideoListAdapter.isEmpty()){
            rv_payperviewvideoList.setVisibility(View.GONE);
        }
        if(sw_list.isRefreshing()){
            sw_list.setRefreshing(false);
        }
    }
    private void displayErrorLayout(String message){
        dismissProgress();
        if(sw_list.isRefreshing()){
            sw_list.setRefreshing(false);
        }
        rv_payperviewvideoList.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
        tv_errormsg.setVisibility(View.VISIBLE);
        ll_error.setVisibility(View.VISIBLE);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final AnimationItem item) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, item.getResourceId());
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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
        if(SharedPreferenceUtility.getGuest()){
            sw_list.setEnabled(false);
        }else{
            sw_list.setEnabled(true);
        }

    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable item : disposables) {
            if (item != null && !item.isDisposed()) {
                item.dispose();
            }
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void showProgress(){
        if(progressDialog != null){
            progressDialog.show();
        }
    }
    @Override
    public void onShowItemClicked(int adapterPosition) {
        SharedPreferenceUtility.setVideoId(payPerViewVideoListAdapter.getItem(adapterPosition).getVideo_id());
        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, payPerViewVideoListAdapter.getItem(adapterPosition).getVideo_id());
       // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);
            payPerViewVideoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
