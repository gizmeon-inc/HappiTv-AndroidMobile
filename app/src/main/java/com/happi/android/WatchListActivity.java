package com.happi.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.happi.android.adapters.WatchListAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.LoginRegisterAlert;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.GetWatchListModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends BaseActivity implements WatchListAdapter.OnItemClicked,
        WatchListAdapter.OnLongItemclicked, LoginRegisterAlert.OnLoginRegisterUserNegative,
        LoginRegisterAlert.OnLoginRegisterUserNeutral {

    private CompositeDisposable compositeDisposable;
    private List<GetWatchListModel> watchListModelsList;
    private WatchListAdapter watchListAdapter;
    private GridRecyclerView rv_watchList;
    ImageView iv_menu;
    ImageView iv_back;
    ImageView iv_logo_text;
    TypefacedTextViewRegular tv_title;
    LinearLayout rl_end_icons;
    public static String pageContext = "";
    ProgressDialog progressDialog;
    SkeletonScreen loadingVideos;
    private AnimationItem[] mAnimationItems;
    private AnimationItem mSelectedItem;
    private ImageView iv_errorimg;
    private TypefacedTextViewRegular tv_errormsg;
    LinearLayout ll_error;
    private SwipeRefreshLayout sw_list;
    public static Activity currentActivity;
    private String title = "";


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

        currentActivity = this;
        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        if (getIntent() != null && getIntent().getStringExtra("pageContext") != null && !getIntent().getStringExtra("pageContext").isEmpty()) {
            pageContext = getIntent().getStringExtra("pageContext");
        } else {
            pageContext = "";
        }
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        progressDialog = new ProgressDialog(WatchListActivity.this, R.style.MyTheme);
        progressDialog.setCancelable(false);
       // progressDialog.show();

        compositeDisposable = new CompositeDisposable();



        watchListModelsList = new ArrayList<>();
        rv_watchList = findViewById(R.id.rv_watchList);
        rv_watchList.setLayoutManager(new GridLayoutManager(this, 3));
        rv_watchList.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        rv_watchList.setLongClickable(true);
        watchListAdapter = new WatchListAdapter(pageContext, watchListModelsList, getApplicationContext(), this::onShowItemClicked, this::onShowItemLongClick);
        rv_watchList.setAdapter(watchListAdapter);
        rv_watchList.setVisibility(View.GONE);

        /*if (getIntent().getStringExtra("pageContext") != null && !getIntent().getStringExtra("pageContext").isEmpty()) {
            pageContext = getIntent().getStringExtra("pageContext");
        } else {
            pageContext = "";
        }*/


        if(pageContext != null && !pageContext.isEmpty()){
            if(pageContext.equalsIgnoreCase("Favourites")){
                title = "Favorites";
            }else{
                title = pageContext;
            }

        }else{
            title = "";
        }

       // getSessionToken();

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

        tv_title.setText(title);
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

            }});
    }

    private void loadData(){
        if( SharedPreferenceUtility.getGuest()){
            displayErrorLayout("Oops!! You don't have any data.");
            if(sw_list.isRefreshing()){
                sw_list.setRefreshing(false);
            }
            showLoginOrRegisterAlert();
        }else{
            if(HappiApplication.getAppToken()== null || HappiApplication.getAppToken().isEmpty()){
                getSessionToken();
            }else{
                if (!pageContext.isEmpty()) {
                    if (pageContext.equalsIgnoreCase("Favourites")) {
                        getFavouriteList();
                    } else if (pageContext.equalsIgnoreCase("Watch List")) {
                        getWatchList();
                    }
                }else{
                    displayErrorLayout("Oops!! You don't have any data.");
                    if(sw_list.isRefreshing()){
                        sw_list.setRefreshing(false);
                    }
                }
            }
        }
//       if(sw_list.isRefreshing()){
//            sw_list.setRefreshing(false);
//        }

    }
    private void showLoginOrRegisterAlert() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String screen = "List";
        if(!title.isEmpty()){
            screen = title;
        }
        String message = "Please Login or Register to see your "+screen+".";
        LoginRegisterAlert alertDialog =
                new LoginRegisterAlert(this, message, "Ok", "Cancel", this::onLoginRegisterNegativeClick,
                        this::onLoginRegisterNeutralClick, true);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    private void setupRecyclerView() {
        rv_watchList.setVisibility(View.VISIBLE);
      //  rv_watchList.setLayoutManager(new GridLayoutManager(this, 3));
      //  rv_watchList.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        watchListAdapter = new WatchListAdapter(pageContext, watchListModelsList, getApplicationContext(), this::onShowItemClicked, this::onShowItemLongClick);
        rv_watchList.setAdapter(watchListAdapter);

        loadingVideos = Skeleton.bind(rv_watchList)
                .adapter(watchListAdapter)
                .load(R.layout.loading_videos_vertical)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();
        Disposable internetDisposable = ReactiveNetwork.observeInternetConnectivity()
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

        loadData();
       // getSessionToken();
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
                    if (!pageContext.isEmpty()) {
                        if (pageContext.equalsIgnoreCase("Favourites")) {
                            getFavouriteList();
                        } else if (pageContext.equalsIgnoreCase("Watch List")) {
                            getWatchList();
                        }
                    }else{
                        displayErrorLayout("Oops!! You don't have any data.");
                        if(sw_list.isRefreshing()){
                            sw_list.setRefreshing(false);
                        }
                    }

                }, throwable -> {
                    displayErrorLayout("Server Error. Please try after sometime.");
                    dismissProgress();
                    Toast.makeText(WatchListActivity.this, "Server error. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void getWatchList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable watchListDisposable = usersService.getWatchlist(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(),
                SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getWatchListResponseModel -> {
                    if (getWatchListResponseModel.getData().size() != 0) {
                        watchListModelsList = getWatchListResponseModel.getData();
                        populateList(watchListModelsList);
                        dismissProgress();
                        // watchListAdapter.notifyDataSetChanged();
                        // rv_watchList.setVisibility(View.VISIBLE);
                    } else {
                        rv_watchList.setVisibility(View.GONE);
                        dismissProgress();
                        displayErrorLayout("Oops! Your List is empty.");
                    }

                }, throwable -> {
                    if(sw_list.isRefreshing()){
                        sw_list.setRefreshing(false);
                    }
                    dismissProgress();
                    displayErrorLayout("Server error. Please try again after sometime.");
                    Toast.makeText(WatchListActivity.this, "Server error. Please try again after sometime.", Toast.LENGTH_SHORT).show();

                });
       // dismissProgress();
        compositeDisposable.add(watchListDisposable);
    }

    private void getFavouriteList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable favouriteDisposable = usersService.getFavouritesList(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(), SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getResponseModel -> {

                    if (getResponseModel.getData().size() != 0) {
                        watchListModelsList = getResponseModel.getData();
                        populateList(watchListModelsList);
                        dismissProgress();
                        //  watchListAdapter.notifyDataSetChanged();
                        //  rv_watchList.setVisibility(View.VISIBLE);
                    } else {
                        if(sw_list.isRefreshing()){
                            sw_list.setRefreshing(false);
                        }
                        rv_watchList.setVisibility(View.GONE);
                        dismissProgress();
                        displayErrorLayout("Oops! Your List is empty.");
                    }

                }, throwable -> {
                    if(sw_list.isRefreshing()){
                        sw_list.setRefreshing(false);
                    }
                    dismissProgress();
                    displayErrorLayout("Server error. Please try again after sometime.");
                    Toast.makeText(WatchListActivity.this, "Server error. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
       // dismissProgress();
        compositeDisposable.add(favouriteDisposable);

    }

    private void populateList(List<GetWatchListModel> showList) {

        dismissProgress();
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        ll_error.setVisibility(View.GONE);

        rv_watchList.setVisibility(View.VISIBLE);

        watchListAdapter.clearAll();
        watchListAdapter.addAll(showList);
        loadingVideos.hide();
        watchListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_watchList, mSelectedItem);
        if(watchListAdapter.isEmpty()){
            rv_watchList.setVisibility(View.GONE);
            displayErrorLayout("Oops!! You don't have any data.");
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
        rv_watchList.setVisibility(View.GONE);
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
        if(pageContext != null && !pageContext.isEmpty() && pageContext.equalsIgnoreCase("Watch List")){
            SharedPreferenceUtility.setCurrentBottomMenuIndex(3);
            if(getMenuItem() != R.id.item_my_list){
                updateMenuItem(3);
            }
        }else{
            updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        }

        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        if(SharedPreferenceUtility.getGuest()){
            sw_list.setEnabled(false);
        }else{
            sw_list.setEnabled(true);
        }
        if(HappiApplication.isNeedsToBeRefreshed()){
            HappiApplication.setNeedsToBeRefreshed(false);
            sw_list.setRefreshing(true);
            loadData();
        }else{
            sw_list.setRefreshing(false);
        }

      //  sw_list.setRefreshing(true);
       /* if( SharedPreferenceUtility.getGuest()){
            displayErrorLayout("No Data");
        }else{
            if(HappiApplication.getAppToken()== null || HappiApplication.getAppToken().isEmpty()){
                getSessionToken();
            }else{
                if (!pageContext.isEmpty()) {
                    if (pageContext.equalsIgnoreCase("Favourites")) {
                        getFavouriteList();
                    } else if (pageContext.equalsIgnoreCase("Watch List")) {
                        getWatchList();
                    }
                }
            }
        }*/
        super.onResume();
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

    private void showAlert(String showName, String showId, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("Do you want to remove " + "<font color = '#34A7CD'>"+showName+"</font>"  + " from  " + pageContext + " ?"));
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //call remove from list api

                removeShowFromList(showId, adapterPosition);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog disappears
                dialog.cancel();
                watchListAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeShowFromList(String selectedShowId, int adapterPosition) {
        showProgress();
        ApiClient.UsersService usersService = ApiClient.create();

        if (pageContext.equalsIgnoreCase("favourites")) {
            Disposable favDisposable = usersService.likeOrUnlikeShow(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(),
                    selectedShowId, SharedPreferenceUtility.getUserId(), 0, BuildConfig.VERSION_NAME)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(watchListShowsResponseModel -> {
                        if (watchListShowsResponseModel.getStatus() == 100) {

                            watchListAdapter.removeItem(adapterPosition);
                            watchListAdapter.notifyDataSetChanged();
                            if(watchListAdapter.isEmpty()){
                                displayErrorLayout("Oops! Your List is empty.");
                            }
                            dismissProgress();
                        } else {
                            dismissProgress();
                            Toast.makeText(this, "Can't connect to server. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                            watchListAdapter.notifyDataSetChanged();
                        }
                    }, throwable -> {
                        dismissProgress();
                        displayErrorLayout("Oops! Your List is empty.");
                    });

            compositeDisposable.add(favDisposable);

        } else if (pageContext.equalsIgnoreCase("watch list")) {

            Disposable watchDisposable = usersService.addToWatchList(HappiApplication.getAppToken(), SharedPreferenceUtility.getPublisher_id(),
                    selectedShowId, SharedPreferenceUtility.getUserId(), 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(watchListShowsResponseModel -> {
                        if (watchListShowsResponseModel.getStatus() == 100) {

                            watchListAdapter.removeItem(adapterPosition);
                            watchListAdapter.notifyDataSetChanged();
                            if(watchListAdapter.isEmpty()){
                                displayErrorLayout("Oops! Your List is empty.");
                            }
                            dismissProgress();
                        } else {
                            dismissProgress();
                            Toast.makeText(this, "Can't connect to server. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                            watchListAdapter.notifyDataSetChanged();
                        }
                    }, throwable -> {
                        dismissProgress();
                        displayErrorLayout("Oops! Your List is empty.");
                    });

            compositeDisposable.add(watchDisposable);
        }

    }

    @Override
    public void onShowItemClicked(int adapterPosition) {
        SharedPreferenceUtility.setShowId(watchListAdapter.getItem(adapterPosition).getShow_id()+"+");
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY, watchListAdapter.getItem(adapterPosition).getShow_id()+"+");
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);
        if(pageContext.equalsIgnoreCase("Watch List")){
            watchListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onShowItemLongClick(int adapterPosition) {
        showAlert(watchListAdapter.getItem(adapterPosition).getShow_name(), watchListAdapter.getItem(adapterPosition).getShow_id(), adapterPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
    private void goToLoginScreen() {
        Intent loginIntent = new Intent(WatchListActivity.this, SubscriptionLoginActivity.class);
        loginIntent.putExtra("from", pageContext);
        startActivity(loginIntent);
        // finish();
    }
    @Override
    public void onLoginRegisterNegativeClick() {
        goToLoginScreen();
    }

    @Override
    public void onLoginRegisterNeutralClick() {

    }
}
