package com.happi.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.happi.android.common.ActivityChooser;
import com.happi.android.utils.ConstantUtils;

public class NotificationManagerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String notification_id = intent.getStringExtra("notification_id");

        ActivityChooser.goToActivity(ConstantUtils.VIDEO_PLAYER_ACTIVITY, notification_id);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
