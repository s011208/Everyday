
package com.bj4.yhh.everyday;

public class Card {
    public static final int CARD_TYPE_NEWS = 0;

    public static final int CARD_TYPE_WEATHER = 1;

    public static final int CARD_TYPE_APK = 2;

    public static final int ORDER_NONE = 9999;

    public static final int ID_NONE = -1;

    private int mOrder;

    private int mType;

    private int mId = ID_NONE;

    public Card(int id, int type, int order) {
        mType = type;
        mId = id;
    }

    public int getOrder() {
        return mOrder;
    }

    public int getType() {
        return mType;
    }

    public int getId() {
        return mId;
    }
}
