package com.happi.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.otpView.OtpView;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RegisterActivity extends BaseActivity {

    private EditText et_name;
    private EditText et_email;
    private EditText et_password;
    private EditText et_phone_number;
    private Button bt_signup;
    private TextView tv_login;
    private String deviceId = "";
    private String verified = "0";
    private String c_code = "";
    private String countryCode = "";
    private CountryCodePicker ccp_picker;
    private PhoneAuthProvider.ForceResendingToken mResendToken = null;

    private FirebaseAuth mAuth = null;
    private String mVerificationId = "";
    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;

    //otp
    RelativeLayout rl_otp_screen;
    RelativeLayout rl_otp_verification_screen;
    TextView tv_timer;
    TextView tv_resend_otp;
    TextView tv_done;
    public int counterdown;
    public int minute;
    String downTimer = "";
    String minuteTimer = "";
    private OtpView otpView;
    TextView tv_verfication_code_number;
    ImageView iv_back_to_page;
    LinearLayout ll_resend;
    LinearLayout ll_main_page;
    boolean isOtpScreenOpen = false;
    CountDownTimer otpTimer;
    private int user_id = 0;
    private String email = "";
    private String password = "";
    private String fbId = "";


    public static boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_register);
        RelativeLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);

        HappiApplication.setCurrentContext(this);
        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }

        isOtpScreenOpen = false;
        compositeDisposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        et_phone_number = findViewById(R.id.et_phone_number);
        ccp_picker = findViewById(R.id.ccp_picker);

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage("Please wait.");

        tv_login = findViewById(R.id.tv_login);
        TextView tv_alreadyAccount = findViewById(R.id.tv_alreadyAccount);
        tv_login.setVisibility(View.VISIBLE);
        tv_alreadyAccount.setVisibility(View.VISIBLE);


        bt_signup = findViewById(R.id.bt_signup);

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
        ll_main_page = findViewById(R.id.ll_main_page);

        et_name.setError(null);
        et_email.setError(null);
        et_password.setError(null);


        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("from", "register");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (et_name.getText().toString().trim().isEmpty()) {
                    et_name.setError("Please enter your Name");
                    et_name.setFocusable(true);
                    et_name.requestFocus();
                } else if (et_email.getText().toString().trim().isEmpty()) {
                    et_email.setError("Please enter your E-mail address");
                    et_email.setFocusable(true);
                    et_email.requestFocus();
                } else if (!isValidEmail(et_email.getText().toString().trim())) {
                    et_email.setError("Please enter a valid E-mail address");
                    et_email.setFocusable(true);
                    et_email.requestFocus();
                } else if (et_password.getText().toString().trim().isEmpty()) {
                    et_password.setError("Please enter your password");
                    et_password.setFocusable(true);
                    et_password.requestFocus();
                } else if (et_password.getText().toString().trim().length() < 6) {
                    et_password.setError("Minimum 6 letters required.");
                    et_password.setFocusable(true);
                    et_password.requestFocus();
                } else if (et_phone_number.getText().toString().trim().isEmpty()) {
                    et_phone_number.setError("Please enter phone number");
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                }else if (et_phone_number.getText().toString().trim().length() > 15) {
                    et_phone_number.setError("Invalid phone number");
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                } else {
                    deviceId = SharedPreferenceUtility.getAdvertisingId();
                    dialog.show();
                    hideSoftKeyBoard();
                    countryCode = "+" + ccp_picker.getSelectedCountryCode();
                  //  sendVerificationCode(et_phone_number.getText().toString().trim(), countryCode);

                    verified = "1";
                    c_code = countryCode;
                    String emailInLowerCase = et_email.getText().toString().trim().toLowerCase();
                    registerWithEmailApiCall(emailInLowerCase, et_password.getText().toString().trim(), et_name.getText().toString().trim(), "", c_code + et_phone_number.getText().toString().trim(), SharedPreferenceUtility.getAdvertisingId(), "android-phone", "gmail-login", "0", verified, c_code);


                }
            }
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
/*
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
*/
        tv_resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* resendVerificationCode(et_phone_number.getText().toString().trim(), countryCode);
                tv_resend_otp.setEnabled(false);
                ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline_grey));
                tv_resend_otp.setTextColor(getResources().getColor(R.color.coolGrey));*/
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

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                   /* status:
                    0 - otp incorrect
                    1 - otp verified ( response will have user data)*/
                    if (loginResponseModel.getStatus() == 1) {
                        if (loginResponseModel.getData() != null && !loginResponseModel.getData().isEmpty()) {
                            SharedPreferenceUtility.setGuest(false);

                            SharedPreferenceUtility.saveUserDetails(
                                    loginResponseModel.getData().get(0).getUser_id(), loginResponseModel.getData().get(0).getUser_name(), email,
                                    password, "", "", "", fbId, false, c_code + et_phone_number.getText().toString().trim());

                            isOtpScreenOpen = false;
                          //  SharedPreferenceUtility.setLanguage("");

                            //handling 3 cases
                            hideSoftKeyBoard();
                          //  goToSubscriptionPage();
                            goToHome();
                         //   verified = "1";
                          //  c_code = countryCode;
                          //  String phoneNo = c_code + et_phone_number.getText().toString().trim();

                          //  String emailAddress = et_email.getText().toString().trim().toLowerCase();
                         //   registerApiCall(emailAddress, et_password.getText().toString().trim(), et_name.getText().toString().trim(), "", phoneNo, deviceId, "android-phone", "gmail-login", "0", verified, c_code);


                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            if(loginResponseModel.getMessage() != null && (!loginResponseModel.getMessage().isEmpty())){
                                alert(loginResponseModel.getMessage());
                            }
                        }


                    } else {
                        otpView.setText("");
                        if(loginResponseModel.getMessage() != null && (!loginResponseModel.getMessage().isEmpty())){
                            alert(loginResponseModel.getMessage());
                        }
                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(otpDisposable);
    }

    // register email with api call
    private void registerWithEmailApiCall(final String email, final String password, final String firstName,
                                          final String lastName, final String phone, final String deviceId,
                                          final String deviceTyps, final String loginType, final String fbId,
                                          final String verified, String c_code) {

        dialog.show();
        hideSoftKeyBoard();

        //creating the json object to send
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("first_name", firstName);
        jsonObject.addProperty("last_name", lastName);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("device_id", deviceId);
        jsonObject.addProperty("device_type", deviceTyps);
        jsonObject.addProperty("login_type", loginType);
        jsonObject.addProperty("facebook_id", fbId);
        jsonObject.addProperty("verified", verified);
        jsonObject.addProperty("c_code", c_code);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());
        jsonObject.addProperty("latitude", HappiApplication.getLatitude());
        jsonObject.addProperty("longitude", HappiApplication.getLongitude());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());

        //  Log.d("CHECKING REG:REG", "country_code "+SharedPreferenceUtility.getCountryCode());
        //  Log.d("CHECKING  REG:REG", "latitude "+CeyFlixApplication.getLatitude());
        //   Log.d("CHECKING  REG:REG", "longitude "+CeyFlixApplication.getLongitude());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.RegisterWithEmail(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseModel -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (responseModel == null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        /*status:-
                        0 - registraion failed
                        1 - OTP sent to mail
                        2 - already Registered user*/
                        if (responseModel.getStatus() == 0) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            alert(responseModel.getMessage());
                        } else if (responseModel.getStatus() == 1) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            user_id = responseModel.getData().getUser_id();
                            showOtpVerificationPage();
                        } else if (responseModel.getStatus() == 2) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            alert(responseModel.getMessage());
                        } else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if(responseModel.getMessage() != null && !responseModel.getMessage().isEmpty())
                                alert(responseModel.getMessage());
                        }
                    }
                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(videoDisposable);
    }


    private void sendVerificationCode(String mobile, String countryCode) {

       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );*/
    }

    private void resendVerificationCode(String mobile, String countryCode) {

        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                mResendToken
        );*/
    }
    private void showOtpVerificationPage() {
        isOtpScreenOpen = true;

        bt_signup.setEnabled(false);
        et_name.setEnabled(false);
        et_email.setEnabled(false);
        et_phone_number.setEnabled(false);
        ccp_picker.setCcpClickable(false);
        et_password.setEnabled(false);
        tv_login.setEnabled(false);


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
        setTimer();
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
    private void setTimer() {
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
                tv_timer.setText(minuteTimer + ":" + downTimer);
                if (minute > 0) {
                    --minute;
                }

                counterdown--;
            }

            public void onFinish() {
                tv_timer.setText(" 00:00");
                tv_resend_otp.setEnabled(true);
                tv_resend_otp.setTextColor(getResources().getColor(R.color.colorAccent));
                ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline));
                tv_resend_otp.setClickable(true);
            }
        }.start();
    }

    //the callback to detect the verification status
