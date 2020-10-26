package com.happi.android.customviews;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class ScrollingLinearLayoutManager extends LinearLayoutManager {
    private final int duration;

    public ScrollingLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int duration) {
        super(context, orientation, reverseLayout);
        this.duration = duration;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        View firstVisibleChild = recyclerView.getChildAt(0);
        //View firstVisibleChild = recyclerView.getChildAt(position);
        int itemHeight = firstVisibleChild.getHeight();
        Log.e("SCROLL", "itemHeight>>"+itemHeight);
        int currentPosition = recyclerView.getChildLayoutPosition(firstVisibleChild);
        Log.e("SCROLL", "currentPosition>>"+currentPosition);
        int distanceInPixels = Math.abs((currentPosition - position) * itemHeight);
        Log.e("SCROLL", "distanceInPixels 1 >>"+distanceInPixels);
        if (distanceInPixels == 0) {
            distanceInPixels = (int) Math.abs(firstVisibleChild.getY());
            Log.e("SCROLL", "distanceInPixels 2 >>"+distanceInPixels);
        }
        SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext(), distanceInPixels, duration);
        smoothScroller.setTargetPosition(position);
        Log.e("SCROLL", "setTargetPosition >>"+position);
        startSmoothScroll(smoothScroller);
    }

    private class SmoothScroller extends LinearSmoothScroller {
        private final float distanceInPixels;
        private final float duration;

        public SmoothScroller(Context context, int distanceInPixels, int duration) {
            super(context);
            this.distanceInPixels = distanceInPixels;
            this.duration = duration;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return ScrollingLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float proportion = (float) dx / distanceInPixels;
            int d = (int) (duration * proportion);

            Log.e("SCROLL", ""+duration+" * "+ proportion+">>"+d);
            return (int) (duration * proportion);
        }
    }
}
