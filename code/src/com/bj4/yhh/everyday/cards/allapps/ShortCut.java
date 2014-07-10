
package com.bj4.yhh.everyday.cards.allapps;

import android.graphics.drawable.Drawable;

public class ShortCut {
    private Drawable mIcon;

    private CharSequence mLabel;

    private String mPkg, mClz;

    public ShortCut(String pkg, String clz, CharSequence label, Drawable icon) {
        mPkg = pkg;
        mClz = clz;
        mLabel = label;
        mIcon = icon;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public CharSequence getLabel() {
        return mLabel;
    }

    public String getPackageName() {
        return mPkg;
    }

    public String getClassName() {
        return mClz;
    }
}
