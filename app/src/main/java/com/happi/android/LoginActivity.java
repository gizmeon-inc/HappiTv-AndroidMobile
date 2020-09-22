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
import android.widget.TextView;
import android.widget.Toast;

import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity  {

    private EditText et_email, et_password;
    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;

    private Boolean isGuest = false;

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
        setContentView(R.layout.activity_login);
        compositeDisposable = new CompositeDisposable();

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Please wait.");

        HappiApplication.setCurrentContext(this);
        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }
        LinearLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);

        LinearLayout ll_signup = findViewById(R.id.ll_signup);
        TextView tv_signup = findViewById(R.id.tv_signup);
        TextView tv_skip = findViewById(R.id.tv_skip);
        TextView new_user = findViewById(R.id.new_user);
        new_user.setVisibility(View.VISIBLE);
        tv_signup.setVisibility(View.VISIBLE);
        tv_skip.setVisibility(View.VISIBLE);

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

        ll_signup.setOnClickListener(view -> {

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        tv_forgot_password.setOnClickListener(view -> {

            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        tv_skip.setOnClickListener(view -> {

            skipLogin();
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
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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

        String versionName = BuildConfig.VERSION_NAME;
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.newLogin(email,
                password, SharedPreferenceUtility.getPublisher_id(), SharedPreferenceUtility.getAdvertisingId(),
                HappiApplication.getIpAddress(), versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), email,
                                password, "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);
                        isGuest = false;
                        getSessionToken();

                    }
                    /*else if (loginResponseModel.getStatus() == 101) {

                        user_id =loginResponseModel.getData().get(0).getUser_id();
                        showOtpVerificationPage();

                    }*/else if (loginResponseModel.getStatus() == 102) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        et_email.setError("Invalid Credentials");
                        et_email.requestFocus();
                    } else if (loginResponseModel.getStatus() == 500) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, "Internal Server Error", Toast
                                .LENGTH_SHORT).show();
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, "Server Error", Toast
                                .LENGTH_SHORT).show();
                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

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

                                if (!isGuest) {
                                    HappiApplication.setIsNewLoginFromPremiumPage(false);
                                    //not from premium
                                    goToHomePage();
                                } else {
                                    goToHomePage();
                                }


                        }, throwable -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void skipLogin() {

        SharedPreferenceUtility.setGuest(true);

        dialog.show();

        String deviceId = SharedPreferenceUtility.getAdvertisingId();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_id", deviceId);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());
        jsonObject.addProperty("latitude", HappiApplication.getLatitude());
        jsonObject.addProperty("longitude", HappiApplication.getLongitude());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable disposable = usersService.GuestRegister(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 1) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), "Guest", "", "", "", "",
                                "", "", true, "");
                        isGuest = true;
                        SharedPreferenceUtility.setGuest(true);

                        getSessionToken();

                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, getString(R.string.server_error),
                            Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(disposable);

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
        // super.onBackPressed();
        finish();
        finishAffinity();
        System.exit(0);

    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }


}

