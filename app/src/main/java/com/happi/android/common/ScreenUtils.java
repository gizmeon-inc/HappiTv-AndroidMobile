package com.happi.android.common;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ScreenUtils extends Activity {

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    public static int dpToPx(Context context, int value) {
        float fl = (float) value;
        int ft =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fl, context.getResources().getDisplayMetrics());
        return ft;
    }
}
