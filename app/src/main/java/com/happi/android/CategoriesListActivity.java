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

import com.happi.android.adapters.CategoryListAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.CategoryModel;
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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CategoriesListActivity extends BaseActivity implements CategoryListAdapter.itemClickListener {

    TypefacedTextViewRegular tv_title;
    ImageView iv_menu, iv_back, iv_logo_text, iv_search;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_category_list;
    CategoryListAdapter categoryList_adapter;
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

        iv_back.setOnClickListener(v ->
                CategoriesListActivity.super.onBackPressed());
    }

    private void setupRecyclerview() {

        rv_category_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_category_list.addItemDecoration(new ItemDecorationAlbumColumns(7,3));
        categoryList_adapter = new CategoryListAdapter(getApplicationContext(),
                this::onCategoryItemClicked, true);
        rv_category_list.setAdapter(categoryList_adapter);

        loadingCategories = Skeleton.bind(rv_category_list)
                .adapter(categoryList_adapter)
                .load(R.layout.loading_categories_vertical)
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
                    if (isConnected){
                      //  Log.d("^&^&^&","CatList"+"^&^&^&");
                       // loadCategories();
                    }

                });
        loadCategoryList();
    }

    private void loadCategoryList(){
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

        categoryList_adapter.clearAll();
        categoryList_adapter.addAll(categories);
        loadingCategories.hide();
        categoryList_adapter.notifyDataSetChanged();
        runLayoutAnimation(rv_category_list, mSelectedItem);
        if(categoryList_adapter.isEmpty()){
            rv_category_list.setVisibility(View.GONE);
        }
    }


    private void displayErrorLayout(String message) {
        rv_category_list.setVisibility(View.GONE);

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(message);
    }

    @Override
    public void onCategoryItemClicked(int adapterPosition) {

        HappiApplication.setCategoryId(categoryList_adapter
                .getItem(adapterPosition).getCategoryid() + ";" + categoryList_adapter.getItem
                (adapterPosition).getCategory());
        ActivityChooser.goToActivity(ConstantUtils.CATEGORYVIEW_ACTIVITY, categoryList_adapter
                .getItem(adapterPosition).getCategoryid() + ";" + categoryList_adapter.getItem
                (adapterPosition).getCategory());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        super.onResume();
        HappiApplication.setCurrentContext(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }
}
