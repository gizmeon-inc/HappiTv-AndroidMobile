package com.happi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.happi.android.adapters.ChannelListAdapter;
import com.happi.android.adapters.ChannelSearchSuggestionAdapter;
import com.happi.android.adapters.SearchResultsAdapter;
import com.happi.android.adapters.ShowList_adapter;
import com.happi.android.adapters.ShowSearchSuggestionAdapter;
import com.happi.android.adapters.VideoSearchSuggestionAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.ItemDecorationAlbumColumns;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelModel;
import com.happi.android.models.ShowModel;
import com.happi.android.models.VideoModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.recyclerview.GridRecyclerView;
import com.happi.android.roomController.RoomChannelSearchModel;
import com.happi.android.roomController.RoomChannelSearchRepository;
import com.happi.android.roomController.RoomShowSearchModel;
import com.happi.android.roomController.RoomShowSearchRepository;
import com.happi.android.roomController.RoomVideoSearchModel;
import com.happi.android.roomController.RoomVideoSearchRepository;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends BaseActivity implements SearchResultsAdapter
        .itemClickListener, ChannelListAdapter.itemClickListener, VideoSearchSuggestionAdapter
        .suggestedVideoItemClickListener, ChannelSearchSuggestionAdapter.suggestedChannelItemClickListener,
         ShowList_adapter.itemClickListener,ShowSearchSuggestionAdapter.suggestedShowItemClickListener{

    EditText et_search;
    ImageView iv_back;
    Timer timer;
    ProgressBar pb_progressbar;
    TypefacedTextViewRegular tv_errormsg;
    ImageView iv_errorimg;
    GridRecyclerView rv_search_result;
    GridRecyclerView rv_channel_result;
    SearchResultsAdapter searchResultsAdapter;
    ChannelListAdapter channelListAdapter;
    private AnimationItem mSelectedItem;
    //SkeletonScreen loadingVideos;
    private Disposable internetDisposable;
    private CompositeDisposable compositeDisposable;
    private String search_type;
    private int userId = 0;

    ShowList_adapter showList_adapter;

    VideoSearchSuggestionAdapter videoSearchSuggestionAdapter;
    ChannelSearchSuggestionAdapter channelSearchSuggestionAdapter;
    ShowSearchSuggestionAdapter showSearchSuggestionAdapter;
    RecyclerView rv_search_suggestion;

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
        setContentView(R.layout.activity_search);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        compositeDisposable = new CompositeDisposable();
        et_search = findViewById(R.id.et_search);
        iv_back = findViewById(R.id.iv_back);
        tv_errormsg = findViewById(R.id.tv_errormsg);
        iv_errorimg = findViewById(R.id.iv_errorimg);
        rv_search_result = findViewById(R.id.rv_search_result);
        rv_channel_result = findViewById(R.id.rv_channel_result);
        rv_search_suggestion = findViewById(R.id.rv_search_suggestion);
        pb_progressbar = findViewById(R.id.pb_progressbar);


        userId = SharedPreferenceUtility.getUserId();

        Intent intent = getIntent();
        if(getIntent() != null && (getIntent().getStringExtra("search_type") != null  && !getIntent().getStringExtra("search_type").isEmpty())){
            search_type = intent.getStringExtra("search_type");
        }else{
            search_type = "show";
        }

        setupRecyclerview();

        if(search_type.equals("show")){
            et_search.setHint(R.string.search_show);
            loadSearchSuggestions("show");

        } else if (search_type.equals("video")) {

            et_search.setHint(R.string.search_video);
            loadSearchSuggestions("video");
        } else if (search_type.equals("channel")) {

            et_search.setHint(R.string.search_channel);
            loadSearchSuggestions("channel");
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!et_search.getText().toString().trim().isEmpty()) {

                    pb_progressbar.setVisibility(View.VISIBLE);
                } else {

                    rv_search_result.setVisibility(View.GONE);
                    rv_channel_result.setVisibility(View.GONE);
                    pb_progressbar.setVisibility(View.GONE);
                    tv_errormsg.setVisibility(View.GONE);
                    iv_errorimg.setVisibility(View.GONE);
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (!et_search.getText().toString().trim().isEmpty()) {

                            //keyword search API call
                           // loadSearchResult(et_search.getText().toString().trim());
                            loadShowSearchResult(et_search.getText().toString().trim());
                        }
                    }
                }, 750);
            }
        });

        et_search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (!et_search.getText().toString().trim().isEmpty()) {

                    pb_progressbar.setVisibility(View.VISIBLE);
                    hideSoftKeyBoard();
                   // loadSearchResult(et_search.getText().toString().trim());
                    loadShowSearchResult(et_search.getText().toString().trim());
                }
                return true;
            }
            return false;
        });
    }

    private void loadShowSearchResult(String searchKey){

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.searchByshows(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getPublisher_id(),
                searchKey,SharedPreferenceUtility.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showListResponseModel -> {

                        insertSearchSuggestions("show", searchKey);
                        if (showListResponseModel.getShowModelList().isEmpty()) {

                            displayErrorLayout(getString(R.string.no_results_found));
                        } else {

                            updateShowDataList(showListResponseModel.getShowModelList());
                        }

                }, throwable -> {

                    displayErrorLayout(getString(R.string.error_message_loading_users));
                });
        compositeDisposable.add(videoDisposable);

    }

    private void loadSearchResult(String searchKey) {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.Search(HappiApplication.getAppToken(),
                searchKey, search_type, userId, SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchResponseModel -> {

                    if (search_type.equals("video")) {

                        insertSearchSuggestions("video", searchKey);
                        if (searchResponseModel.getVideo_data().isEmpty()) {

                            displayErrorLayout(getString(R.string.no_results_found));
                        } else {

                            updateVideoDataList(searchResponseModel.getVideo_data());
                        }
                    } else if (search_type.equals("channel")) {

                        insertSearchSuggestions("channel", searchKey);
                        if (searchResponseModel.getChannel_data().isEmpty()) {

                            displayErrorLayout(getString(R.string.no_results_found));
                        } else {

                            updateChannelDataList(searchResponseModel.getChannel_data());
                        }
                    }
                }, throwable -> {

                    displayErrorLayout(getString(R.string.error_message_loading_users));
                });
        compositeDisposable.add(videoDisposable);
    }

    private void loadSearchSuggestions(String search_type) {

        if(search_type.equals("show")){
            //show
            RoomShowSearchRepository roomShowSearchRepository = new
                    RoomShowSearchRepository(HappiApplication.getCurrentContext());
            roomShowSearchRepository.getTasks().observe(this, roomShowSearchModels -> {

                HashMap<String, RoomShowSearchModel> modelHashMap = new LinkedHashMap<>();
                for (RoomShowSearchModel roomShowSearchModel : roomShowSearchModels) {
                    modelHashMap.put(roomShowSearchModel.getSearchKeyword(),
                            roomShowSearchModel);
                }
                roomShowSearchModels = new ArrayList<>(modelHashMap.values());

                showSearchSuggestionAdapter = new ShowSearchSuggestionAdapter(this,
                        roomShowSearchModels, this::onSuggestedShowItemClicked);
                rv_search_suggestion.setAdapter(showSearchSuggestionAdapter);
            });
        }
        else if (search_type.equals("video")) {

            RoomVideoSearchRepository roomVideoSearchRepository = new RoomVideoSearchRepository
                    (HappiApplication.getCurrentContext());
            roomVideoSearchRepository.getTasks().observe(this, roomVideoSearchModels -> {

                HashMap<String, RoomVideoSearchModel> modelHashMap = new LinkedHashMap<>();
                for (RoomVideoSearchModel roomVideoSearchModel : roomVideoSearchModels) {
                    modelHashMap.put(roomVideoSearchModel.getSearchKeyword(), roomVideoSearchModel);
                }
                roomVideoSearchModels = new ArrayList<>(modelHashMap.values());

                videoSearchSuggestionAdapter = new VideoSearchSuggestionAdapter(this,
                        roomVideoSearchModels, this::onSuggestedVideoItemClicked);
                rv_search_suggestion.setAdapter(videoSearchSuggestionAdapter);
            });
        } else if (search_type.equals("channel")) {

            RoomChannelSearchRepository roomChannelSearchRepository = new
                    RoomChannelSearchRepository(HappiApplication.getCurrentContext());
            roomChannelSearchRepository.getTasks().observe(this, roomChannelSearchModels -> {

                HashMap<String, RoomChannelSearchModel> modelHashMap = new LinkedHashMap<>();
                for (RoomChannelSearchModel roomChannelSearchModel : roomChannelSearchModels) {
                    modelHashMap.put(roomChannelSearchModel.getSearchKeyword(),
                            roomChannelSearchModel);
                }
                roomChannelSearchModels = new ArrayList<>(modelHashMap
                        .values());

                channelSearchSuggestionAdapter = new ChannelSearchSuggestionAdapter(this,
                        roomChannelSearchModels, this::onSuggestedChannelItemClicked);
                rv_search_suggestion.setAdapter(channelSearchSuggestionAdapter);
            });
        }
    }

    private void insertSearchSuggestions(String search_type, String keyword) {

        if(search_type.equals("show")){
            RoomShowSearchRepository roomShowSearchRepository = new RoomShowSearchRepository
                    (HappiApplication.getCurrentContext());
            roomShowSearchRepository.insertTask(keyword.toLowerCase());

        }else if (search_type.equals("video")) {

            RoomVideoSearchRepository roomVideoSearchRepository = new RoomVideoSearchRepository
                    (HappiApplication.getCurrentContext());
            roomVideoSearchRepository.insertTask(keyword.toLowerCase());
        } else if (search_type.equals("channel")) {

            RoomChannelSearchRepository roomChannelSearchRepository = new RoomChannelSearchRepository
                    (HappiApplication.getCurrentContext());
            roomChannelSearchRepository.insertTask(keyword.toLowerCase());
        }
    }

    private void updateShowDataList(List<ShowModel> showModelList) {

        showList_adapter.clearAll();
        showList_adapter.addAll(showModelList);
        pb_progressbar.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_search_result.setVisibility(View.VISIBLE);
        rv_channel_result.setVisibility(View.GONE);
        rv_search_suggestion.setVisibility(View.GONE);
        //searchResultsAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_search_result, mSelectedItem);
        if (showList_adapter.isEmpty()) {
            rv_search_result.setVisibility(View.GONE);
            rv_channel_result.setVisibility(View.GONE);
        }
    }
    private void updateVideoDataList(List<VideoModel> videoModelList) {

        searchResultsAdapter.clearAll();
        searchResultsAdapter.addAll(videoModelList);
        pb_progressbar.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_search_result.setVisibility(View.VISIBLE);
        rv_channel_result.setVisibility(View.GONE);
        rv_search_suggestion.setVisibility(View.GONE);
        //searchResultsAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_search_result, mSelectedItem);
        if (searchResultsAdapter.isEmpty()) {
            rv_search_result.setVisibility(View.GONE);
            rv_channel_result.setVisibility(View.GONE);
        }
    }

    private void updateChannelDataList(List<ChannelModel> channelModel) {

        channelListAdapter.clearAll();
        channelListAdapter.addAll(channelModel);
        pb_progressbar.setVisibility(View.GONE);
        iv_errorimg.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.GONE);
        rv_search_result.setVisibility(View.GONE);
        rv_channel_result.setVisibility(View.VISIBLE);
        channelListAdapter.notifyDataSetChanged();
        runLayoutAnimation(rv_channel_result, mSelectedItem);
        if (channelListAdapter.isEmpty()) {
            rv_search_result.setVisibility(View.GONE);
            rv_channel_result.setVisibility(View.GONE);
        }
    }

    private void displayErrorLayout(String errorMessage) {
        rv_search_result.setVisibility(View.GONE);
        rv_channel_result.setVisibility(View.GONE);
        rv_search_suggestion.setVisibility(View.GONE);
        pb_progressbar.setVisibility(View.GONE);

        iv_errorimg.setVisibility(View.VISIBLE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(errorMessage);
    }

    private void setupRecyclerview() {

        if(search_type.equals("show")){
            List<RoomShowSearchModel> roomShowSearchModels = new ArrayList<>();
            rv_search_suggestion.setLayoutManager(new LinearLayoutManager(this));
            showSearchSuggestionAdapter = new ShowSearchSuggestionAdapter(getApplicationContext
                    (), roomShowSearchModels, this::onSuggestedShowItemClicked);
            rv_search_suggestion.setAdapter(showSearchSuggestionAdapter);

        }else if (search_type.equals("video")) {

            List<RoomVideoSearchModel> roomVideoSearchModel = new ArrayList<>();
            rv_search_suggestion.setLayoutManager(new LinearLayoutManager(this));
            videoSearchSuggestionAdapter = new VideoSearchSuggestionAdapter(getApplicationContext
                    (), roomVideoSearchModel, this::onSuggestedVideoItemClicked);
            rv_search_suggestion.setAdapter(videoSearchSuggestionAdapter);
        } else if (search_type.equals("channel")) {

            List<RoomChannelSearchModel> roomChannelSearchModel = new ArrayList<>();
            rv_search_suggestion.setLayoutManager(new LinearLayoutManager(this));
            channelSearchSuggestionAdapter = new ChannelSearchSuggestionAdapter(getApplicationContext
                    (), roomChannelSearchModel, this::onSuggestedChannelItemClicked);
            rv_search_suggestion.setAdapter(channelSearchSuggestionAdapter);

        }
        rv_search_result.setLayoutManager(new GridLayoutManager(this, 3));
        rv_search_result.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        showList_adapter = new ShowList_adapter(getApplicationContext(),
                this::onShowsItemClicked,true);
        rv_search_result.setAdapter(showList_adapter);
//        searchResultsAdapter = new SearchResultsAdapter(getApplicationContext(),
//                this::onSearchItemClicked, true);
//        rv_search_result.setAdapter(searchResultsAdapter);

       /* rv_channel_result.setLayoutManager(new GridLayoutManager(this, 3));
        rv_channel_result.addItemDecoration(new ItemDecorationAlbumColumns(7, 3));
        channelListAdapter = new ChannelListAdapter(getApplicationContext(),
                this::onChannelItemClicked, true);
        rv_channel_result.setAdapter(channelListAdapter);*/

        /*internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    if (isConnected) {
                        if (!et_search.getText().toString().isEmpty()) {
                            loadSearchResult(et_search.getText().toString());
                        }
                    } else {
                    }
                });*/
    }
    @Override
    public void onShowsItemClicked(int adapterPosition) {
        hideSoftKeyBoard();
        SharedPreferenceUtility.setShowId(showList_adapter.getItem(adapterPosition).getShow_id());
        ActivityChooser.goToActivity(ConstantUtils.SHOW_DETAILS_ACTIVITY,
                showList_adapter.getItem(adapterPosition).getShow_id());
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);
    }

    @Override
    public void onSearchItemClicked(int adapterPosition) {

        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, searchResultsAdapter
                .getItem(adapterPosition).getVideo_id());
       // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        overridePendingTransition(0,0);
    }

    @Override
    public void onChannelItemClicked(int adapterPosition) {

        ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY,
                channelListAdapter.getItem(adapterPosition).getChannelId());
      //  overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        SharedPreferenceUtility.setCurrentBottomMenuIndex(1);
        if(getMenuItem() != R.id.item_search){
            updateMenuItem(1);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(internetDisposable, compositeDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        if (imm != null && imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onSuggestedVideoItemClicked(int adapterPosition, String searchText) {

        pb_progressbar.setVisibility(View.VISIBLE);
        et_search.setText(searchText);
        //loadSearchResult(searchText);
    }

    @Override
    public void onSuggestedChannelItemClicked(int adapterPosition, String searchText) {

        pb_progressbar.setVisibility(View.VISIBLE);
        et_search.setText(searchText);
        //loadSearchResult(searchText);
    }


    @Override
    public void onSuggestedShowItemClicked(int adapterPosition, String searchKey) {
        pb_progressbar.setVisibility(View.VISIBLE);
        et_search.setText(searchKey);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
