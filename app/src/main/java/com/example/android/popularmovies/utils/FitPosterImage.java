package com.example.android.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Created by Kane on 07/03/2018.
 * Customized ImageView to display the poster at proper scales.
 * Adapted the code from https://stackoverflow.com/a/15264039/9459146
 */

public class FitPosterImage extends AppCompatImageView {
    public FitPosterImage(Context context) {
        super(context);
    }

    public FitPosterImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitPosterImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Poster image is 185x278 (so height is roughly 1.5 times the width)
        int newHeight = (int) Math.round(getMeasuredWidth() * 1.5);
        setMeasuredDimension(getMeasuredWidth(), newHeight);
    }
}
