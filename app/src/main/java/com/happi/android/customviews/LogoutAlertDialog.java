package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;

public class LogoutAlertDialog extends Dialog {
    public Activity activity;
    private onLogoutClickListener onLogoutClickListener;

    public LogoutAlertDialog(@NonNull Activity activity, onLogoutClickListener onLogoutClickListener) {
        super(activity);
        this.activity = activity;
        this.onLogoutClickListener = onLogoutClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout_all_option);

        TypefacedTextViewRegular tv_msg = findViewById(R.id.tv_msg);
        TypefacedTextViewRegular tv_logoutall = findViewById(R.id.tv_logoutall);
        TypefacedTextViewRegular tv_logout = findViewById(R.id.tv_logout);

        tv_logoutall.setVisibility(View.GONE);

        tv_logoutall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClickListener.onLogoutAllClicked();
                dismiss();
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClickListener.onLogoutClicked();
                dismiss();
            }
        });
    }

    public interface onLogoutClickListener{
        public void onLogoutClicked();
        public void onLogoutAllClicked();
    }

}

