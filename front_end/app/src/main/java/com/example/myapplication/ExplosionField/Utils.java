package com.example.myapplication.ExplosionField;

import android.content.res.Resources;

public class Utils {
    /**
     * 密度
     */
    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    public static int dp2px(int dp) {
        return Math.round(dp * DENSITY);
    }
}