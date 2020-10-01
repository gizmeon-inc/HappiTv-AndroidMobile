package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.happi.android.R;

public class LogoutDialogClass extends Dialog {
    public Activity activity;
    private onLogoutClickListener onLogoutClickListener;

    public LogoutDialogClass(@NonNull Activity activity, onLogoutClickListener onLogoutClickListener) {
        super(activity);
        this.activity = activity;
        this.onLogoutClickListener = onLogoutClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout);

        TypefacedTextViewRegular tv_msg = findViewById(R.id.tv_msg);
        TypefacedTextViewRegular tv_no = findViewById(R.id.tv_no);
        TypefacedTextViewRegular tv_yes = findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClickListener.onLogoutClicked();
                dismiss();
            }
        });
    }

    public interface onLogoutClickListener{
        public void onLogoutClicked();
    }

}
