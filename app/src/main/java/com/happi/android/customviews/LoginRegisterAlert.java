package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;

public class LoginRegisterAlert extends Dialog {

    TypefacedTextViewRegular tv_msg;
    TypefacedTextViewRegular tv_negative;
    TypefacedTextViewRegular tv_positive;
    TypefacedTextViewRegular tv_neutral;
    private String text;
    private String positive;
    private String negative;
    private String neutral;
    private LoginRegisterAlert.OnLoginRegisterUserNegative onLoginRegisterNegative;
    private LoginRegisterAlert.OnLoginRegisterUserNeutral onLoginRegisterNeutral;
    private LoginRegisterAlert.OnLoginRegisterUserPositive onLoginRegisterUserPositive;
    private boolean isCancelable;



    public LoginRegisterAlert(@NonNull Activity activity, String message, String positive,
                              String negative, String neutral, LoginRegisterAlert.OnLoginRegisterUserNegative onLoginRegisterNegative,
                              LoginRegisterAlert.OnLoginRegisterUserNeutral onLoginRegisterNeutral,
                              LoginRegisterAlert.OnLoginRegisterUserPositive onLoginRegisterUserPositive, boolean isCancelable) {
        super(activity);

        this.text = message;
        this.positive = positive;
        this.negative = negative;
        this.neutral = neutral;
        this.onLoginRegisterNegative = onLoginRegisterNegative;
        this.onLoginRegisterNeutral = onLoginRegisterNeutral;
        this.onLoginRegisterUserPositive = onLoginRegisterUserPositive;
        this.isCancelable = isCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login_register_alert);

        tv_msg = findViewById(R.id.tv_msg);
        tv_negative = findViewById(R.id.tv_negative);
        tv_positive = findViewById(R.id.tv_positive);
        tv_neutral = findViewById(R.id.tv_neutral);
        tv_msg.setText(text);
        tv_negative.setText(negative);
        tv_positive.setText(positive);
        tv_neutral.setText(neutral);


        tv_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onLoginRegisterNegative.onLoginRegisterNegativeClick();
               if(isCancelable){
                   dismiss();
               }

            }
        });
        tv_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onLoginRegisterUserPositive.onLoginRegisterPositiveClick();
                if(isCancelable){
                    dismiss();
                }
            }
        });
        tv_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginRegisterNeutral.onLoginRegisterNeutralClick();
                dismiss();
            }
        });
    }

    public interface OnLoginRegisterUserPositive{
        public void onLoginRegisterPositiveClick();

    }
    public interface OnLoginRegisterUserNegative{
        public void onLoginRegisterNegativeClick();

    }
    public interface OnLoginRegisterUserNeutral{
        public void onLoginRegisterNeutralClick();

    }
}
