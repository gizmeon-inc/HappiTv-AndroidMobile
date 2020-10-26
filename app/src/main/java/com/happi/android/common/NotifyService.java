package com.happi.android.common;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;

import com.happi.android.HomeActivity;
import com.happi.android.LoginActivity;
import com.happi.android.MainHomeActivity;
import com.happi.android.R;
import com.happi.android.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class NotifyService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private Notification mNotification;
    private int mNotificationId = 1000;
    private  String CHANNEL_ID = "samples.notification.devdeeds.com.CHANNEL_ID";
    private  String CHANNEL_NAME = "Sample Notification";


    public NotifyService() {
        super("NotifyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Long timestamp = 0L;
        String videoTitle ="";
        int channel_id = -1;
        int uniqueId = 0;

        if (intent != null && intent.getExtras() != null) {
            timestamp = (Long) intent.getExtras().getLong("timestamp");
            videoTitle = (String) intent.getExtras().getString("video_title");
            channel_id = (int) intent.getExtras().getInt(ConstantUtils.CHANNEL_ID);
            uniqueId = (int) intent.getExtras().getInt(ConstantUtils.UNIQUE_ID, uniqueId);
        }

        if (timestamp > 0) {
            Context context = this.getApplicationContext();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notifyIntent = new Intent();
            String title = "Watch Now!";
            String message = "";
            if(videoTitle.isEmpty()){
                message = "Live playing";
            }else{
                message = videoTitle +" "+ "starts now.";
            }
            if(SharedPreferenceUtility.getUserId() != 0) {
                if(SharedPreferenceUtility.getIsLiveVisible() && (SharedPreferenceUtility.getChannelId() == channel_id)){

                }else {
                   // notifyIntent = new Intent(this, HomeActivity.class);
                    notifyIntent = new Intent(this, MainHomeActivity.class);
                }
            }else{
                notifyIntent = new Intent(this, LoginActivity.class);
            }
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            notifyIntent.putExtra("title", title);
            notifyIntent.putExtra("message", message);
            notifyIntent.putExtra("notification", true);
            notifyIntent.putExtra(ConstantUtils.CHANNEL_ID, channel_id);
            notifyIntent.putExtra(ConstantUtils.UNIQUE_ID, uniqueId);
            createChannel(message);


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Resources res = this.getResources();
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotification = new Notification.Builder(this, CHANNEL_ID)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message))
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setContentText(message).build();
            } else {

                mNotification = new Notification.Builder(this)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message))
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setContentText(message).build();

            }
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationId is a unique int for each notification that you must define
            //notificationManager.notify(mNotificationId, mNotification);
            notificationManager.notify(uniqueId, mNotification);

            ArrayList<Integer> notfIds = new ArrayList<>();
            notfIds = SharedPreferenceUtility.getNotificationIds();
            if(notfIds.size()>0 && (uniqueId != 0)){
                int index = notfIds.indexOf(uniqueId);
                if(index != -1)
                    notfIds.remove(index);
                SharedPreferenceUtility.setNotificationIds(notfIds);
            }

        }
    }

    private void createChannel(String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Context context = getApplicationContext();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.parseColor("#24B0AF"));
            notificationChannel.setDescription("notification_channel_description");
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setDescription(description);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
