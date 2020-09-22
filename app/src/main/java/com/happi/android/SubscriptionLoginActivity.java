package com.happi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionLoginActivity extends BaseActivity {


    private EditText et_email, et_password;


    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;
    private String from = "empty";

    public static boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

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
        setContentView(R.layout.activity_subscription_login);

        LinearLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);


        Intent intentP = getIntent();
        // from = intentP.getStringExtra("from");
        if (intentP.getStringExtra("from") != null) {
            from = intentP.getStringExtra("from");
        }


        compositeDisposable = new CompositeDisposable();

        dialog = new ProgressDialog(SubscriptionLoginActivity.this);
        dialog.setMessage("Please wait.");

        Button btLogin = findViewById(R.id.bt_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        TypefacedTextViewRegular tv_forgot_password = findViewById(R.id.tv_forgot_password);

        et_email.setError(null);
        et_password.setError(null);


        btLogin.setOnClickListener(view -> {

            if (et_email.getText().toString().trim().isEmpty()) {

                et_email.setError("Please enter your email Address");
                et_email.requestFocus();
            } else if (!isValidEmail(et_email.getText().toString().trim())) {

                et_email.setError("Invalid Email");
                et_email.requestFocus();
            } else if (et_password.getText().toString().trim().isEmpty()) {

                et_password.setError("Please enter your password");
                et_password.requestFocus();
            } else {

                hideSoftKeyBoard();

                String emailAddress = et_email.getText().toString().trim().toLowerCase();
                loginApiCall(emailAddress, et_password.getText().toString().trim());

            }
        });

        tv_forgot_password.setOnClickListener(view -> {

            Intent intent = new Intent(SubscriptionLoginActivity.this, SubscriptionForgotPasswordActivity.class);
            intent.putExtra("from", "loginActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goToHomePage() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(SubscriptionLoginActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }


    @Override
    protected void onResume() {

        // TODO Auto-generated method stub
        super.onResume();
        HappiApplication.setCurrentContext(this);
        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
    }


    //login API function
    private void loginApiCall(final String email, final String password) {

        dialog.show();

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.login(email,
                password, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), email,
                                password, "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);
                        getSessionToken();


                    } else if (loginResponseModel.getStatus() == 102) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        et_email.setError("Invalid Credentials");
                        et_email.requestFocus();
                    } else if (loginResponseModel.getStatus() == 500) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(SubscriptionLoginActivity.this, "Internal Server Error", Toast
                                .LENGTH_SHORT).show();
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(SubscriptionLoginActivity.this, "Server Error", Toast
                                .LENGTH_SHORT).show();
                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);
    }

    private void getSessionToken() {


        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAppKey()
                        , SharedPreferenceUtility.getBundleID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {


                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());                       //     FEApplication.setPublisher_id(sessionTokenResponseModel.getPublisher_id());

                            checkSubscription();


                        }, throwable -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(SubscriptionLoginActivity.this, "Some error occured. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void updateReceipt(String purchaseToken, String productId) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable receiptDisposable = usersService.UpdateReceipt(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), purchaseToken,
                SharedPreferenceUtility.getPublisher_id(), productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(receiptResponseModel -> {

                }, throwable -> {

                });
        compositeDisposable.add(receiptDisposable);
    }

    private void checkSubscription() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel -> {
                    List<String> subids = new ArrayList<>();
                    if (subscriptionResponseModel.getData().size() != 0) {
                        List<UserSubscriptionModel> subscriptionModelList = subscriptionResponseModel.getData();
                        for (UserSubscriptionModel item : subscriptionModelList) {
                            subids.add(item.getSub_id());
                        }
                    }
                    HappiApplication.setSub_id(subids);
                    if (!SharedPreferenceUtility.getGuest()) {
                        if (from != null && from.equals("videoPlayer")) {

                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SubscriptionActivity.currentActivity.finish();
                            goToVideoPlayerScreen();

                        } else if (from != null && from.equals("channelPlayer")) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            SubscriptionActivity.currentActivity.finish();
                            goToChannelPlayerScreen();

                        } else if (from != null && from.equalsIgnoreCase("showDetails")) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            ShowDetailsActivity.currentActivity.finish();
                            goToShowDetailsScreen();

                        } else {
                            HappiApplication.setIsNewLoginFromPremiumPage(false);
                            goToHomePage();
                        }
                    } else {
                        //is guest
                        goToHomePage();
                    }

                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyBoard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
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
    protected void onDestroy() {
        super.onDestroy();
        safelyDispose(compositeDisposable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private void goToVideoPlayerScreen(){
        Intent intent = new Intent(SubscriptionLoginActivity.this, VideoPlayerActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToChannelPlayerScreen(){
        //Intent intent = new Intent(SubscriptionLoginActivity.this, ChannelHomeActivity.class);
        Intent intent = new Intent(SubscriptionLoginActivity.this, ChannelLivePlayerActivity.class);
        startActivity(intent);
        finish();
    }
    private void goToShowDetailsScreen(){
        Intent showIntent = new Intent(SubscriptionLoginActivity.this, ShowDetailsActivity.class);
        startActivity(showIntent);
        finish();
    }
}


