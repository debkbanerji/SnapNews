package com.example.SnapNews;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.StackView;

/**
 * Created by Deb Banerji on 26-Mar-16.
 */
public class TouchProofStackView extends StackView {
    public TouchProofStackView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //do nothing
        return false;
    }
}
