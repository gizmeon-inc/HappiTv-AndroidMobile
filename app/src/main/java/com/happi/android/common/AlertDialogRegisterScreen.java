package com.happi.android.common;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewRegular;

public class AlertDialogRegisterScreen extends Dialog {
    public Activity activity;
    private String text;


    public AlertDialogRegisterScreen(@NonNull Activity activity, String message) {
        super(activity);
        this.activity = activity;
        this.text = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_register_screen);

        TypefacedTextViewRegular tv_msg = findViewById(R.id.tv_msg);
        TypefacedTextViewRegular tv_positive = findViewById(R.id.tv_positive);
        tv_msg.setText(text);

        tv_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
