package com.happi.android.customviews;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class SpeedyLinearLayoutManager extends LinearLayoutManager {

    //private static final float MILLISECONDS_PER_INCH = 5f; //default is 25f (bigger = slower)
    private static final float MILLISECONDS_PER_INCH = 150f; //default is 25f (bigger = slower)
    private final float MILLISECONDS_PER_PX;

    public SpeedyLinearLayoutManager(Context context) {
        super(context);
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    public SpeedyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
    public SpeedyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

        final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return super.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {

                float acc = MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                Log.e("SCROLL","ACC>>"+acc);
                return acc;
            }
            protected int calculateTimeForScrolling(int dx) {
                // In a case where dx is very small, rounding may return 0 although dx > 0.
                // To avoid that issue, ceil the result so that if dx > 0, we'll always return positive
                // time.
                return (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX);
            }
            protected int calculateTimeForDeceleration(int dx) {
                // we want to cover same area with the linear interpolator for the first 10% of the
                // interpolation. After that, deceleration will take control.
                // area under curve (1-(1-x)^2) can be calculated as (1 - x/3) * x * x
                // which gives 0.100028 when x = .3356
                // this is why we divide linear scrolling time with .3356
                int dec = (int) Math.ceil(calculateTimeForScrolling(dx) / .3356);
                Log.e("SCROLL","DEC>>"+dec);
                return  dec;
            }
        };

        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }
}
