package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;

public class NumberRegistrationAlert extends Dialog {

    private TypefacedTextViewRegular tv_msg;
    private TypefacedTextViewRegular tv_negative;
    private TypefacedTextViewRegular tv_positive;
    private String text;
    private String positive;
    private String negative;
    private OnNumberRegisterUserNegative onNumberRegisterUserNegative;
    private OnNumberRegisterUserPositive onNumberRegisterUserPositive;


    public NumberRegistrationAlert(@NonNull Activity activity, String message, String positive, String negative,
                                   OnNumberRegisterUserNegative onNumberRegisterUserNegative,
                                   OnNumberRegisterUserPositive onNumberRegisterUserPositive) {
        super(activity);
        this.text = message;
        this.positive = positive;
        this.negative = negative;
        this.onNumberRegisterUserNegative = onNumberRegisterUserNegative;
        this.onNumberRegisterUserPositive = onNumberRegisterUserPositive;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_number_register_alert);

        tv_msg = findViewById(R.id.tv_msg);
        tv_negative = findViewById(R.id.tv_negative);
        tv_positive = findViewById(R.id.tv_positive);
        tv_msg.setText(text);
        tv_negative.setText(negative);
        tv_positive.setText(positive);

        tv_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberRegisterUserPositive.onNumberRegisterPositiveClick();
                dismiss();
            }
        });
        tv_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberRegisterUserNegative.onNumberRegisterNegativeClick();
                dismiss();
            }
        });
    }

    public interface OnNumberRegisterUserPositive{
        public void onNumberRegisterPositiveClick();

    }
    public interface OnNumberRegisterUserNegative{
        public void onNumberRegisterNegativeClick();

    }

}

