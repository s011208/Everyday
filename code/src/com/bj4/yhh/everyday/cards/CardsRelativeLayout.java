
package com.bj4.yhh.everyday.cards;

import com.bj4.yhh.everyday.LoaderManager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class CardsRelativeLayout extends RelativeLayout {
    private int mCardType;

    protected Context mContext;

    protected LoaderManager.Callback mCallback;

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
        mContext = context;
    }

    public void setCallback(LoaderManager.Callback cb) {
        mCallback = cb;
    }

    public void setCardType(int type) {
        mCardType = type;
    }

    public int getCardType() {
        return mCardType;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        initCompoments();
    }

    public abstract void initCompoments();

    public abstract void updateContent();
}
