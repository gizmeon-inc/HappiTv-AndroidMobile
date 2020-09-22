package com.happi.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.LoginRegisterAlert;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.NumberRegistrationAlert;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.PhoneVerificationModel;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.otpView.OtpView;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class SubscriptionActivity extends BaseActivity implements LogoutAlertDialog.onLogoutClickListener,CustomAlertDialog.OnOkClick, LoginRegisterAlert.OnLoginRegisterUserPositive,
        LoginRegisterAlert.OnLoginRegisterUserNeutral, LoginRegisterAlert.OnLoginRegisterUserNegative,
        NumberRegistrationAlert.OnNumberRegisterUserPositive, NumberRegistrationAlert.OnNumberRegisterUserNegative{


    public static Activity currentActivity;

    private FrameLayout fl_container_for_icon;
    private RelativeLayout rl_web_view;
    private WebView wv_subscription;
    private CompositeDisposable compositeDisposable;
    private String token;
    private ProgressDialog progressDialog;

    //phone number register layout
    private ImageView iv_close;
    private CountryCodePicker ccp_picker;
    private RelativeLayout rl_bottom_sheet;
    private RelativeLayout rl_btm;
    private EditText et_phone_number;
    private String countryCode = "";
    private String phoneNumber = "";
    private FirebaseAuth mAuth = null;
    private String mVerificationId = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken = null;
    private RelativeLayout rl_otp_screen;
    private RelativeLayout rl_otp_verification_screen;
    private TextView tv_timer;
    private TextView tv_resend_otp;
    private TextView tv_done;
    public int counterdown;
    public int minute;
    private String downTimer = "";
    private String minuteTimer = "";
    private OtpView otpView;
    private TextView tv_verfication_code_number;
    private ImageView iv_back_to_page;
    private LinearLayout ll_resend;
    boolean isOtpScreenOpen = false;
    private CountDownTimer otpTimer;
    private TextView tv_error;
    private long channel = 0;
    private long video = 0;
    private boolean isFromSubsc = false;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {


            } else {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            alert(e.getMessage(), false);
            Log.d("FirebaseException", e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            super.onCodeSent(s, forceResendingToken);
            showOtpVerificationPage();
            //storing the verification id that is sent to the user
            if (s != null) {
                mVerificationId = s;
                mResendToken = forceResendingToken;
            }
        }
    };

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
        setContentView(R.layout.activity_subscription);

        HappiApplication.setCurrentContext(this);
        currentActivity = this;

        progressDialog = new ProgressDialog(SubscriptionActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        compositeDisposable = new CompositeDisposable();
        token = "";
        fl_container_for_icon = findViewById(R.id.fl_container_for_icon);
        rl_web_view = findViewById(R.id.rl_web_view);
        wv_subscription = findViewById(R.id.wv_subscription);

        fl_container_for_icon.setVisibility(View.VISIBLE);
        rl_web_view.setVisibility(GONE);

        TypefacedTextViewRegular tv_title = findViewById(R.id.tv_title);
        tv_title.setVisibility(View.GONE);

        if(getIntent() != null &&
                (getIntent().getStringExtra("from") != null && !getIntent().getStringExtra("from").isEmpty())){
            String from  = getIntent().getStringExtra("from");
            if(from.equalsIgnoreCase("videoPlayer")){
                channel = 0;
                video = SharedPreferenceUtility.getVideoId();
            }else if(from.equalsIgnoreCase("channelPlayer")){
                //channel = "1";
                video = 0;
                channel = SharedPreferenceUtility.getChannelId();
            }else{
                channel = 0;
                video = SharedPreferenceUtility.getVideoId();
            }
        }else{
            channel = 0;
            video = SharedPreferenceUtility.getVideoId();
        }

        isFromSubsc = false;
        //number registration
        mAuth = FirebaseAuth.getInstance();
        rl_bottom_sheet = findViewById(R.id.rl_bottom_sheet);
        rl_btm = findViewById(R.id.rl_btm);
        rl_bottom_sheet.setVisibility(View.GONE);
        //otp
        rl_otp_screen = findViewById(R.id.rl_otp_screen);
        tv_done = findViewById(R.id.tv_done);
        otpView = findViewById(R.id.otp_view);
        iv_back_to_page = findViewById(R.id.iv_back_to_page);
        rl_otp_verification_screen = findViewById(R.id.rl_otp_verification_screen);
        tv_verfication_code_number = findViewById(R.id.tv_verfication_code_number);
        tv_timer = findViewById(R.id.tv_timer);
        tv_resend_otp = findViewById(R.id.tv_resend_otp);
        ll_resend = findViewById(R.id.ll_resend);

        et_phone_number = findViewById(R.id.et_phone_number);
        ccp_picker = findViewById(R.id.ccp_picker);
        iv_close = findViewById(R.id.iv_close);
        Button btn_continue = findViewById(R.id.btn_continue);

        tv_error = findViewById(R.id.tv_error);

        //toolbar
        ImageView iv_menu = findViewById(R.id.iv_menu);
        ImageView iv_logo_text = findViewById(R.id.iv_logo_text);
        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView iv_search = findViewById(R.id.iv_search);

        tv_error.setVisibility(View.GONE);
        iv_menu.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.GONE);
        iv_logo_text.setVisibility(View.VISIBLE);



        et_phone_number.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_btm.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                rl_btm.setLayoutParams(params);
                rl_bottom_sheet.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                et_phone_number.requestFocus();
                showSoftKeyboard();
                rl_bottom_sheet.setVisibility(View.VISIBLE);
                return false;
            }
        });
        et_phone_number.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here

                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_error.setVisibility(View.GONE);

            }
        });
        et_phone_number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            btn_continue.performClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (et_phone_number.getText().toString().length() == 0) {
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                    tv_error.setText(R.string.enter_number_error);
                    tv_error.setVisibility(View.VISIBLE);
                } else if (et_phone_number.getText().toString().length() > 15) {
                    et_phone_number.setError("Invalid phone number");
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                    tv_error.setVisibility(View.INVISIBLE);
                } else {
                    countryCode = "+" + ccp_picker.getSelectedCountryCode();
                    phoneNumber = countryCode + et_phone_number.getText().toString().trim();

                    Log.d("SubscriptionActivity", "Phone : " + phoneNumber);
                    progressDialog.show();
                    sendVerificationCode(et_phone_number.getText().toString().trim(), countryCode);


                }

            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyBoard();
                rl_bottom_sheet.setVisibility(View.GONE);
                checkIfNumberIsVerified();
            }
        });
        iv_back_to_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_otp_verification_screen.setVisibility(View.GONE);
                if (otpTimer != null) {
                    otpTimer.cancel();
                }
                checkIfNumberIsVerified();
            }
        });
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(PremiumVideoDetailsActivity.this, "Done", Toast.LENGTH_LONG).show();

                if (!otpView.getText().toString().isEmpty()) {


                    if (AppUtils.isOnline()) {

                        String otpEntered = otpView.getText().toString();
                        otpView.setText("");
                        if (otpEntered != null) {
                            progressDialog.show();
                            // tv_timer.setText(" 00:00");
                            verifyVerificationCode(otpEntered);
                        }
                        hideSoftKeyBoard();
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
        otpView.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tv_done.performClick();
            }
            return false;
        });
        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_resend_otp.setEnabled(false);
                //ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline_grey));
                ll_resend.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_outline_grey, null));
                tv_resend_otp.setTextColor(getResources().getColor(R.color.coolGrey));

                resendVerificationCode(et_phone_number.getText().toString().trim(), countryCode);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscriptionActivity.super.onBackPressed();
                finish();

            }
        });

        if (SharedPreferenceUtility.getGuest()) {
            showLoginOrRegisterAlert();
        } else {

            if (HappiApplication.getAppToken() != null && !HappiApplication.getAppToken().isEmpty()) {
                checkIfNumberIsVerified();
            } else {
                getSessionToken(true);
            }
        }


    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the usermAuth
        signInWithPhoneAuthCredential(credential);
    }

    private void showOtpVerificationPage() {
       if(progressDialog.isShowing()){
           progressDialog.dismiss();
       }

        isOtpScreenOpen = true;
        rl_bottom_sheet.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_otp_screen.getLayoutParams();
        rl_otp_screen.setLayoutParams(params);
        rl_otp_verification_screen.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        setTimer();
        String verificationText = getText(R.string.please_type_verf_code) + " " + phoneNumber;
        tv_verfication_code_number.setText(verificationText);
        rl_otp_verification_screen.setVisibility(View.VISIBLE);
        otpView.setText("");
        otpView.requestFocus();


    }


    private void setTimer() {

        tv_resend_otp.setEnabled(false);
        //ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline_grey));
        ll_resend.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_outline_grey, null));
        tv_resend_otp.setTextColor(getResources().getColor(R.color.coolGrey));


        counterdown = 60;
        minute = 0;
        minuteTimer = "";
        downTimer = "";

        otpTimer = new CountDownTimer(62000, 1000) {
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
                String timerText = minuteTimer + ":" + downTimer;
                tv_timer.setText(timerText);
                if (minute > 0) {
                    --minute;
                }

                counterdown--;
            }

            public void onFinish() {
                String finishedTimer = " 00:00";
                tv_timer.setText(finishedTimer);
                if (otpTimer != null) {
                    otpTimer.cancel();
                }
                tv_resend_otp.setEnabled(true);
                tv_resend_otp.setTextColor(getResources().getColor(R.color.colorAccent));
                ll_resend.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_outline, null));
                //ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline));
                tv_resend_otp.setClickable(true);
            }
        }.start();
    }

    public void showSoftKeyboard() {

        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideSoftKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        if (imm != null && imm.isAcceptingText() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void resendVerificationCode(String mobile, String countryCode) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                HappiApplication.getCurrentActivity(),
                mCallbacks,
                mResendToken
        );
    }

    private void sendVerificationCode(String mobile, String countryCode) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobile,
                60,
                TimeUnit.SECONDS,
                HappiApplication.getCurrentActivity(),
                mCallbacks
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(HappiApplication.getCurrentActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@io.reactivex.annotations.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            // verify phone number
                            verifyNumberApiCall();
                            //  checkIfNumberIsVerified();

                            Log.d("NumberSUCVER", "number is verified");

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered";
                            }

                           /* Snackbar snackbar = Snackbar.make(findViewById(R.id.ll_parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();*/

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(HappiApplication.getCurrentActivity(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyNumberApiCall() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable phoneDisposable = usersService.verifyPhoneNumber(HappiApplication.getAppToken(), SharedPreferenceUtility.getUserId(), phoneNumber, countryCode, "android-phone", SharedPreferenceUtility.getPublisher_id(), SharedPreferenceUtility.getVideoId(), SharedPreferenceUtility.getCountryCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(phoneVerificationResponseModel -> {

                    alert(phoneVerificationResponseModel.getMessage(), true);

                }, throwable -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(SubscriptionActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(phoneDisposable);
    }

    void alert(String message, boolean isOtpScrnVisible) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setCancelable(false);
        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isOtpScrnVisible) {
                    isOtpScreenOpen = false;
                    rl_bottom_sheet.setVisibility(View.GONE);
                    rl_otp_verification_screen.setVisibility(View.GONE);

                    if (HappiApplication.getAppToken() != null && !HappiApplication.getAppToken().isEmpty()) {
                        checkIfNumberIsVerified();
                    } else {
                        getSessionToken(true);
                    }
                }
                dialog.dismiss();
            }
        });
        Log.d("SubscriptionActivity", "Showing alert dialog: " + message);
        bld.create().show();
    }

    private void showLoginOrRegisterAlert() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        String message = "Please Login or Register to continue.";
        LoginRegisterAlert alertDialog =
                new LoginRegisterAlert(this, message, "Register", "Login", "Cancel", this::onLoginRegisterNegativeClick,
                        this::onLoginRegisterNeutralClick, this::onLoginRegisterPositiveClick, false);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void checkIfNumberIsVerified() {
        progressDialog.show();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable phoneVerificationDisposable = usersService.checkPhoneVerification(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), "android-phone",
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(phoneVerificationResponseModel -> {


                    if (phoneVerificationResponseModel.getData().size() != 0) {
                        if (phoneVerificationResponseModel.getData() != null) {
                            PhoneVerificationModel phoneVerificationModel = phoneVerificationResponseModel.getData().get(0);
                            if (!phoneVerificationModel.getPhone_verified_flag().equals("null")) {

                                if (phoneVerificationModel.getPhone_verified_flag().equals("1")) {
                                    //number is verified, proceed to in app payment

                                    if (HappiApplication.getAppToken() != null && !HappiApplication.getAppToken().isEmpty()) {
                                        fetchToken();
                                    } else {
                                        getSessionToken(false);
                                    }


                                } else if (phoneVerificationModel.getPhone_verified_flag().equals("0")) {

                                    showNumberRegistrationAlert();
                                }

                            }
                        }
                    }


                }, throwable -> {

                    // no response
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(SubscriptionActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(phoneVerificationDisposable);


    }

    private void showNumberRegistrationAlert() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String message = getResources().getString(R.string.register_number);
        NumberRegistrationAlert alertDialog = new NumberRegistrationAlert(this, message, "Ok", "Cancel",
                         this::onNumberRegisterNegativeClick, this::onNumberRegisterPositiveClick);

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void verifyNumber() {
        rl_btm.setVisibility(View.VISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        et_phone_number.setText("");
        tv_error.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_btm.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        rl_btm.setLayoutParams(params);
        rl_bottom_sheet.setLayoutParams(new RelativeLayout.LayoutParams(width, 350));
        rl_bottom_sheet.setVisibility(View.VISIBLE);
    }

    private void getSessionToken(boolean isNumberVerificationCheck) {
        progressDialog.show();

        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();

        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), appKey, bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {
                            Log.d("TAG_PREMIUM_VIDEO", "sample home: sessiontoken");

                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());
                            SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(), sessionTokenResponseModel.getInterstitial_id(), sessionTokenResponseModel.getApp_id(), sessionTokenResponseModel.getRewarded_status(), sessionTokenResponseModel.getInterstitial_status(), sessionTokenResponseModel.getMopub_interstitial_id(), sessionTokenResponseModel.getMopub_banner_id(), sessionTokenResponseModel.getMopub_interstitial_status(), sessionTokenResponseModel.getMopub_rect_banner_id());

                            if (isNumberVerificationCheck) {
                                checkIfNumberIsVerified();
                            } else {
                                fetchToken();
                            }

                        }, throwable -> {

                            Log.e("getSessionToken", "");
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void fetchToken() {
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", SharedPreferenceUtility.getUserId());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable = usersService.getToken(HappiApplication.getAppToken(), jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenResponse -> {
                    if (tokenResponse != null) {
                        if (tokenResponse.getStatus() != null && tokenResponse.getStatus() == 100) {
                            if (tokenResponse.getData() != null) {
                                token = tokenResponse.getData().trim();
                                loadWebView();
                            }
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(this, "Some error occurred. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, "Some error occurred. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(tokenDisposable);
    }

    private void loadWebView() {
        rl_web_view.setVisibility(View.VISIBLE);
        fl_container_for_icon.setVisibility(GONE);
        String url = ConstantUtils.SUBSCRIPTION_WEBVIEW_URL.trim() + token.trim()
                + "&vd=" + video
                + "&ch=" + channel;
        Log.v("okhttp", "MAIN: url: " + url);
        WebSettings settings = wv_subscription.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");

        wv_subscription.setWebViewClient(new FEWebViewClient());
        wv_subscription.loadUrl(url);

    }

    @Override
    public void onOkClickNeutral() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onLoginRegisterPositiveClick() {
     goToRegisterScreen();
    }

    @Override
    public void onLoginRegisterNegativeClick() {
        goToLoginScreen();
    }

    @Override
    public void onLoginRegisterNeutralClick() {
     SubscriptionActivity.super.finish();
    }


    private void goToLoginScreen(){

        Intent intent = new Intent(SubscriptionActivity.this, SubscriptionLoginActivity.class);
        if(getIntent() != null && ((getIntent().getStringExtra("from") != null) && (!getIntent().getStringExtra("from").isEmpty()))){
            intent.putExtra("from" , getIntent().getStringExtra("from"));
        }
        startActivity(intent);
    }
    private void goToRegisterScreen(){
        Intent intent = new Intent(SubscriptionActivity.this, SubscriptionRegisterActivity.class);
        if(getIntent() != null && ((getIntent().getStringExtra("from") != null) && (!getIntent().getStringExtra("from").isEmpty()))){
            intent.putExtra("from" , getIntent().getStringExtra("from"));
        }
        startActivity(intent);
    }

    @Override
    public void onNumberRegisterPositiveClick() {
        verifyNumber();
    }

    @Override
    public void onNumberRegisterNegativeClick() {
        SubscriptionActivity.super.finish();
    }

    private class FEWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v("okhttp", "FINISHED: url: " + url);
            if (url != null && !url.isEmpty()) {
                if (url.contains(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL.trim())) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url != null && !url.trim().isEmpty()) {
                if (url.trim().equalsIgnoreCase(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL_SUCCESS.trim())) {
                    progressDialog.setMessage("Processing your payment...");
                    progressDialog.show();
                    fl_container_for_icon.setVisibility(View.VISIBLE);
                    updateSubscriptionIds();
                    // Toast.makeText(CeyFlixApplication.getCurrentContext(), " onPageStarted >> Subscription is successfull.", Toast.LENGTH_SHORT).show();

                } else if (url.trim().equalsIgnoreCase(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL_FAILURE.trim())) {
                    fl_container_for_icon.setVisibility(View.VISIBLE);
                    showAlertDialog("Sorry, your Subscription has failed.");
                    // Toast.makeText(CeyFlixApplication.getCurrentContext(), " onPageStarted >> Sorry. Your Subscription has failed.", Toast.LENGTH_SHORT).show();

                }
            }
            Log.v("okhttp", "onPageStarted: url: " + url);
        }

    }

    private void showAlertDialog(String message) {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(this, "ok", message, "Ok", "", null, null, this::onOkClickNeutral, null);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void updateSubscriptionIds() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel -> {
                    if (subscriptionResponseModel.isForcibleLogout()) {
                        isFromSubsc = true;
                        loginExceededAlertSubscription();
                    }
                    else {
                        List<String> subids = new ArrayList<>();
                        if (subscriptionResponseModel.getData().size() != 0) {
                            List<UserSubscriptionModel> subscriptionModelList = subscriptionResponseModel.getData();
                            for (UserSubscriptionModel item : subscriptionModelList) {
                                subids.add(item.getSub_id());
                            }
                        }
                        HappiApplication.setSub_id(subids);
                        //////////////////////////////////IMPORTANT : uncomment for test purposes only ///////////////////////////
                   /* List<String> subscriptionModelListSample = FEApplication.getSub_id();
                    ArrayList<String> subscriptionIdListTest = SharedPreferenceUtility.getSubscriptionItemIdList();
                    if (subscriptionIdListTest.size() != 0) {
                        subscriptionModelListSample.addAll(subscriptionIdListTest);
                    }
                   FEApplication.setSub_id(subscriptionModelListSample);*/
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////

                        List<String> subscriptionIds = HappiApplication.getSub_id();
                        if (subscriptionIds.size() != 0) {
                            ArrayList<String> subscriptionIdList = SharedPreferenceUtility.getSubscriptionItemIdList();
                            if (subscriptionIdList.size() != 0) {
                                boolean hasSubscribed = false;
                                for (String idItem : subscriptionIdList) {
                                    for (String subId : subscriptionIds) {

                                        if (idItem.equals(subId)) {
                                            hasSubscribed = true;
                                            break;

                                        }
                                    }
                                }
                                if (hasSubscribed) {
                                    goToPlayer();
                                } else {
                                    goToHomeScreen();
                                }
                            }


                        } else {
                            //subIdList empty
                            //show premium details page
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            showAlertDialog("Your Subscription is being processed. Please try again after sometime.");
                        }
                    }
                }, throwable -> {
                    //hideProgressDialog();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(HappiApplication.getCurrentContext(), "Something went wrong. Please try again after sometime.", Toast.LENGTH_SHORT).show();
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
    private void goToHomeScreen() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToPlayer() {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        String from = "";
        Intent intent;
        if(getIntent() != null && ((getIntent().getStringExtra("from") != null) && (!getIntent().getStringExtra("from").isEmpty()))){
            from = getIntent().getStringExtra("from");
        }
        if(from != null && !from.isEmpty()){
            if(from.equalsIgnoreCase("videoPlayer")){
                intent = new Intent(SubscriptionActivity.this, VideoPlayerActivity.class);
            }else if(from.equalsIgnoreCase("channelPlayer")){
             //   intent = new Intent(SubscriptionActivity.this, ChannelHomeActivity.class);
                intent = new Intent(SubscriptionActivity.this, ChannelLivePlayerActivity.class);
            }else{
                intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
            }
        }else{
            intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(isOtpScreenOpen || (rl_otp_verification_screen.getVisibility() == View.VISIBLE)){
            isOtpScreenOpen = false;
            rl_bottom_sheet.setVisibility(View.GONE);
            rl_otp_verification_screen.setVisibility(View.GONE);
            if(otpTimer != null){
                otpTimer.cancel();}
            checkIfNumberIsVerified();

        }else if(rl_bottom_sheet.getVisibility() == View.VISIBLE){
                iv_close.performClick();
        }else{
            super.onBackPressed();
            finish();
        }
    }
}