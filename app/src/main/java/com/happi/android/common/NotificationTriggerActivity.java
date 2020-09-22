package com.happi.android.common;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.happi.android.utils.ConstantUtils;

import java.util.Calendar;

public class NotificationTriggerActivity extends AppCompatActivity {


    public static void sendNotifications(Long timeStamp, String videoTitle, int channelId, int uniqueId, Context activity){
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Activity.ALARM_SERVICE);
        Intent alarmIntent = new Intent(activity.getApplicationContext(), AlarmReceiver.class); // AlarmReceiver1 = broadcast receiver

        alarmIntent.putExtra("reason", "notification");
        alarmIntent.putExtra("video_title",videoTitle);
        alarmIntent.putExtra("timestamp", timeStamp);
        alarmIntent.putExtra(ConstantUtils.CHANNEL_ID,channelId);
        alarmIntent.putExtra(ConstantUtils.UNIQUE_ID, uniqueId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, uniqueId, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        // alarmManager.set(AlarmManager.RTC_WAKEUP, 1582700280000L, pendingIntent);


    }
    public static void clearNotification(int notificationId,Context activity){
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);

    }
}
