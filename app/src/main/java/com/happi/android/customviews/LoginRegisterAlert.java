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
    TypefacedTextViewRegular tv_neutral;
    private String text;
    private String negative;
    private String neutral;
    private LoginRegisterAlert.OnLoginRegisterUserNegative onLoginRegisterNegative;
    private LoginRegisterAlert.OnLoginRegisterUserNeutral onLoginRegisterNeutral;
    private boolean isCancelable;



    public LoginRegisterAlert(@NonNull Activity activity, String message,
                              String negative, String neutral, LoginRegisterAlert.OnLoginRegisterUserNegative onLoginRegisterNegative,
                              LoginRegisterAlert.OnLoginRegisterUserNeutral onLoginRegisterNeutral, boolean isCancelable) {
        super(activity);

        this.text = message;
        this.negative = negative;
        this.neutral = neutral;
        this.onLoginRegisterNegative = onLoginRegisterNegative;
        this.onLoginRegisterNeutral = onLoginRegisterNeutral;
        this.isCancelable = isCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login_register_alert);

        tv_msg = findViewById(R.id.tv_msg);
        tv_negative = findViewById(R.id.tv_negative);
        tv_neutral = findViewById(R.id.tv_neutral);
        tv_msg.setText(text);
        tv_negative.setText(negative);
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
        tv_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginRegisterNeutral.onLoginRegisterNeutralClick();
                dismiss();
            }
        });
    }

    public interface OnLoginRegisterUserNegative{
        public void onLoginRegisterNegativeClick();

    }
    public interface OnLoginRegisterUserNeutral{
        public void onLoginRegisterNeutralClick();

    }
}
