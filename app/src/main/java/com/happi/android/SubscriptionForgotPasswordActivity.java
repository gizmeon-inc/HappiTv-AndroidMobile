package com.happi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.webservice.ApiClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionForgotPasswordActivity extends BaseActivity {

    EditText et_email;
    Button bt_submit;
    TextView tv_return_login;
    private ProgressDialog dialog;
    private CompositeDisposable compositeDisposable;

    //validate email_id
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
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_subscription_forgot_password);
        HappiApplication.setCurrentContext(this);
        LinearLayout ll_parent = findViewById(R.id.ll_parent);
        setupUI(ll_parent);

        tv_return_login = (TextView) findViewById(R.id.tv_return_login);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        et_email = (EditText) findViewById(R.id.et_email);

        et_email.setError(null);
        compositeDisposable = new CompositeDisposable();

        //call submit on softkeyboard_done
        et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    bt_submit.performClick();
                }
                return false;
            }
        });

        bt_submit.setOnClickListener(view -> {
            String email = "";

            et_email.setError(null);
            email = et_email.getText().toString().trim().toLowerCase();

            if (email.isEmpty()) {

                et_email.setError("Enter Your Email");
                et_email.requestFocus();
            } else if (!isValidEmail(email)) {

                et_email.setError("Invalid Email");
                et_email.requestFocus();
            } else {

                forgotPasswordApiCall(email);
            }
        });

        tv_return_login.setOnClickListener(view -> {
            Intent intent = new Intent(SubscriptionForgotPasswordActivity.this, SubscriptionLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }


    public void forgotPasswordApiCall(String email) {

        dialog = new ProgressDialog(SubscriptionForgotPasswordActivity.this, R.style.MyTheme);
        dialog.show();

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable forgotPasswordDisposable = usersService.forgotPassword(email, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(basicResponse -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (basicResponse.getMessage().equalsIgnoreCase("Email has been sent successfully")) {
                        Toast.makeText(SubscriptionForgotPasswordActivity.this, "Please check your mail", Toast.LENGTH_SHORT).show();

                    } else if (basicResponse.getMessage().equalsIgnoreCase("Invalid user")) {
                        Toast.makeText(SubscriptionForgotPasswordActivity.this, "This email id is not registered with " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();

                    }


                }, throwable -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(SubscriptionForgotPasswordActivity.this, getString(R.string.server_error), Toast
                            .LENGTH_LONG).show();

                });
        compositeDisposable.add(forgotPasswordDisposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        safelyDispose(compositeDisposable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HappiApplication.setCurrentContext(this);
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
}

