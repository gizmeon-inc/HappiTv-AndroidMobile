package com.happi.android.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.happi.android.utils.ConstantUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotifyService.class);
        service.putExtra("reason", intent.getStringExtra("reason"));
        service.putExtra("video_title",intent.getStringExtra("video_title"));
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0));
        service.putExtra(ConstantUtils.CHANNEL_ID, intent.getIntExtra(ConstantUtils.CHANNEL_ID,-1));
        service.putExtra(ConstantUtils.UNIQUE_ID, intent.getIntExtra(ConstantUtils.UNIQUE_ID, -1));

        context.startService(service);
    }
}
