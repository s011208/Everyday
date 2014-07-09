
package com.bj4.yhh.everyday.activities;

import java.util.ArrayList;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

public class CardListAdapter extends BaseAdapter {
    private Context mContext;

    private LoaderManager mLoaderManager;

    private final ArrayList<Card> mCards = new ArrayList<Card>();

    private LayoutInflater mInflater;

    public interface RequestRefreshCallback {
        public void requestRefresh();
    }

    private RequestRefreshCallback mCallback;

    public CardListAdapter(Context context, LoaderManager lm, RequestRefreshCallback cb) {
        mContext = context;
        mLoaderManager = lm;
        mCallback = cb;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initData();
    }

    public void notifyDataSetChanged() {
        initData();
        super.notifyDataSetChanged();
    }

    private void initData() {
        mCards.clear();
        mCards.addAll(mLoaderManager.getCards());
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public Card getItem(int position) {
        return mCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.card_list_row, null);
            holder = new ViewHolder();
            holder.mLoadingProgress = (ProgressBar)convertView
                    .findViewById(R.id.card_loading_progress);
            holder.mCardContent = (FrameLayout)convertView.findViewById(R.id.card_content);
            holder.mCardSwitcher = (ViewSwitcher)convertView.findViewById(R.id.card_switcher);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final Card card = getItem(position);
        holder.mCardSwitcher.setDisplayedChild(0);
        holder.mCardContent.setTag(position);
        if (convertView.getHeight() <= 0 || convertView.getWidth() <= 0) {
            holder.mCardSwitcher.getViewTreeObserver().addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (holder.mCardSwitcher.getViewTreeObserver().isAlive()) {
                                holder.mCardSwitcher.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                            mLoaderManager.loadCardContent(card, holder.mCardContent,
                                    new LoaderManager.LoadingCardCallback() {
                                        @Override
                                        public void loadComplete(int result) {
                                            if (result == LoaderManager.LoadingCardCallback.RESULT_OK) {
                                                holder.mCardSwitcher.setDisplayedChild(1);
                                                if (mCallback != null) {
                                                    mCallback.requestRefresh();
                                                }
                                            } else if (result == LoaderManager.LoadingCardCallback.RESULT_FROM_CACHE) {
                                                holder.mCardSwitcher.setDisplayedChild(1);
                                            } else {
                                            }
                                        }
                                    }, position);
                        }
                    });
        } else {
            mLoaderManager.loadCardContent(card, holder.mCardContent,
                    new LoaderManager.LoadingCardCallback() {
                        @Override
                        public void loadComplete(int result) {
                            if (result == LoaderManager.LoadingCardCallback.RESULT_OK) {
                                holder.mCardSwitcher.setDisplayedChild(1);
                                if (mCallback != null) {
                                    mCallback.requestRefresh();
                                }
                            } else if (result == LoaderManager.LoadingCardCallback.RESULT_FROM_CACHE) {
                                holder.mCardSwitcher.setDisplayedChild(1);
                            } else {
                            }
                        }
                    }, position);
        }
        return convertView;
    }

    public static class ViewHolder {
        ProgressBar mLoadingProgress;

        FrameLayout mCardContent;

        ViewSwitcher mCardSwitcher;
    }

}
