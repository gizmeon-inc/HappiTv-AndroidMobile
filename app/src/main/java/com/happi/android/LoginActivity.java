package com.happi.android;

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

import androidx.appcompat.app.AlertDialog;
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
import com.google.gson.JsonObject;
import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.AlertDialogRegisterScreen;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.EmailFbLoginAlertDialog;
import com.happi.android.customviews.LogoutAlertDialog;
import com.happi.android.customviews.NumberRegistrationAlert;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.otpView.OtpView;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends BaseActivity implements LogoutAlertDialog.onLogoutClickListener {

    private EditText et_email;
    private TextInputEditText et_password;
    private TextInputLayout tl_password;
    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;

    //otp
    private CountDownTimer otpTimer;
    private int counterdown;
    private int minute;
    private String downTimer = "";
    private String minuteTimer = "";
    private Button btLogin;
    private RelativeLayout rl_otp_screen;
    private RelativeLayout rl_otp_verification_screen;
    private TextView tv_timer;
    private TextView tv_resend_otp;
    private TextView tv_done;
    private OtpView otpView;
    private TextView tv_verfication_code_number;
    private ImageView iv_back_to_page;
    private LinearLayout ll_resend;
    private boolean isOtpScreenOpen = false;
    private int user_id = 0;

    //fb login
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private static final String EMAIL = "email";
    private String fbEmailID = "";
    private String fbtoken = "";
    private String fbUserName = "";
    private String fbId = "";
    private String fbUserImagePath = "";

    private String emailAddress = "";
    private String password = "";


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

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.btm_nav));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_login);


        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.happi.android",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }*/


        compositeDisposable = new CompositeDisposable();

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Please wait.");

        HappiApplication.setCurrentContext(this);
        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }
        if (HappiApplication.getIpAddress().isEmpty()) {
            getNetworkIP();
        }
        RelativeLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);


        LinearLayout ll_signup = findViewById(R.id.ll_signup);
        TextView tv_signup = findViewById(R.id.tv_signup);
        TextView tv_skip = findViewById(R.id.tv_skip);

        if (SharedPreferenceUtility.isRegistration_mandatory_flag()) {
            tv_skip.setVisibility(View.GONE);
        } else {
            //tv_skip.setVisibility(View.VISIBLE);
            tv_skip.setVisibility(View.GONE);
        }
        if (SharedPreferenceUtility.isSubscription_mandatory_flag()) {
            ll_signup.setVisibility(View.GONE);
            tv_skip.setVisibility(View.GONE);
        } else {
            ll_signup.setVisibility(View.VISIBLE);
        }

        tv_skip.setVisibility(View.GONE);

        btLogin = findViewById(R.id.bt_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        tl_password = findViewById(R.id.tl_password);

        TypefacedTextViewRegular tv_forgot_password = findViewById(R.id.tv_forgot_password);

        et_email.setError(null);
        tl_password.setError(null);
        et_password.setError(null);

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
                                        emailAddress = fbEmailID;
                                        password = "";
                                        if (emailAddress.isEmpty()) {
                                            EmailFbLoginAlertDialog emailDialog = new EmailFbLoginAlertDialog(HappiApplication.getCurrentActivity(), "OK", "CANCEL",
                                                    new EmailFbLoginAlertDialog.OnOkClicked() {
                                                        @Override
                                                        public void onClickPositive(String email) {
                                                            emailAddress = email;
                                                            Log.e("FACB", "onSuccess>> Email-" + emailAddress);
                                                            Log.e("FACB", "onSuccess>>fbLoginApiCall-from-dialog");
                                                            Log.e("FACB", "onSuccess>>fbLoginApiCall-from-dialog>>fbId" + fbId);
                                                            fbLoginApiCall(fbId);
                                                        }
                                                    }, new EmailFbLoginAlertDialog.OnCancelClicked() {
                                                @Override
                                                public void onClickNegative() {
                                                    LoginManager.getInstance().logOut();
                                                    Log.e("FACB","onSuccess>>CANCEL");
                                                }
                                            });
                                            emailDialog.setCancelable(false);
                                            Objects.requireNonNull(emailDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            emailDialog.show();


                                         /*   LayoutInflater li = LayoutInflater.from(HappiApplication.getCurrentContext());
                                            View promptsView = li.inflate(R.layout.prompts, null);

                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
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

                                                                    hideSoftKeyBoard();
                                                                    if (userInput.getText().toString().trim().isEmpty()) {

                                                                        userInput.setError("Please enter your email Address");
                                                                        userInput.requestFocus();
                                                                    } else if (!isValidEmail(userInput.getText().toString().trim())) {

                                                                        userInput.setError("Invalid Email");
                                                                        userInput.requestFocus();
                                                                    }else{
                                                                        emailAddress = userInput.getText().toString().trim().toLowerCase();
                                                                        Log.e("FACB","onSuccess>> Email-"+emailAddress);
                                                                        Log.e("FACB","onSuccess>>fbLoginApiCall-from-dialog");
                                                                        Log.e("FACB","onSuccess>>fbLoginApiCall-from-dialog>>fbId"+fbId);
                                                                        dialog.dismiss();
                                                                        fbLoginApiCall(fbId);
                                                                    }

                                                                    *//*if (!isValidEmail(emailAddress)) {

                                                                        LoginManager.getInstance().logOut();
                                                                        Log.e("FACB","onSuccess>>Invalid Email");
                                                                        Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                                                                    } else {

                                                                        dialog.dismiss();
                                                                        Log.e("FACB","reg>> 2");
//                                                                        registerApiCall(emailAddress, password, fbUserName, "", "", SharedPreferenceUtility.getAdvertisingId(),
//                                                                                "android-phone", "fb-Login", fbId);

                                                                    }*//*

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
                                            AlertDialog alertDialog = alertDialogBuilder.create();

                                            // show it
                                            alertDialog.show();*/
                                        } else {

                                            emailAddress = fbEmailID.toLowerCase();
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

//                et_password.setError("Please enter your password");
//                et_password.requestFocus();
                tl_password.setErrorEnabled(true);
                tl_password.setError("Please enter your password");
                tl_password.requestFocus();

            } else {

                hideSoftKeyBoard();

                 emailAddress = et_email.getText().toString().trim().toLowerCase();
                 password = et_password.getText().toString().trim();
                loginApiCall(emailAddress, password);

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
        tv_signup.setOnClickListener(view -> {

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void goToHomePage() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        //Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
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

                        user_id =loginResponseModel.getData().get(0).getUser_id();
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
                        Toast.makeText(LoginActivity.this, "Internal Server Error", Toast
                                .LENGTH_SHORT).show();
                    }else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(LoginActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

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

        alert("Please check your email for verification code. If not found in your Inbox, please check the SPAM folder.");

    }

   /* private void showOtpVerificationPage() {
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
        alert("We have sent an email to your account with the OTP for verification. Please check your SPAM, if it's not there in your INBOX.");

        // alert("The email we sent may have landed in your SPAM folder. Please check your SPAM, if not found in your INBOX.");
        //alert("We have sent an email to your account with the OTP for verification. Please check your SPAM, if not found in your INBOX.");
        //alert("We have sent an email to your account with the OTP for verification. It may have landed in your SPAM folder. Please check your SPAM, if not found in your INBOX.");
    }*/
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
                        if(loginResponseModel.getMessage() != null && (!loginResponseModel.getMessage().isEmpty())){
                            alert(loginResponseModel.getMessage());
                        }
                    }

                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

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

                               /* if (!isGuest) {
                                    HappiApplication.setIsNewLoginFromPremiumPage(false);
                                    //not from premium
                                    goToHomePage();
                                } else {
                                    goToHomePage();
                                }*/

                            if (SharedPreferenceUtility.getGuest()) {
                                goToHomePage();
                            } else {
                                getUserSubscriptions();
                            }



                        }, throwable -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
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
                .subscribe(subscriptionResponseModel -> {
                    if (subscriptionResponseModel.isForcibleLogout()) {
                        loginExceededAlertSubscription();
                    } else {
                        List<String> subids = new ArrayList<>();
                        if (subscriptionResponseModel.getData().size() != 0) {

                            List<UserSubscriptionModel> userSubscriptionModelList = subscriptionResponseModel.getData();

                            if (userSubscriptionModelList.size() != 0) {
                                for (UserSubscriptionModel model : userSubscriptionModelList) {
                                    subids.add(model.getSub_id());
                                }

                            }

                        }
                        HappiApplication.setSub_id(subids);

                        if(SharedPreferenceUtility.isSubscription_mandatory_flag()){
                            if (HappiApplication.getSub_id().isEmpty()) {
                                goToSubscriptionScreen();
                            } else {
                                goToHomePage();
                            }
                        }else{
                            goToHomePage();
                        }
                       // goToHomePage();

                    }

                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }
    private void loginExceededAlertSubscription() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LogoutAlertDialog alertDialog =
                new LogoutAlertDialog(this, this);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
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

        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            if (imm.isAcceptingText()) { // verify if the soft keyboard is open
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                // imm.hideSoftInputFromWindow(btLogin.getWindowToken(), 0);
            }
        }catch(Exception ex){
            Log.e("Exception"," "+ex.getMessage());
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
        if (isOtpScreenOpen) {
            isOtpScreenOpen = false;
            rl_otp_verification_screen.setVisibility(View.GONE);

            btLogin.setEnabled(true);
            et_email.setEnabled(true);
            et_password.setEnabled(true);
        } else {
            super.onBackPressed();
            finish();
            finishAffinity();
            System.exit(0);
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
            Log.e("1234###", "exception: " + ex.toString());
        }
        return null;
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
                        SharedPreferenceUtility.setPartnerId("");
                        SharedPreferenceUtility.setNotificationIds(new ArrayList<>());
                        SharedPreferenceUtility.setSubscriptionItemIdList(new ArrayList<>());

                        HappiApplication.setSub_id(new ArrayList<>());
                        LoginManager.getInstance().logOut();
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
                        SharedPreferenceUtility.setPartnerId("");
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

    @Override
    public void onLogoutClicked() {
            logoutApiCall();
    }

    @Override
    public void onLogoutAllClicked() {
        logoutAllApiCall();
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
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(otpResendDisposable);
    }
    private void goToSubscriptionScreen(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(LoginActivity.this, MainSubscriptionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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
        if(dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("device_id", SharedPreferenceUtility.getAdvertisingId());
        jsonObject.addProperty("facebook_id", fbId);
        jsonObject.addProperty("ipaddress", HappiApplication.getIpAddress());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());
        jsonObject.addProperty("loginType", "facebook");
        jsonObject.addProperty("fb_email", emailAddress);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.loginViaSocialMedia(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), emailAddress,
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
                       /* et_email.setError("Invalid Credentials");
                        et_email.requestFocus();*/
                        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                    }else if (loginResponseModel.getStatus() == 103) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        goToRegisterScreen();

                    } else if (loginResponseModel.getStatus() == 104) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showAlertForLinkingAccount();

                    } else if (loginResponseModel.getStatus() == 500) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, "Internal Server Error", Toast
                                .LENGTH_SHORT).show();
                    }else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(LoginActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }


                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

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
              getSessionToken();
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
        jsonObject.addProperty("fb_email", emailAddress);
        jsonObject.addProperty("country_code", SharedPreferenceUtility.getCountryCode());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable loginDisposable = usersService.linkSocialAccount(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(loginResponseModel.getData().get(0)
                                        .getUser_id(), loginResponseModel.getData().get(0).getUser_name(), emailAddress,
                                password, "", "", "", "", false, loginResponseModel.getData().get(0).getPhone());
                        SharedPreferenceUtility.setGuest(false);

                        getSessionToken();


                    }else{
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (loginResponseModel.getMessage() != null && !loginResponseModel.getMessage().isEmpty())
                            Toast.makeText(LoginActivity.this, "" + loginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    }


                }, throwable -> {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Something went wrong. Please try again after sometime", Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(loginDisposable);
    }

    private void goToRegisterScreen(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("userName",fbUserName);
        intent.putExtra("userEmail",emailAddress);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}

