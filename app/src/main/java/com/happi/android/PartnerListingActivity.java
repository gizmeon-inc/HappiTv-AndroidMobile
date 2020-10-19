package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.happi.android.adapters.PartnersListingAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.SpacesItemDecoration;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.PartnerResponseModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PartnerListingActivity extends BaseActivity implements PartnersListingAdapter.PartnerItemClickListener {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu, iv_back, iv_logo_text, iv_search;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    //GridRecyclerView rv_partnerList;
    RecyclerView rv_partnerList;
    private PartnersListingAdapter partnersListingAdapter;
    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

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
        //setContentView(R.layout.activity_categories);
        setContentView(R.layout.activity_partner_videos_listing);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();


        tv_title = findViewById(R.id.tv_title);
        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        iv_search = findViewById(R.id.iv_search);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.partners);
        iv_back.setVisibility(View.VISIBLE);
        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        rv_partnerList = findViewById(R.id.rv_category_list);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);


        compositeDisposable = new CompositeDisposable();
        //setupRecyclerview();
        //load partner list
        //int spacingPixels = getResources().getDimensionPixelSize(R.dimen.default_spacing_small);
       // rv_partnerList.addItemDecoration(new SpacesItemDecoration(spacingPixels));
        rv_partnerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        partnersListingAdapter = new PartnersListingAdapter(this, false, this);
        rv_partnerList.setAdapter(partnersListingAdapter);

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadPartnersList();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /*private void setupRecyclerview() {
        //rv_partnerList.setLayoutManager(new GridLayoutManager(this, 1));
        //rv_partnerList.addItemDecoration(new ItemDecorationAlbumColumns(2, 1));
        rv_partnerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        partnersListingAdapter = new PartnersListingAdapter(this, false, this);
        rv_partnerList.setAdapter(partnersListingAdapter);
        loadingPartners = Skeleton.bind(rv_partnerList)
                .adapter(partnersListingAdapter)
                .load(R.layout.item_partner_info_list_skeleton)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();


       *//* rv_partnerList.setLayoutManager(new GridLayoutManager(this, 3));
        rv_partnerList.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        partnersListingAdapter = new PartnersListingAdapter(this, false, this);
        rv_partnerList.setAdapter(partnersListingAdapter);

        loadingPartners = Skeleton.bind(rv_partnerList)
                .adapter(partnersListingAdapter)
                .load(R.layout.item_partner_info_home_skeleton)
                .color(R.color.colorLine)
                .shimmer(true)
                .angle(30)
                .count(30)
                .duration(1000)
                .frozen(false)
                .show();*//*

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    // Log.d("^&^&^&","CatList"+isConnected);
                    if (isConnected) {
                        //  Log.d("^&^&^&","CatList"+"^&^&^&");
                        // loadCategories();
                    }

                });
        loadPartnersList();
    }*/

    private void loadPartnersList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable = usersService.getPartnerList(HappiApplication.getAppToken(),SharedPreferenceUtility.getCountryCode(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(partnerResponseModel -> {

                    if (partnerResponseModel.getData().size() != 0) {

                        updateCategoryList(partnerResponseModel.getData());
                    } else {

                        displayErrorLayout(getString(R.string.no_results_found));
                    }


                }, throwable -> {
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void updateCategoryList(List<PartnerResponseModel.PartnerModel> partnerModels) {

        dismissProgressDialog();

        partnersListingAdapter.clearAll();
        partnersListingAdapter.addAll(partnerModels);
        //loadingPartners.hide();
        partnersListingAdapter.notifyDataSetChanged();
        //runLayoutAnimation(rv_partnerList, mSelectedItem);
        if (partnersListingAdapter.isEmpty()) {
            rv_partnerList.setVisibility(View.GONE);
            displayErrorLayout(getString(R.string.no_results_found));
        }
    }


    private void displayErrorLayout(String message) {
        dismissProgressDialog();

        rv_partnerList.setVisibility(View.GONE);

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
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

        //safelyDispose(internetDisposable, compositeDisposable);
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
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0);

    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    @Override
    public void onPartnerItemClicked(int adapterPosition) {
        String partnerDetails = partnersListingAdapter.getItem(adapterPosition).getPartner_id() + ";" + partnersListingAdapter.getItem
                (adapterPosition).getName();
        SharedPreferenceUtility.setPartnerId(partnerDetails);
        ActivityChooser.goToActivity(ConstantUtils.PARTNER_VIDEOS_LISTING_ACTIVITY, partnerDetails);
        overridePendingTransition(0, 0);
    }

    private void dismissProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}