package com.happi.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.happi.android.BuildConfig;
import com.happi.android.R;

public class AboutUsDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog dialog;
    public TypefacedTextViewRegular bt_submit;
    public TypefacedTextViewRegular tv_version;

    public AboutUsDialogClass(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_about);

        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("Version " + BuildConfig.VERSION_NAME);
        bt_submit = findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
