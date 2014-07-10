
package com.bj4.yhh.everyday.cards;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class CardsRelativeLayout extends RelativeLayout {
    private int mCardType;

    private static final HandlerThread sWorkerThread = new HandlerThread("Cards handler");
    static {
        sWorkerThread.setPriority(Thread.MAX_PRIORITY);
        sWorkerThread.start();
    }

    protected static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    public CardsRelativeLayout(Context context) {
        this(context, null);
    }

    public CardsRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardsRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCardType(int type) {
        mCardType = type;
    }

    public int getCardType() {
        return mCardType;
    }

    public abstract void updateContent();
}
