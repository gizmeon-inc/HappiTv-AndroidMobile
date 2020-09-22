package com.happi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.happi.android.adapters.PremiumItemAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.webservice.ApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PremiumActivity extends BaseActivity implements LogoutAlertDialog.onLogoutClickListener,PremiumItemAdapter.itemClickListener, CustomAlertDialog.OnOkClick {
    private List<UserSubscriptionModel> userSubscriptionModelList;
    TypefacedTextViewRegular tv_title;
    TypefacedTextViewRegular tv_error;
    ImageView iv_menu;
    ImageView iv_back;
    ImageView iv_search;
    ImageView iv_logo_text;
    RecyclerView rv_subscription;
    Button btn_cancel;
    PremiumItemAdapter premiumItemAdapter;
    private CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    boolean isItemClicked;
    String sku;
    String packageName;
    UserSubscriptionModel uModel;
    private boolean isFromSubsc = false;

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
        setContentView(R.layout.activity_premium);
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.show();

        userSubscriptionModelList= new ArrayList<>();
        tv_error = findViewById(R.id.tv_error);
        tv_error.setVisibility(View.GONE);
        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_search = findViewById(R.id.iv_search);
        tv_title = findViewById(R.id.tv_title);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        rv_subscription = findViewById(R.id.rv_subscription);
        btn_cancel = findViewById(R.id.btn_cancel);
       // btn_cancel.setClickable(false);
        isItemClicked = false;
        sku = "empty";
        packageName= "empty";
        isFromSubsc = false;

        iv_menu.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.GONE);

        tv_title.setVisibility(View.VISIBLE);

        tv_title.setText(R.string.subscription_list);


        premiumItemAdapter = new PremiumItemAdapter(getApplicationContext(), this);
        rv_subscription.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_subscription.setAdapter(premiumItemAdapter);
        compositeDisposable = new CompositeDisposable();


        if(SharedPreferenceUtility.getGuest()){
            displayErrorMessage(getString(R.string.no_subs_error));
        }else {
            if(HappiApplication.getAppToken().isEmpty()){
                getSessionToken();
            }else{
                getUserSubscriptions();
            }

        }

        iv_back.setOnClickListener(v -> {
          //  goToHome();
            PremiumActivity.super.onBackPressed();
            finish();
        });

        btn_cancel.setOnClickListener(v -> {
        //cancel subscription
           // if (btn_cancel.isClickable()) {
                if(isItemClicked){

                    if(uModel!=null && uModel.getMode_of_payment()!=null && uModel.getMode_of_payment().equals("android-in-app")){
                        if(uModel.getSubscription_type_id()==1||uModel.getSubscription_type_id()==6){
                            Toast.makeText(this, "This Subscription can't be cancelled.", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                // Uri uri = Uri.parse("https://play.google.com/store");
                                //https://play.google.com/store/account/subscriptions?sku=$sku&package=$packageName
                                if(!sku.equals("empty")){
                                    packageName = PremiumActivity.this.packageName;
                                    Uri uri = Uri.parse("https://play.google.com/store/account/subscriptions?sku="+sku+"package="+packageName);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(this, "Try again .No Matching Subscription found.", Toast.LENGTH_SHORT).show();
                                }
                                // } catch (android.content.ActivityNotFoundException anfe) {
                            } catch (Exception e) {
                                // Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName);
                                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }else{
                        String message = "No Matching Subscription found in your Google Play Store Account. Please check with your other platforms.";
                        showAlert(message);
                        //  Toast.makeText(CeyFlixApplication.getCurrentContext(), "No Matching Subscription found in your Google Play Store Account.Please check with your other platforms.", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(this, "Please select a subscription first", Toast.LENGTH_SHORT).show();
                }

          //  }

        });
    }

    private void getExpiryDate() {
        String validTo;
        String valid;
        String validity;
        if(userSubscriptionModelList.size()!=0){
            for(UserSubscriptionModel userModel : userSubscriptionModelList){
                validTo = userModel.getValid_to();
                String validitydate[] = validTo.split("T");
                valid = validitydate[0];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat dateMonthFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                try {
                    Date date = dateFormat.parse(valid);
                    validity = dateMonthFormat.format(date);
                    userModel.setValid_to(validity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            premiumItemAdapter.updateList(userSubscriptionModelList);
            btn_cancel.setVisibility(View.VISIBLE);
           // btn_cancel.setClickable(false);
            isItemClicked = false;
            sku = "empty";
            packageName= "empty";
            /*if(userSubscriptionModelList.get(0).getMode_of_payment() !=null && userSubscriptionModelList.get(0).getMode_of_payment().equals("1")){
                btn_cancel.setClickable(true);
            }else if(userSubscriptionModelList.get(0).getMode_of_payment() !=null && userSubscriptionModelList.get(0).getMode_of_payment().equals("2")){
                btn_cancel.setClickable(false);
            }else{
                btn_cancel.setClickable(false);
            }*/
        }

    }
    private void getSessionToken() {
        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();


        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), appKey, bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {

                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());                            //  FEApplication.setPublisher_id(sessionTokenResponseModel.getPublisher_id());


                            //Test AdIDs
                            //SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-3940256099942544/6300978111", "ca-app-pub-3940256099942544/5224354917","ca-app-pub-3940256099942544/1033173712","ca-app-pub-3940256099942544~3347511713", "1", "0","24534e1901884e398f1253216226017e","b195f8dd8ded45fe847ad89ed1d016da","0");
                            //Live AdIDs
                            //SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-1674238972502360/1600095046", "ca-app-pub-1674238972502360/8688247573","ca-app-pub-1674238972502360/4202207657");
                            //SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(),sessionTokenResponseModel.getInterstitial_id(),sessionTokenResponseModel.getApp_id(),sessionTokenResponseModel.getRewarded_status(),sessionTokenResponseModel.getInterstitial_status(),sessionTokenResponseModel.getMopub_interstitial_id(),sessionTokenResponseModel.getMopub_banner_id(),"0");

                            SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(), sessionTokenResponseModel.getInterstitial_id(), sessionTokenResponseModel.getApp_id(), sessionTokenResponseModel.getRewarded_status(), sessionTokenResponseModel.getInterstitial_status(), sessionTokenResponseModel.getMopub_interstitial_id(), sessionTokenResponseModel.getMopub_banner_id(), sessionTokenResponseModel.getMopub_interstitial_status(), sessionTokenResponseModel.getMopub_rect_banner_id());
                            // SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(),sessionTokenResponseModel.getInterstitial_id(),sessionTokenResponseModel.getApp_id(),sessionTokenResponseModel.getRewarded_status(),sessionTokenResponseModel.getInterstitial_status(),sessionTokenResponseModel.getMopub_interstitial_id(),"3936806750b3468fa4dff17bfe7a912a",sessionTokenResponseModel.getMopub_interstitial_status());

                            getUserSubscriptions();

                        }, throwable -> {
                            // Log.w("KKK###","session");
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void getUserSubscriptions() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel ->{
                    if (subscriptionResponseModel.isForcibleLogout()) {
                        isFromSubsc = true;
                        loginExceededAlertSubscription();
                    }
                    else {
                        List<String> subids = new ArrayList<>();
                        if(subscriptionResponseModel.getData().size() != 0){
                            userSubscriptionModelList = subscriptionResponseModel.getData();
                            if(userSubscriptionModelList.size() != 0){
                                for(UserSubscriptionModel model : userSubscriptionModelList){
                                    subids.add(model.getSub_id());
                                }

                            }
                            getExpiryDate();
                        }else{
                            displayErrorMessage(getString(R.string.no_subs_error));
                        }
                        HappiApplication.setSub_id(subids);
                    }

                    }, throwable -> {
                    displayErrorMessage(getString(R.string.try_again));
                    //Toast.makeText(PremiumActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }
    private void loginExceededAlertSubscription() {
       /* if (dialog.isShowing()) {
            dialog.dismiss();
        }*/
        LogoutAlertDialog alertDialog = new LogoutAlertDialog(HappiApplication.getCurrentActivity(), this);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    @Override
    public void onLogoutClicked() {
        if (isFromSubsc) {
            // logoutApiCall();
        }
    }

    @Override
    public void onLogoutAllClicked() {
        //  logoutAllApiCall();
    }
    private void displayErrorMessage(String message) {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        rv_subscription.setVisibility(View.GONE);
        tv_error.setText(message);
        tv_error.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
    }

    public void goToHome() {
        Intent intentH = new Intent(PremiumActivity.this, HomeActivity.class);
        startActivity(intentH);

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
        //goToHome();
        super.onBackPressed();
        finish();    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isItemClicked = false;
        sku = "empty";
        packageName= "empty";
        safelyDispose(compositeDisposable);
    }

    @Override
    public void onItemClicked(int adapterPosition) {

         uModel =  userSubscriptionModelList.get(adapterPosition);
        premiumItemAdapter.setSelected(adapterPosition);
        premiumItemAdapter.notifyDataSetChanged();

        isItemClicked = true;
        sku = "empty";
        packageName= "empty";
        if(uModel.getMode_of_payment() !=null && uModel.getMode_of_payment().equals("android-in-app")){
            sku = uModel.getSubscription_type_name();
        }else{
            sku = "empty";
            packageName= "empty";
        }
    }

    private void showAlert(String messageText){
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(this,"ok",messageText,"Ok","",null,null,this::onOkClickNeutral,null);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
    @Override
    public void onOkClickNeutral() {

    }
}
