package com.happi.android.utils;

import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by gizmeon on 16-02-2018.
 */

public class CommonBindingUtils {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String imageUrl) {
        Glide.with(imageView.getContext()).load(imageUrl).into(imageView);
    }

}
