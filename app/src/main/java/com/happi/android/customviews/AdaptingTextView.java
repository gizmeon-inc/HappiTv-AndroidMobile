package com.happi.android.customviews;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class AdaptingTextView extends androidx.appcompat.widget.AppCompatTextView {

    public AdaptingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AdaptingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptingTextView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // set fitting lines to prevent cut text
        int fittingLines = h / this.getLineHeight();
        if (fittingLines > 0) {
            this.setLines(fittingLines);
            this.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

}
