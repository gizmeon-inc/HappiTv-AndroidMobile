package com.happi.android.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;


public class TransparentProgressDialog extends Dialog {

    private ImageView img;

    public TransparentProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);
        setTitle(null);
        setCancelable(true);
        setOnCancelListener(null);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        img = new ImageView(context);
        img.setImageResource(themeResId);
        linearLayout.addView(img, layoutParams);
        addContentView(linearLayout, params);
    }

    @Override
    public void show() {
        super.show();

        RotateAnimation animation = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF,
                .5f, Animation.RELATIVE_TO_SELF, .5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(3000);
        img.setAnimation(animation);
        img.startAnimation(animation);
    }
}
