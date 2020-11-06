package com.happi.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import androidx.core.content.res.ResourcesCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.NumberRegistrationAlert;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
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
    //private EditText et_password;
    private TextInputEditText et_password;
    private TextInputLayout tl_password;
    private EditText et_phone_number;
    private Button bt_signup;
    private TextView tv_login;
    private String deviceId = "";
    private String verified = "0";
    private String c_code = "";
    private String countryCode = "";
    private CountryCodePicker ccp_picker;

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
    boolean isOtpScreenOpen = false;
    CountDownTimer otpTimer;
    private int user_id = 0;
    private String email = "";
    private String password = "";
    private String phoneNo = "";

    //fb login
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private static final String EMAIL = "email";
    private String fbEmailID = "";
    private String fbtoken = "";
    private String fbUserName = "";
    private String fbId = "";
    private String fbUserImagePath = "";
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

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.btm_nav));
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
        if(HappiApplication.getIpAddress().isEmpty()){
            getNetworkIP();
        }

        isOtpScreenOpen = false;
        compositeDisposable = new CompositeDisposable();
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        tl_password = findViewById(R.id.tl_password);
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

        et_name.setError(null);
        et_email.setError(null);
        et_password.setError(null);
        tl_password.setError(null);

        //========================================from login - FB================================//

        //checking if redirected from Login
        if(getIntent() != null){
            String uName = "";
            String uEmail = "";
            if(getIntent().getStringExtra("userName") != null){
                uName = getIntent().getStringExtra("userName");
                et_name.setText(uName);
            }
            if(getIntent().getStringExtra("userEmail") != null){
                uEmail = getIntent().getStringExtra("userEmail");
                et_email.setText(uEmail);
            }

        }

        //========================================================================================//
       //fb
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult result) {
                // App code
                Log.e("FACB","onSuccess");
                Log.e("FACB","onSuccess"+result.getAccessToken());
                Log.e("FACB","onSuccess"+result.getRecentlyDeniedPermissions());
                Log.e("FACB","onSuccess"+result.getRecentlyGrantedPermissions());

                GraphRequest request = GraphRequest.newMeRequest(
                        result.getAccessToken(),
                        (object, response) -> {

                            AfterLogin(object);

                            JSONObject json = response
                                    .getJSONObject();

                            try {
                                if (json != null) {
                                    Log.e("FACB","onSuccess"+json.toString());

                                    /*JSONObject jsonPicture = json
                                            .getJSONObject("picture");
                                    JSONObject jsonData = jsonPicture
                                            .getJSONObject("data");
                                    fbUserImagePath = jsonData
                                            .getString("url");*/

                                    if (json.has("email")) {
                                        fbEmailID = json
                                                .getString("email");
                                    } else {
                                        fbEmailID = null;
                                    }
                                    fbUserName = json
                                            .getString("name");
                                    fbId = json
                                            .getString("id");
                                    if (fbUserImagePath == null) {

                                        fbUserImagePath = "";
                                    }
                                    if (fbEmailID == null
                                            || fbEmailID
                                            .equals("null")) {
                                        fbEmailID = "";
                                    }

                                    String[] firstNameUser = fbUserName
                                            .split("\\s+");

                                    if (AppUtils.isOnline()) {

                                        //loginApi Call
                                        email = fbEmailID;
                                        password = "";
                                        if (email.isEmpty()) {
                                            LayoutInflater li = LayoutInflater.from(HappiApplication.getCurrentContext());
                                            View promptsView = li.inflate(R.layout.prompts, null);

                                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(
                                                    HappiApplication.getCurrentContext());

                                            // set prompts.xml to alertdialog builder
                                            alertDialogBuilder.setView(promptsView);

                                            final EditText userInput = (EditText) promptsView
                                                    .findViewById(R.id.editTextDialogUserInput);

                                            // set dialog message
                                            alertDialogBuilder
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    // get user input and set it to result
                                                                    // edit text

                                                                    email = userInput.getText().toString();
                                                                    Log.e("FACB","onSuccess>> Email-"+email);
                                                                    if (!isValidEmail(email)) {

                                                                        LoginManager.getInstance().logOut();
                                                                        Log.e("FACB","onSuccess>>Invalid Email");
                                                                        Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                                                                    } else {

                                                                        dialog.dismiss();
                                                                        Log.e("FACB","reg>> 2");
//                                                                        registerApiCall(emailAddress, password, fbUserName, "", "", SharedPreferenceUtility.getAdvertisingId(),
//                                                                                "android-phone", "fb-Login", fbId);

                                                                    }

                                                                }
                                                            })
                                                    .setNegativeButton("Cancel",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.cancel();
                                                                    LoginManager.getInstance().logOut();
                                                                    Log.e("FACB","onSuccess>>CANCEL");
                                                                }
                                                            });
                                            // create alert dialog
                                            androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();

                                            // show it
                                            alertDialog.show();
                                        } else {

                                            dialog.show();
                                            Log.e("FACB","onSuccess>>fbLoginApiCall");
                                            Log.e("FACB","onSuccess>>fbLoginApiCall>>fbId"+fbId);
                                            fbLoginApiCall(fbId);
                                        }

                                    } else{
                                        Log.e("FACB","onSuccess>>no internet");
                                        Toast.makeText(HappiApplication.getCurrentContext(), "Check your Internet connection", Toast.LENGTH_LONG).show();

                                    }
                                }

                            } catch (JSONException e) {
                                Log.e("FACB","JSONException>>"+e.getMessage());
                                e.printStackTrace();
                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        //"id,name,email,gender,picture.type(large), birthday");
                        "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("FACB","onCancel>>");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("FACB","onError>>"+exception.getLocalizedMessage());
            }
        });
        //===============================================================================================================
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
                    //et_password.setError("Please enter your password");
                    //et_password.setFocusable(true);
                    //et_password.requestFocus();
                    tl_password.setErrorEnabled(true);
                    tl_password.setError("Please enter your password");
                    tl_password.setFocusable(true);
                    tl_password.requestFocus();
                } else if (et_password.getText().toString().trim().length() < 6) {
                    //et_password.setError("Minimum 6 letters required.");
                    //et_password.setFocusable(true);
                    //et_password.requestFocus();
                    tl_password.setErrorEnabled(true);
                    tl_password.setError("Minimum 6 letters required.");
                    tl_password.setFocusable(true);
                    tl_password.requestFocus();
                }
                /*else if (et_phone_number.getText().toString().trim().isEmpty()) {
                    et_phone_number.setError("Please enter phone number");
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                //}else if (et_phone_number.getText().toString().trim().length() > 15) {
                }*/
               /* else if ((!et_phone_number.getText().toString().trim().isEmpty()) && (et_phone_number.getText().toString().trim().length() > 15)) {
                    et_phone_number.setError("Invalid phone number");
                    et_phone_number.setFocusable(true);
                    et_phone_number.requestFocus();
                } */else {
                    deviceId = SharedPreferenceUtility.getAdvertisingId();
                    dialog.show();
                    hideSoftKeyBoard();
                    countryCode = "+" + ccp_picker.getSelectedCountryCode();
                    verified = "1";
                    c_code = countryCode;
                    email = et_email.getText().toString().trim().toLowerCase();
                    password = et_password.getText().toString().trim();
                    phoneNo = "";
//                    if(!et_phone_number.getText().toString().trim().isEmpty()){
//                        phoneNo = c_code + et_phone_number.getText().toString().trim();
//                    }
                    registerWithEmailApiCall(email, password, et_name.getText().toString().trim(), "",phoneNo,
                            SharedPreferenceUtility.getAdvertisingId(), "android-phone",
                            "gmail-login", "0", verified, c_code);


                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 6 || s.length() == 6){
                    et_password.setError(null);
                    tl_password.setError(null);
                    tl_password.setErrorEnabled(false);
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
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(otpResendDisposable);
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
                                    password, "", "", "", "", false, phoneNo);

                            isOtpScreenOpen = false;

                            hideSoftKeyBoard();
                            goToHome();

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
        jsonObject.addProperty("ipaddress", HappiApplication.getIpAddress());


        ApiClient.UsersService usersService = ApiClient.create();
        Disposable videoDisposable = usersService.RegisterWithEmail(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseModel -> {


                    if (responseModel == null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                    } else {

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
        tv_verfication_code_number.setText(getText(R.string.please_type_verf_code) + " " + email);
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

        alert("Please check your email for verification code. If not found in your Inbox, please check the SPAM folder.");

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


    private void goToHome() {
        //Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        Intent intent = new Intent(RegisterActivity.this, MainHomeActivity.class);
        intent.putExtra("from", "register");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

       try{
           InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
           assert imm != null;
           if (imm != null && imm.isAcceptingText()) {
               if (getCurrentFocus() != null)// verify if the soft keyboard is open
                   imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
           }
       }catch(Exception ex){
           Log.e("reg exception","");
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
            // Log.e("1234###", "exception: " + ex.toString());
        }
        return null;
    }


    //fb
    private void AfterLogin(JSONObject fbobject) {

        try {

            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {

                fbtoken = token.getToken();
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
    //check fb id is already used
    private void fbLoginApiCall(final String fbId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_id", SharedPreferenceUtility.getAdvertisingId());
        jsonObject.addProperty("facebook_id", fbId);
        jsonObject.addProperty("ipaddress", HappiApplication.getIpAddress());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());
        jsonObject.addProperty("loginType", "facebook");
        jsonObject.addProperty("fb_email", email);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.loginViaSocialMedia(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), email,
                                password, "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);

                       hideSoftKeyBoard();
                       goToHome();


                    } else if (loginResponseModel.getStatus() == 101) {

                        user_id = loginResponseModel.getData().get(0).getUser_id();
                        showOtpVerificationPage();

                    } else if (loginResponseModel.getStatus() == 102) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                       /* et_email.setError("Invalid Credentials");
                        et_email.requestFocus();*/
                        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                    }else if (loginResponseModel.getStatus() == 103) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        et_email.setText(email);
                        et_name.setText(fbUserName);


                    } else if (loginResponseModel.getStatus() == 104) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showAlertForLinkingAccount();

                    } else if (loginResponseModel.getStatus() == 500) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(RegisterActivity.this, "Internal Server Error", Toast
                                .LENGTH_SHORT).show();
                    }else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(RegisterActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }


                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);

        /*call.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, Response<LoginResponseModel> response) {

                if (response.isSuccessful()) {
                    if(response.body() != null){
                        if (response.body().getStatus() == 100) {

                            SharedPreferenceUtility.saveUserDetails(response.body().getData().get(0)
                                            .getUser_id(), response.body().getData().get(0).getUser_name(), emailAddress, password, "", "",
                                    "", fbId, false,"");
                            Log.e("FACB","getStatus>> 100");
                            Log.e("FACB","fbId>> "+fbId);
                            //goToHomePage();
                            getSessionToken();
                        } else if (response.body().getStatus() == 102) {
                            Log.e("FACB","getStatus>> 102");
                            //registerApiCall(emailAddress, password, fbUserName, "", "", SharedPreferenceUtility.getAdvertisingId(), "android-phone", "fb-Login", fbId);

                        }else{
                            Log.e("FACB","getStatus>> "+response.body().getStatus());
                        }
                    }else{
                        Log.e("FACB","response body>> null");
                    }

                }else{
                    Log.e("FACB","response >> not success");
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("FACB","onFailure");
                Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

            }
        });*/
    }
    private void showAlertForLinkingAccount(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        String message = "Do you want to link your Facebook account?";
        NumberRegistrationAlert alertLinking = new NumberRegistrationAlert(this, message, "Yes", "No", new NumberRegistrationAlert.OnNumberRegisterUserNegative() {
            @Override
            public void onNumberRegisterNegativeClick() {
                if(dialog != null && !dialog.isShowing()){
                    dialog.show();
                }
                hideSoftKeyBoard();
                goToHome();
            }
        }, new NumberRegistrationAlert.OnNumberRegisterUserPositive() {
            @Override
            public void onNumberRegisterPositiveClick() {
                linkSocialAccountApiCall();
            }
        });
        Objects.requireNonNull(alertLinking.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertLinking.setCancelable(false);
        alertLinking.show();
    }
    private void linkSocialAccountApiCall(){

        if(dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_id", SharedPreferenceUtility.getAdvertisingId());
        jsonObject.addProperty("facebook_id", fbId);
        jsonObject.addProperty("ipaddress", HappiApplication.getIpAddress());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());
        jsonObject.addProperty("loginType", "facebook");
        jsonObject.addProperty("fb_email", email);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.linkSocialAccount(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), email,
                                password, "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);

                        hideSoftKeyBoard();
                        goToHome();


                    }else{
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(RegisterActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }


                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);
    }
}
