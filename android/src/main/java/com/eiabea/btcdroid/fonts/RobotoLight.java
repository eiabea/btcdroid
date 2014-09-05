package com.eiabea.btcdroid.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLight extends TextView {

    public RobotoLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf"));
    }

    public RobotoLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf"));
    }

    public RobotoLight(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf"));
    }

}