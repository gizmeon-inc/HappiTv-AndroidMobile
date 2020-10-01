package com.happi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.AlertDialogRegisterScreen;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.otpView.OtpView;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionLoginActivity extends BaseActivity implements LogoutAlertDialog.onLogoutClickListener {


    private EditText et_email, et_password;


    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;
    private String from = "empty";
    //otp
    CountDownTimer otpTimer;
    public int counterdown;
    public int minute;
    String downTimer = "";
    String minuteTimer = "";
    Button btLogin;
    RelativeLayout rl_otp_screen;
    RelativeLayout rl_otp_verification_screen;
    TextView tv_timer;
    TextView tv_resend_otp;
    TextView tv_done;
    private OtpView otpView;
    TextView tv_verfication_code_number;
    ImageView iv_back_to_page;
    LinearLayout ll_resend;
    boolean isOtpScreenOpen = false;
    private int user_id = 0;

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

        HappiApplication.setCurrentContext(this);
        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }
        if (HappiApplication.getIpAddress().isEmpty()) {
            getNetworkIP();
        }

        LinearLayout ll_signup = findViewById(R.id.ll_signup);
        TypefacedTextViewRegular tv_signup = findViewById(R.id.tv_signup);


        if(SharedPreferenceUtility.isSubscription_mandatory_flag()){
            ll_signup.setVisibility(View.INVISIBLE);
        }else{
            ll_signup.setVisibility(View.VISIBLE);
        }


        RelativeLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);


        Intent intentP = getIntent();
        // from = intentP.getStringExtra("from");
        if (intentP.getStringExtra("from") != null) {
            from = intentP.getStringExtra("from");
        }


        compositeDisposable = new CompositeDisposable();

        dialog = new ProgressDialog(SubscriptionLoginActivity.this);
        dialog.setMessage("Please wait.");

        btLogin = findViewById(R.id.bt_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        TypefacedTextViewRegular tv_forgot_password = findViewById(R.id.tv_forgot_password);

        et_email.setError(null);
        et_password.setError(null);

        //otp
        rl_otp_screen = findViewById(R.id.rl_otp_screen);
        tv_done = findViewById(R.id.tv_done);
        otpView = findViewById(R.id.otp_view);
        iv_back_to_page = findViewById(R.id.iv_back_to_page);
        rl_otp_verification_screen = findViewById(R.id.rl_otp_verification_screen);
        tv_verfication_code_number = findViewById(R.id.tv_verfication_code_number);
        tv_timer = findViewById(R.id.tv_timer);
        tv_resend_otp = findViewById(R.id.tv_resend_otp);
        tv_resend_otp.setVisibility(View.INVISIBLE);
        ll_resend = findViewById(R.id.ll_resend);

        tv_signup.setOnClickListener(v -> {

            goToRegister();
        });
        iv_back_to_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (otpView.getText() != null && !otpView.getText().toString().trim().isEmpty()) {
                    if (AppUtils.isOnline()) {
                        dialog.show();
                        hideSoftKeyBoard();
                        verifyOtpFromEmailApiCall(otpView.getText().toString().trim());
                    } else {
                        otpView.setEnabled(true);
                    }

                } else {
                    otpView.requestFocus();
                    otpView.setError("Please enter OTP");
                    otpView.setFocusable(true);

                }

            }
        });
        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
                tv_resend_otp.setEnabled(false);
                // ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline_grey));
                Drawable resendDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_outline_grey, null);
                ll_resend.setBackground(resendDrawable);
                tv_resend_otp.setTextColor(getResources().getColor(R.color.coolGrey));
            }
        });

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
    //resend otp
    private void resendOtp(){
        setTimer();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable otpResendDisposable = usersService.resendOtp(user_id, SharedPreferenceUtility.getAdvertisingId(),
                HappiApplication.getIpAddress(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(basicResponse -> {
                    if (basicResponse != null && (basicResponse.getMessage() != null && !basicResponse.getMessage().isEmpty())) {

                        alert(basicResponse.getMessage());
                    }

                },throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(otpResendDisposable);
    }
    private void setTimer() {

        counterdown = 30;
        minute = 0;
        minuteTimer = "";
        downTimer = "";

        otpTimer = new CountDownTimer(32000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_resend_otp.setEnabled(false);
                minuteTimer = " " + "0" + String.valueOf(minute);
                if (counterdown > 0) {
                    if (counterdown < 10) {
                        downTimer = "0" + String.valueOf(counterdown);
                    } else {
                        downTimer = String.valueOf(counterdown);
                    }
                } else {
                    downTimer = "00";
                }
                tv_timer.setText(minuteTimer + ":" + downTimer);
                if (minute > 0) {
                    --minute;
                }

                counterdown--;
            }

            public void onFinish() {
                tv_timer.setText(" 00:00");
                tv_resend_otp.setEnabled(true);
                tv_resend_otp.setTextColor(getResources().getColor(R.color.white));
                //ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline));
                Drawable resendDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_outline, null);
                ll_resend.setBackground(resendDrawable);
                tv_resend_otp.setClickable(true);
            }
        }.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goToHomePage() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
       // Intent intent = new Intent(SubscriptionLoginActivity.this, HomeActivity.class);
        Intent intent = new Intent(SubscriptionLoginActivity.this, MainHomeActivity.class);
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

                        getSessionToken();


                    } else if (loginResponseModel.getStatus() == 101) {

                        user_id = loginResponseModel.getData().get(0).getUser_id();
                        showOtpVerificationPage();

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
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(SubscriptionLoginActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);
    }

    void alert(String message) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        AlertDialogRegisterScreen exitAppDialogClass =
                new AlertDialogRegisterScreen(this, message);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitAppDialogClass.show();
    }

    private void showOtpVerificationPage() {
        isOtpScreenOpen = true;
        btLogin.setEnabled(false);
        et_email.setEnabled(false);
        et_password.setEnabled(false);


        if ((dialog.isShowing())) {
            dialog.dismiss();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_otp_screen.getLayoutParams();
        rl_otp_screen.setLayoutParams(params);
        rl_otp_verification_screen.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        tv_verfication_code_number.setText(getText(R.string.please_type_verf_code) + " " + et_email.getText().toString().trim().toLowerCase());
        rl_otp_verification_screen.setVisibility(View.VISIBLE);
        otpView.setText("");
        otpView.requestFocus();
        otpView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_DONE
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //  Log.d("&&&&","enter pressed");
                    tv_done.performClick();
                }
                return false;
            }
        });

    }

    //verify otp sent to mail
    private void verifyOtpFromEmailApiCall(String otp) {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable otpDisposable = usersService.verifyOtpFromEmail(user_id, SharedPreferenceUtility.getPublisher_id(), otp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {


                   /* status:
                    0 - otp incorrect
                    1 - otp verified ( response will have user data)*/
                    if (loginResponseModel.getStatus() == 1) {
                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), loginResponseModel.getData().get(0).getUser_email(),
                                et_password.getText().toString().trim(), "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);

                        if (isOtpScreenOpen) {
                            isOtpScreenOpen = false;
                            rl_otp_verification_screen.setVisibility(View.GONE);

                            btLogin.setEnabled(true);
                            et_email.setEnabled(true);
                            et_password.setEnabled(true);
                        }
                        getSessionToken();


                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        otpView.setText("");
                        if (loginResponseModel.getMessage() != null && (!loginResponseModel.getMessage().isEmpty())) {
                            alert(loginResponseModel.getMessage());
                        }
                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(otpDisposable);
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
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());                       //     HappiApplication.setPublisher_id(sessionTokenResponseModel.getPublisher_id());

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
                    if (subscriptionResponseModel.isForcibleLogout()) {
                        loginExceededAlertSubscription();
                    } else {
                        List<String> subids = new ArrayList<>();
                        if (subscriptionResponseModel.getData().size() != 0) {
                            List<UserSubscriptionModel> subscriptionModelList = subscriptionResponseModel.getData();
                            for (UserSubscriptionModel item : subscriptionModelList) {
                                subids.add(item.getSub_id());
                            }
                        }
                        HappiApplication.setSub_id(subids);
                        if (!SharedPreferenceUtility.getGuest()) {
                            if (from != null && from.equalsIgnoreCase("videoplayer")) {

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                goToVideoPlayerScreen();

                            }else if (from != null && from.equalsIgnoreCase("videodetails")) {

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                SubscriptionActivity.currentActivity.finish();
                                goToVideoPlayerScreen();

                            } else if (from != null && from.equalsIgnoreCase("channelplayer")) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                goToChannelPlayerScreen();

                            }else if (from != null && from.equalsIgnoreCase("channeldetails")) {
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
                    }
                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionLoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }

    private void loginExceededAlertSubscription() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LogoutAlertDialog alertDialog =
                new LogoutAlertDialog(HappiApplication.getCurrentActivity(), this);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onLogoutClicked() {
        logoutApiCall();
    }

    @Override
    public void onLogoutAllClicked() {
        logoutAllApiCall();
    }

    private void logoutApiCall() {
        dialog.show();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable logoutDisposable = usersService.logout(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getPublisher_id(),
                SharedPreferenceUtility.getAdvertisingId(), HappiApplication.getIpAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutResponseModel -> {

                    if (logoutResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "", "", false, "");
                        SharedPreferenceUtility.setGuest(false);
                        SharedPreferenceUtility.setIsFirstTimeInstall(false);
                        SharedPreferenceUtility.setChannelId(0);
                        SharedPreferenceUtility.setShowId("0");
                        SharedPreferenceUtility.setVideoId(0);
                        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
                        SharedPreferenceUtility.setChannelTimeZone("");
                        SharedPreferenceUtility.setSession_Id("");
                        SharedPreferenceUtility.setNotificationIds(new ArrayList<>());
                        SharedPreferenceUtility.setSubscriptionItemIdList(new ArrayList<>());

                        HappiApplication.setSub_id(new ArrayList<>());

                        et_email.setText("");
                        et_password.setText("");
                        et_email.requestFocus();

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                        Log.e("Logout", "api call failed");
                    }

                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                    Log.e("Logout", "api call failed");
                });

        compositeDisposable.add(logoutDisposable);
    }

    private void logoutAllApiCall() {
        dialog.show();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable logoutDisposable = usersService.logoutAll(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getPublisher_id(),
                SharedPreferenceUtility.getAdvertisingId(), HappiApplication.getIpAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutResponseModel -> {

                    if (logoutResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "", "", false, "");
                        SharedPreferenceUtility.setGuest(false);
                        SharedPreferenceUtility.setIsFirstTimeInstall(false);
                        SharedPreferenceUtility.setChannelId(0);
                        SharedPreferenceUtility.setShowId("0");
                        SharedPreferenceUtility.setVideoId(0);
                        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
                        SharedPreferenceUtility.setChannelTimeZone("");
                        SharedPreferenceUtility.setSession_Id("");
                        SharedPreferenceUtility.setNotificationIds(new ArrayList<>());
                        SharedPreferenceUtility.setSubscriptionItemIdList(new ArrayList<>());

                        HappiApplication.setSub_id(new ArrayList<>());

                        et_email.setText("");
                        et_password.setText("");
                        et_email.requestFocus();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                        Log.e("Logout", "api call failed");
                    }

                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "Unable to logout. Please try again", Toast.LENGTH_SHORT).show();
                    Log.e("Logout", "api call failed");
                });

        compositeDisposable.add(logoutDisposable);
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

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            if (imm.isAcceptingText()) { // verify if the soft keyboard is open
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                // imm.hideSoftInputFromWindow(btLogin.getWindowToken(), 0);
            }
        } catch (Exception ex) {
            Log.e("Exception", " " + ex.getMessage());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        safelyDispose(compositeDisposable);
    }

    @Override
    public void onBackPressed() {
        if (isOtpScreenOpen) {
            isOtpScreenOpen = false;
            rl_otp_verification_screen.setVisibility(View.GONE);

            btLogin.setEnabled(true);
            et_email.setEnabled(true);
            et_password.setEnabled(true);
        } else {
            super.onBackPressed();
            finish();
        }
    }


    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private void goToVideoPlayerScreen() {
        Intent intent = new Intent(SubscriptionLoginActivity.this, VideoPlayerActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToChannelPlayerScreen() {
        //Intent intent = new Intent(SubscriptionLoginActivity.this, ChannelHomeActivity.class);
        Intent intent = new Intent(SubscriptionLoginActivity.this, ChannelLivePlayerActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToShowDetailsScreen() {
        Intent showIntent = new Intent(SubscriptionLoginActivity.this, ShowDetailsActivity.class);
        startActivity(showIntent);
        finish();
    }

    private void getNetworkIP() {
        boolean isMobileData = false;
        boolean isWifi = false;
        String ipAddress = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (networkInfo.isConnected()) {
                    isWifi = true;
                } else {
                    isWifi = false;
                }
            }

            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (networkInfo.isConnected()) {
                    isMobileData = true;
                } else {
                    isMobileData = false;
                }

            }
        }

        if (isWifi) {
            ipAddress = getWifiIpAddress();
            HappiApplication.setIpAddress(ipAddress);
        }
        if (isMobileData) {
            ipAddress = getMobileIpAddress();
            HappiApplication.setIpAddress(ipAddress);
        }
    }

    private String getWifiIpAddress() {
        @SuppressWarnings("deprecation")
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    private String getMobileIpAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumipAddr = networkInterface.getInetAddresses(); enumipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        // if(inetAddress instanceof Inet4Address)
                        return Formatter.formatIpAddress(inetAddress.hashCode());

                    }

                }
            }

        } catch (Exception ex) {
            Log.e("1234###", "exception: " + ex.toString());
        }
        return null;
    }


    private void goToRegister() {

        Intent intent = new Intent(SubscriptionLoginActivity.this, SubscriptionRegisterActivity.class);
        if(getIntent().getSerializableExtra("from") != null){
            intent.putExtra("from",getIntent().getSerializableExtra("from"));
        }
        startActivity(intent);
        finish();
    }
}


