package com.happi.android.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.happi.android.ChannelLivePlayerActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.happi.android.HomeActivity;
import com.happi.android.MainHomeActivity;
import com.happi.android.R;
import com.happi.android.VideoPlayerActivity;
import com.happi.android.utils.ConstantUtils;

import java.io.IOException;
import java.net.URL;

public class HappiMessagingService extends FirebaseMessagingService {

    private static final String TAG = "HappiMessagingService";
    Bitmap bitmap;
    private static final String CHANNEL_ID = "Happi Tv";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String id="0";
        createNotificationChannel();
        String type=remoteMessage.getData().get("content_type");
        if(type.equals("channel")){
            id = remoteMessage.getData().get("channel_id");
        }else{
            id = remoteMessage.getData().get("video_id");
        }

        String imageUri = remoteMessage.getData().get("mediaUrl");
        String title = remoteMessage.getData().get("title");
        String description = remoteMessage.getData().get("description");
        bitmap = getBitmapfromUrl(imageUri);
        if(imageUri.equals("")){
            sendNotification(id,type, bitmap, title,description,false);
        }else {
            sendNotification(id,type, bitmap, title,description,true);
        }

    }

    private void sendNotification(String id,String type, Bitmap image, String title, String description,boolean im) {
        if(type.equals("channel")){
           // Intent intent = new Intent(this, ChannelHomeActivity.class);
            Intent intent = new Intent(this, ChannelLivePlayerActivity.class);
            intent.putExtra(ConstantUtils.CHANNEL_ID, Integer.parseInt(id));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //stackBuilder.addParentStack(HomeActivity.class);
            stackBuilder.addParentStack(MainHomeActivity.class);
            //stackBuilder.addParentStack(ChannelHomeActivity.class);
            stackBuilder.addParentStack(ChannelLivePlayerActivity.class);
            stackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews notificationLayout = new RemoteViews("com.happi.android",
                    R.layout.notification_small);
            RemoteViews notificationLayoutExpanded = new RemoteViews("com.happi.android", R.layout.notification_large);

            notificationLayout.setTextViewText(R.id.tv_title, title);
            notificationLayout.setTextViewText(R.id.tv_description, description);
            notificationLayoutExpanded.setTextViewText(R.id.tv_title, title);
            notificationLayoutExpanded.setTextViewText(R.id.tv_description, description);
            notificationLayoutExpanded.setImageViewBitmap(R.id.iv_bitmap, image);
            if(!im){
                notificationLayoutExpanded.setViewVisibility(R.id.iv_bitmap, View.GONE);
            }
            notificationLayout.setOnClickPendingIntent(R.id.rl_parent, pendingIntent);
            notificationLayoutExpanded.setOnClickPendingIntent(R.id.rl_parent, pendingIntent);

            // Apply the layouts to the notification
            Notification customNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                  //  .setSmallIcon(R.drawable.small_icon)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutExpanded)
                    .setColorized(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(1032 //ID of notification
                        , customNotification);
            }

        }else{

            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(ConstantUtils.VIDEO_DETAILS, Integer.parseInt(id));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //stackBuilder.addParentStack(HomeActivity.class);
            stackBuilder.addParentStack(MainHomeActivity.class);
            stackBuilder.addParentStack(VideoPlayerActivity.class);
            stackBuilder.addNextIntent(intent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews notificationLayout = new RemoteViews("com.happi.android",
                    R.layout.notification_small);
            RemoteViews notificationLayoutExpanded = new RemoteViews("com.happi.android", R.layout.notification_large);

            notificationLayout.setTextViewText(R.id.tv_title, title);
            notificationLayout.setTextViewText(R.id.tv_description, description);
            notificationLayoutExpanded.setTextViewText(R.id.tv_title, title);
            notificationLayoutExpanded.setTextViewText(R.id.tv_description, description);
            notificationLayoutExpanded.setImageViewBitmap(R.id.iv_bitmap, image);
            if(!im){
                notificationLayoutExpanded.setViewVisibility(R.id.iv_bitmap, View.GONE);
            }
            notificationLayout.setOnClickPendingIntent(R.id.rl_parent, pendingIntent);
            notificationLayoutExpanded.setOnClickPendingIntent(R.id.rl_parent, pendingIntent);

            // Apply the layouts to the notification
            Notification customNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                  //  .setSmallIcon(R.drawable.small_icon)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutExpanded)
                    .setColorized(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(1032 //ID of notification
                        , customNotification);
            }

        }

    }

    /* To get a Bitmap image from the URL received*/
    public Bitmap getBitmapfromUrl(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            Bitmap image = BitmapFactory.decodeStream(url.openStream());

            if (image != null) {

                return image;
            } else {

                Bitmap splash = drawableToBitmap(ContextCompat.getDrawable(this,
                        R.drawable.ic_placeholder));
                return splash;
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SharedPreferenceUtility.setFcmToken(s);
    }
}
