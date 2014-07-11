
package com.bj4.yhh.everyday.cards.allapps;

import android.graphics.drawable.Drawable;

public class ShortCut {
    private Drawable mIcon;

    private CharSequence mLabel;

    private String mPkg, mClz;

    private int mId;

    public ShortCut(String pkg, String clz, int id) {
        mPkg = pkg;
        mClz = clz;
        mId = id;
    }

    public void applyShortCutInfo(CharSequence label, Drawable icon) {
        mLabel = label;
        mIcon = icon;
    }

    public ShortCut(String pkg, String clz, CharSequence label, Drawable icon) {
        mPkg = pkg;
        mClz = clz;
        mLabel = label;
        mIcon = icon;
        mId = -1;
    }

    public boolean isEqual(String pkg, String clz) {
        return (mPkg.equals(pkg) && mClz.equals(clz));
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
