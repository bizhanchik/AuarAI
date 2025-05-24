package com.bizhan.auarai.views;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class StrikethroughTextView extends AppCompatTextView {

    public StrikethroughTextView(Context context) {
        super(context);
        init();
    }

    public StrikethroughTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrikethroughTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
} 