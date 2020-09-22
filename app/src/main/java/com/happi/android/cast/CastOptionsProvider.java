package com.happi.android.cast;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.ImageHints;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import com.google.android.gms.common.images.WebImage;

import java.util.List;

public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {
        NotificationOptions notificationOptions = new NotificationOptions.Builder()
//                .setActions(Arrays.asList(MediaIntentReceiver.ACTION_SKIP_NEXT,
//                        MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
//                        MediaIntentReceiver.ACTION_STOP_CASTING), new int[]{1, 2})
                // .setTargetActivityClassName(HomeActivity.class.getName())
                .setTargetActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setImagePicker(new ImagePickerImpl())
                .setNotificationOptions(notificationOptions)
                //.setExpandedControllerActivityClassName(HomeActivity.class.getName())
                .setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        return new CastOptions.Builder()
                .setReceiverApplicationId("A2844572")
                .setCastMediaOptions(mediaOptions)
                .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context appContext) {
        return null;
    }

    private static class ImagePickerImpl extends ImagePicker {

        @Override
        public WebImage onPickImage(MediaMetadata mediaMetadata, @NonNull ImageHints imageHints) {
            //return super.onPickImage(mediaMetadata, imageHints);
            if ((mediaMetadata == null) || !mediaMetadata.hasImages()) {
                return null;
            }
            List<WebImage> images = mediaMetadata.getImages();
            if (images.size() == 1) {
                return images.get(0);
            } else {
                if (imageHints.getType() == ImagePicker.IMAGE_TYPE_MEDIA_ROUTE_CONTROLLER_DIALOG_BACKGROUND) {
                    return images.get(0);
                } else {
                    return images.get(1);
                }
            }
        }
    }
}
