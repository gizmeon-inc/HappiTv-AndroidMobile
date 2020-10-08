package com.happi.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.happi.android.adapters.PartnerVideoListAdapter;
import com.happi.android.adapters.SearchResultsAdapter;
import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.PartnerVideoListResponseModel;
import com.happi.android.models.ShowModel;
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

public class PartnerVideosListingActivity extends BaseActivity implements PartnerVideoListAdapter.PartnerVideoItemClickListener {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu;
    ImageView iv_back;
    ImageView iv_logo_text;
    ImageView iv_search;

    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_video_list;
    PartnerVideoListAdapter partnerVideoListAdapter;
    private AnimationItem mSelectedItem;
    private CompositeDisposable compositeDisposable;
    SkeletonScreen loadingVideos;

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

        compositeDisposable = new CompositeDisposable();

        setupRecyclerview();

        String partnerId = "";
        String partner = "";
        if(getIntent() != null && getIntent().getStringExtra(ConstantUtils.PARTNER_DETAILS) != null){
            partner = getIntent().getStringExtra(ConstantUtils.PARTNER_DETAILS);
        }else{
            partner = SharedPreferenceUtility.getPartnerId();
        }


        if (partner != null && !partner.isEmpty()) {
            if(partner.contains(";")){
                String categoryid = partner.split(";")[0];
                partnerId = categoryid;
                String categorytitle = partner.split(";")[1];
                tv_title.setText(categorytitle);
                getVideosByPartner(partnerId);
            }

        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backNavigation();
            }
        });

    }

    private void backNavigation(){
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }

    private void getVideosByPartner(String id){

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable showDisposable = usersService.getPartnerVideos(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(),
                id,SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(partnerVideoListResponseModel -> {
                    if(partnerVideoListResponseModel.getData().size() != 0){
                        updateShowList(partnerVideoListResponseModel.getData());
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


    private void updateShowList(List<PartnerVideoListResponseModel.PartnerVideoModel> partnerVideoModels) {
        partnerVideoListAdapter.clearAll();
        partnerVideoListAdapter.addAll(partnerVideoModels);
        loadingVideos.hide();
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_video_list.setVisibility(View.VISIBLE);
        partnerVideoListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_video_list, mSelectedItem);
        if (partnerVideoListAdapter.isEmpty()) {
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

        rv_video_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_video_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        partnerVideoListAdapter = new PartnerVideoListAdapter(this,
                this);
        rv_video_list.setAdapter(partnerVideoListAdapter);

        loadingVideos = Skeleton.bind(rv_video_list)
                .adapter(partnerVideoListAdapter)
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
    public void onPartnerVideoItemClicked(int adapterPosition) {
        SharedPreferenceUtility.setShowId(partnerVideoListAdapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY,partnerVideoListAdapter.getItem(adapterPosition).getShow_id());
    }
}
