
package com.bj4.yhh.everyday.cards.playstore.recommend;

public class PlayStoreItem {
    private String mAppUrl;

    private String mImgUrl;

    private String mListType;

    private String mAppName;

    public PlayStoreItem(String aUrl, String iUrl, String type, String aName) {
        mAppUrl = aUrl;
        mImgUrl = iUrl;
        mListType = type;
        mAppName = aName;
    }

    public String getAppUrl() {
        return mAppUrl;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public String getListType() {
        return mListType;
    }

    public String getAppName() {
        return mAppName;
    }
}
