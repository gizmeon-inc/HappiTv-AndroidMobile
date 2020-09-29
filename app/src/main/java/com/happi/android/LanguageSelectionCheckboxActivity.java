package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.happi.android.adapters.ListCheckboxAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.LanguageModel;
import com.happi.android.models.SetLanguagePriorityRequestModel;
import com.happi.android.recyclerview.AnimationItem;
import com.happi.android.webservice.ApiClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LanguageSelectionCheckboxActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private int userId;
    private String accessToken;
    private CompositeDisposable compositeDisposable;
    private RecyclerView rv_languages;
    private Button bt_continue;
    private ListCheckboxAdapter listCheckboxAdapter;
    private List<LanguageModel.LanguageList> languageList;
    private AnimationItem mSelectedItem;
    private String from="";

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
        setContentView(R.layout.activity_language_selection_checkbox);

        userId = SharedPreferenceUtility.getUserId();
        accessToken = HappiApplication
                .getAppToken();
        compositeDisposable = new CompositeDisposable();

        AnimationItem[] mAnimationItems = getAnimationItems();
        mSelectedItem = mAnimationItems[0];

        bt_continue = findViewById(R.id.bt_continue);
        TypefacedTextViewRegular tv_title;
        ImageView iv_menu, iv_back, iv_logo_text, iv_search;
        tv_title = findViewById(R.id.tv_title);
        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        iv_search = findViewById(R.id.iv_search);
        rv_languages = findViewById(R.id.rv_languages);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.language);
        iv_back.setVisibility(View.VISIBLE);
        iv_menu.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);

        List<LanguageModel.LanguageList> languageLists = new ArrayList<>();
        rv_languages.setLayoutManager(new LinearLayoutManager(this));
        listCheckboxAdapter = new ListCheckboxAdapter(languageLists);
        rv_languages.setAdapter(listCheckboxAdapter);

        bt_continue.setClickable(false);
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.show();

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if (from.equals("settings")) {

            GetUserLanguages();
        } else if (from.equals("register")) {
            iv_back.setVisibility(View.INVISIBLE);
            loadLanguages();
        }

        bt_continue.setOnClickListener(v -> {

            if (bt_continue.isClickable()) {
                StringBuilder lang_idsBuilder = new StringBuilder();
                for (LanguageModel.LanguageList languageList : languageList) {

                    if (languageList.isSelected()) {

                        lang_idsBuilder.append(languageList.getLanguage_id()).append(",");
                    }
                }
                String lang_ids = "";
                lang_ids = lang_idsBuilder.toString();

                if (!lang_ids.isEmpty()) {

                    String lang_id_string = lang_ids.substring(0, lang_ids.length() - 1);
                    setLanguagesApiCall(lang_id_string);
                } else {

                    Toast toast = Toast.makeText(HappiApplication.getCurrentContext(), "Please select " +
                            "atleast one Language", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            } else {

                Toast.makeText(this, "Please wait till language list is loaded", Toast.LENGTH_SHORT).show();
            }
        });

        iv_back.setOnClickListener(v -> {


            LanguageSelectionCheckboxActivity.super.onBackPressed();
        });
    }

    private void loadLanguages() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.GetLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(languageModel -> {

                    if (progressDialog.isShowing()) {

                        progressDialog.dismiss();
                    }
                    for (int i = 1; i < languageModel.getData().size(); i++) {

                        if (languageModel.getData().get(i).getLanguage_id() == 2) {

                            languageModel.getData().get(i).setSelected(true);
                        }
                    }
                    languageList = languageModel.getData();
                    listCheckboxAdapter = new ListCheckboxAdapter(languageModel.getData());
                    rv_languages.setAdapter(listCheckboxAdapter);
                    runLayoutAnimation(rv_languages, mSelectedItem);
                    bt_continue.setClickable(true);
                    bt_continue.setVisibility(View.VISIBLE);
                }, throwable -> {

                    if (progressDialog.isShowing()) {

                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(videoDisposable);
    }

    private void GetUserLanguages() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable languageDisposable = usersService.GetUserLanguages(accessToken, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(languageModel -> {

                    if (progressDialog.isShowing()) {

                        progressDialog.dismiss();
                    }
                    languageList = languageModel.getData();
                    listCheckboxAdapter = new ListCheckboxAdapter(languageModel.getData());
                    rv_languages.setAdapter(listCheckboxAdapter);
                    runLayoutAnimation(rv_languages, mSelectedItem);
                    bt_continue.setClickable(true);
                    bt_continue.setVisibility(View.VISIBLE);
                }, throwable -> {

                    if (progressDialog.isShowing()) {

                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(languageDisposable);
    }

    private void setLanguagesApiCall(String lang_ids) {

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.show();

        SetLanguagePriorityRequestModel setLanguagePriorityRequestModel = new
                SetLanguagePriorityRequestModel();
        setLanguagePriorityRequestModel.setUid(userId);
        setLanguagePriorityRequestModel.setLang_list(lang_ids);

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.SetLanguagePriority(HappiApplication
                .getAppToken(), setLanguagePriorityRequestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(basicResponse -> {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (from.equals("settings")) {


                        Toast.makeText(this, "Languages Updated", Toast.LENGTH_SHORT).show();
                        LanguageSelectionCheckboxActivity.super.onBackPressed();
                    } else if (from.equals("register")) {

                        Intent intent = new Intent(LanguageSelectionCheckboxActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }, throwable -> {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(videoDisposable);
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
                new AnimationItem("Slide from bottom", R.anim.layout_animation_from_bottom)
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        HappiApplication.setCurrentContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(compositeDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(from.equals("register")){
            Intent intentback = new Intent(LanguageSelectionCheckboxActivity.this,LoginActivity.class);
            intentback.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intentback);

        }else if(from.equals("settings")){
            super.onBackPressed();

        }
    }
}
