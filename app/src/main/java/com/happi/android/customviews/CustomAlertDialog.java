package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;

public class CustomAlertDialog extends Dialog /*implements View.OnClickListener*/ {
    private Activity activity;
     TypefacedTextViewRegular tv_msg;
     TypefacedTextViewRegular tv_negative;
     TypefacedTextViewRegular tv_positive;
    private String text;
    private String positive;
    private String negative;
    private String type;
    private OnRegNumber onRegNumber;
    private OnSignUp onSignUp;
    private OnOkClick onOkClick;
    private OnTryAgain onTryAgain;


    public CustomAlertDialog(@NonNull Activity activity, String type, String message, String positive,
                             String negative, OnRegNumber onRegNumber, OnSignUp onSignUp, OnOkClick onOkClick, OnTryAgain onTryAgain) {
        super(activity);
        this.activity = activity;
        this.type = type;
        this.text = message;
        this.positive = positive;
        this.negative = negative;
        if(type.equalsIgnoreCase("numberalert")) {
            this.onRegNumber = onRegNumber;
        }else if(type.equalsIgnoreCase("signup")) {
            this.onSignUp = onSignUp;
        }else if(type.equalsIgnoreCase("ok")){
            this.onOkClick = onOkClick;
        }else if(type.equalsIgnoreCase("tryagain")){
            this.onTryAgain = onTryAgain;
        }

    }
    /*public CustomAlertDialog(@NonNull Activity activity,String type,String message,String positive,String negative) {
        super(activity);
        this.activity = activity;
        this.type = type;
        this.text = message;
        this.positive = positive;
        this.negative = negative;
        this.onSignUp = onSignUp;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_alert);
        tv_msg = findViewById(R.id.tv_msg);
        tv_negative = findViewById(R.id.tv_negative);
        tv_positive = findViewById(R.id.tv_positive);
        tv_msg.setText(text);
        tv_negative.setText(negative);
        tv_positive.setText(positive);


        tv_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equalsIgnoreCase("numberalert")){
                    dismiss();
                }else if(type.equalsIgnoreCase("signup")){
                    dismiss();
                }
            }
        });
        tv_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equalsIgnoreCase("numberalert")){
                    dismiss();
                    onRegNumber.onClickRegNumberPositive();
                }else  if(type.equalsIgnoreCase("signup")){
                    dismiss();
                   onSignUp.onSignUpPositive();
                }else  if(type.equalsIgnoreCase("ok")){
                    dismiss();
                    onOkClick.onOkClickNeutral();
                }else  if(type.equalsIgnoreCase("tryagain")){
                    dismiss();
                    onTryAgain.onTryAgainPositive();
                }
            }
        });
    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_negative:
               if(type.equalsIgnoreCase("numberalert")){
                   dismiss();
               }
                break;
            case R.id.tv_positive:
                if(type.equalsIgnoreCase("numberalert")){
                    onRegNumber.onClickRegNumberPositive();
                }
                break;
            default:
                break;
        }
        dismiss();

    }*/
    public interface OnRegNumber{
        public void onClickRegNumberPositive();
    }
    public interface OnSignUp{
        public void onSignUpPositive();
        public void onSignUpNegative();
    }
    public interface OnOkClick{
        public void onOkClickNeutral();
    }
    public interface OnTryAgain{
        public void onTryAgainPositive();
    }



}
