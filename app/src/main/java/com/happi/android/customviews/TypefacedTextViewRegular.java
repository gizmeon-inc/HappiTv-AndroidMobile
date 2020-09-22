package com.happi.android.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.happi.android.utils.ConstantUtils;


public class TypefacedTextViewRegular extends androidx.appcompat.widget.AppCompatTextView {

    public TypefacedTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), ConstantUtils.FONT_NAME_REGULAR);
        setTypeface(typeface);
    }
}
