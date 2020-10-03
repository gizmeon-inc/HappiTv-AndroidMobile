package com.happi.android;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.happi.android.adapters.CategoryCircleViewAdapter;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.CategoryModel;
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

public class CategoriesListActivity extends BaseActivity implements CategoryCircleViewAdapter.itemClickListenerForCategory {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu, iv_back, iv_logo_text, iv_search;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_category_list;
    private CategoryCircleViewAdapter circleViewAdapter;
    private AnimationItem mSelectedItem;
    private Disposable internetDisposable;
    private CompositeDisposable compositeDisposable;
    SkeletonScreen loadingCategories;

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
        setContentView(R.layout.activity_categories);

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
        tv_title.setText(R.string.categories);
        iv_back.setVisibility(View.VISIBLE);
        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        rv_category_list = findViewById(R.id.rv_category_list);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        tv_errormsg = findViewById(R.id.tv_errormsg);


        compositeDisposable = new CompositeDisposable();
        setupRecyclerview();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setupRecyclerview() {

        rv_category_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_category_list.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        circleViewAdapter = new CategoryCircleViewAdapter(this, this::onCategoryItemClickedForCircleView, true);
        rv_category_list.setAdapter(circleViewAdapter);

        loadingCategories = Skeleton.bind(rv_category_list)
                .adapter(circleViewAdapter)
                .load(R.layout.item_category_circle_loading_vert)
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
                    // Log.d("^&^&^&","CatList"+isConnected);
                    if (isConnected) {
                        //  Log.d("^&^&^&","CatList"+"^&^&^&");
                        // loadCategories();
                    }

                });
        loadCategoryList();
    }

    private void loadCategoryList() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable cateogoryDisposable = usersService.GetTheme(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryListResponseModel -> {

                    if (categoryListResponseModel.getData().size() != 0) {

                        updateCategoryList(categoryListResponseModel.getData());
                    } else {

                        displayErrorLayout(getString(R.string.no_results_found));
                    }


                }, throwable -> {
                    displayErrorLayout(getString(R.string.server_error));
                });
        compositeDisposable.add(cateogoryDisposable);
    }

    private void loadCategories() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.GetTheme(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CategoryModel>() {
                    @Override
                    public void accept(CategoryModel categoryListResponseModel) {

                        if (categoryListResponseModel.getData().size() != 0) {

                            updateCategoryList(categoryListResponseModel.getData());
                        } else {

                            displayErrorLayout(getString(R.string.no_results_found));
                        }

                    }
                }, throwable -> displayErrorLayout(getString(R.string.server_error)));
        compositeDisposable.add(videoDisposable);
    }


    private void updateCategoryList(List<CategoryModel.Category> categories) {

        circleViewAdapter.clearAll();
        circleViewAdapter.addAll(categories);
        loadingCategories.hide();
        circleViewAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_category_list, mSelectedItem);
        if (circleViewAdapter.isEmpty()) {
            rv_category_list.setVisibility(View.GONE);
            displayErrorLayout(getString(R.string.no_results_found));
        }
    }


    private void displayErrorLayout(String message) {
        rv_category_list.setVisibility(View.GONE);

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

        safelyDispose(internetDisposable, compositeDisposable);
    }

    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        SharedPreferenceUtility.setCurrentBottomMenuIndex(2);
        if(getMenuItem() != R.id.item_categories){
            updateMenuItem(2);
        }
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
    public void onCategoryItemClickedForCircleView(int adapterPosition) {
        HappiApplication.setCategoryId(circleViewAdapter
                .getItem(adapterPosition).getCategoryid() + ";" + circleViewAdapter.getItem
                (adapterPosition).getCategory());
        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, circleViewAdapter
                .getItem(adapterPosition).getCategoryid() + ";" + circleViewAdapter.getItem
                (adapterPosition).getCategory());
        overridePendingTransition(0, 0);
    }
}