/*
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code == null) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            alert(e.getMessage());
            Log.d("FirebaseException", e.getMessage());
        }

        private void showOtpVerificationPage() {
            isOtpScreenOpen = true;

            bt_signup.setEnabled(false);
            et_name.setEnabled(false);
            et_email.setEnabled(false);
            et_phone_number.setEnabled(false);
            ccp_picker.setCcpClickable(false);
            et_password.setEnabled(false);
            tv_login.setEnabled(false);


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
            setTimer();
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

        private void setTimer() {

            tv_resend_otp.setEnabled(false);
            ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline_grey));
            tv_resend_otp.setTextColor(getResources().getColor(R.color.coolGrey));


            counterdown = 60;
            minute = 0;
            minuteTimer = "";
            downTimer = "";

            otpTimer = new CountDownTimer(62000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tv_resend_otp.setEnabled(false);
                    minuteTimer = " " + "0" + minute;
                    if (counterdown > 0) {
                        if (counterdown < 10) {
                            downTimer = "0" + counterdown;
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
                    String zeroText = " 00:00";
                    tv_timer.setText(zeroText);
                    if (otpTimer != null) {
                        otpTimer.cancel();
                    }
                    tv_resend_otp.setEnabled(true);
                    tv_resend_otp.setTextColor(getResources().getColor(R.color.colorAccent));
                    ll_resend.setBackground(getResources().getDrawable(R.drawable.bg_outline));
                    tv_resend_otp.setClickable(true);
                }
            }.start();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            super.onCodeSent(s, forceResendingToken);
            showOtpVerificationPage();
            //storing the verification id that is sent to the user
            if (s != null) {
                mVerificationId = s;
                mResendToken = forceResendingToken;
                //   Log.e("VVVV","oncode sent: "+s);
            }

        }

    };
*/

    void alert(String message) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setPositiveButton("OK", null);
        Log.d("ALERT", "Showing alert dialog: " + message);
        bld.create().show();
    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        if (otp != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

            //signing the usermAuth

            signInWithPhoneAuthCredential(credential);

        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //verification successful we will start the profile activity


                            verified = "1";
                            c_code = countryCode;
                            String phoneNo = c_code + et_phone_number.getText().toString().trim();

                            String emailAddress = et_email.getText().toString().trim().toLowerCase();
                            registerApiCall(emailAddress, et_password.getText().toString().trim(), et_name.getText().toString().trim(), "", phoneNo, deviceId, "android-phone", "gmail-login", "0", verified, c_code);


                        } else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.ll_parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    //Register API Call
    private void registerApiCall(final String email, final String password, final String firstName, final String lastName, final String phone, final String deviceId, final String deviceTyps,
                                 final String loginType, final String fbId, final String verified, String c_code) {

        hideSoftKeyBoard();

        //creating the json object to send
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("first_name", firstName);
        jsonObject.addProperty("last_name", lastName);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("device_id", deviceId);
        jsonObject.addProperty("device_type", deviceTyps);
        jsonObject.addProperty("login_type", loginType);
        jsonObject.addProperty("facebook_id", fbId);
        jsonObject.addProperty("verified", verified);
        jsonObject.addProperty("c_code", c_code);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());
        jsonObject.addProperty("latitude", HappiApplication.getLatitude());
        jsonObject.addProperty("longitude", HappiApplication.getLongitude());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.Register(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {


                    if (loginResponseModel == null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                    } else {
                        if (loginResponseModel.getStatus() == 1) {

                            SharedPreferenceUtility.saveUserDetails(
                                    loginResponseModel.getData().get(0).getUser_id(), firstName, email,
                                    password, "", "", "", fbId, false, phone);
                            SharedPreferenceUtility.setGuest(false);
                            isOtpScreenOpen = false;

                            hideSoftKeyBoard();

                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            goToHome();

                        } else if (loginResponseModel.getStatus() == 2) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(RegisterActivity.this, loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                            goToLogin();
                        } else if (loginResponseModel.getStatus() == 3) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                                alert(loginResponseModel.getMessage());
                        } else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                                alert(loginResponseModel.getMessage());
                        }
                    }
                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(videoDisposable);
    }

    private void goToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.putExtra("from", "register");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        if (imm != null && imm.isAcceptingText()) {
            if (getCurrentFocus() != null)// verify if the soft keyboard is open
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isOtpScreenOpen) {
            isOtpScreenOpen = false;
            rl_otp_verification_screen.setVisibility(View.GONE);
            if (otpTimer != null) {
                otpTimer.cancel();
            }

            bt_signup.setEnabled(true);
            et_name.setEnabled(true);
            et_email.setEnabled(true);
            et_phone_number.setEnabled(true);
            ccp_picker.setCcpClickable(true);
            et_password.setEnabled(true);
            tv_login.setEnabled(true);

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
}
