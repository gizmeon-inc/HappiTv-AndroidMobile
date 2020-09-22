package com.happi.android.customviews;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.happi.android.R;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;

public class SliderLayoutManager extends LinearLayoutManager {
    private Context context;
    private boolean hasScrolled = false;
    private OnDateSelectedListener onDateSelectedListener;
    private int orientation;
    private RecyclerView recyclerView;

    public SliderLayoutManager(Context context, int orientation, boolean reverseLayout, OnDateSelectedListener onDateSelectedListener) {
        super(context, orientation, reverseLayout);
        this.context = context;
        this.orientation = orientation;
        this.onDateSelectedListener = onDateSelectedListener;
    }
    private void scaleDownView(){

        float mid = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount() ; i++) {

            // Calculating the distance of the child from the center
            View child = getChildAt(i);
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float distanceFromCenter = Math.abs(mid - childMid);

            // The scaling formula
            double d1 = (double)(distanceFromCenter / getWidth());
            float f1 = (float) Math.sqrt(d1) *0.66f;
            float scale = 1 -f1;

            // Set scale to view
            child.setScaleX(scale);
            child.setScaleY(scale);

            if (!hasScrolled) {
                TextView firstChild = (TextView) getChildAt(0).findViewById(R.id.tv_date);
                        firstChild.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

            if (SharedPreferenceUtility.isNightMode()){
                TextView item =(TextView) child.findViewById(R.id.tv_date);
                        item.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
            else{
                TextView item = (TextView) child.findViewById(R.id.tv_date);
                        item.setTextColor(ContextCompat.getColor(context, R.color.dark_black));
            }

        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(!HappiApplication.isDatePickerClicked) {

            RecyclerView timeRecyclerView = (RecyclerView) recyclerView.getRootView().findViewById(R.id.rv_date_list);

                    // When scroll stops we notify on the selected item
                    hasScrolled = true;
            if (state == RecyclerView.SCROLL_STATE_IDLE) {

                if (timeRecyclerView != null) {
                    timeRecyclerView.setClickable(true);
                }

                // Find the closest child to the recyclerView center --> this is the selected item.
                int recyclerViewCenterX = getRecyclerViewCenterX();
                int minDistance = recyclerView.getWidth();
                int position = -1;
                for (int i = 0; i< recyclerView.getChildCount();i++) {
                    View child = recyclerView.getChildAt(i);
                    int childCenterX =
                            getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2;
                    int newDistance = Math.abs(childCenterX - recyclerViewCenterX);

                    if (newDistance < minDistance) {
                        minDistance = newDistance;
                        position = recyclerView.getChildLayoutPosition(child);
                    }
                }

                TextView txt = (TextView) recyclerView.getLayoutManager().findViewByPosition(position).findViewById(R.id.tv_date);
                   txt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

                // Notify on item selection
                onDateSelectedListener.onDateSelected(position);
            } else if (state == RecyclerView.SCROLL_STATE_DRAGGING) {

                if (timeRecyclerView != null) {
                    timeRecyclerView.setClickable(false);
                }

            }
        }
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        recyclerView = view;

        //Smart snapping
        recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scaleDownView();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
       // return super.scrollHorizontallyBy(dx, recycler, state);
        if (orientation == HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            scaleDownView();
            return scrolled;
        } else {
            return 0;
        }
    }
    private int getRecyclerViewCenterX() {
        return (recyclerView.getRight() - recyclerView.getLeft()) / 2 + recyclerView.getLeft();
    }
    public interface OnDateSelectedListener{
        void onDateSelected(int layoutPosition);
    }
}
