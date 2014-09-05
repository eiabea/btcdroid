package com.eiabea.btcdroid.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoBold extends TextView {

    public RobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf"));
    }

    public RobotoBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf"));
    }

    public RobotoBold(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf"));
    }

}