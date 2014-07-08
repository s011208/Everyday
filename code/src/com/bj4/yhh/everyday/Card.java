
package com.bj4.yhh.everyday;

public class Card {
    public static final int CARD_TYPE_NEWS = 0;

    public static final int CARD_TYPE_WEATHER = 1;

    public static final int CARD_TYPE_APK = 2;

    private String mTitle;

    private String mContent;

    public Card(String title, String content) {
        mTitle = title;
        mContent = content;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }
}
