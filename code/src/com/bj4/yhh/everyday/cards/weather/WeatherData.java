
package com.bj4.yhh.everyday.cards.weather;

public class WeatherData {
    int mCityId;

    String mCityName;

    String mNationName;

    long mSunSet;

    long mSunRise;

    long mCurrentTempature;

    long mMaxTempature;

    long mMinTempature;

    int mHumidity;

    int mConditionCode;

    String mConditionDescription;

    String mConditionIcon;
    
    int mWind;

    public WeatherData(long currentTemp, long maxTemp, long minTemp, int humidity, long sunset,
            long sunrise, int cityid, String cityName, String nationName, int conditionCode,
            String conditionDes, String conditionIcon, int wind) {
        mCurrentTempature = currentTemp;
        mMaxTempature = maxTemp;
        mMinTempature = minTemp;
        mHumidity = humidity;
        mSunSet = sunset;
        mSunRise = sunrise;
        mCityId = cityid;
        mCityName = cityName;
        mNationName = nationName;
        mConditionCode = conditionCode;
        mConditionDescription = conditionDes;
        mConditionIcon = conditionIcon;
        mWind = wind;
    }

    @Override
    public String toString() {
        return "temp: " + mCurrentTempature + ", max temp: " + mMaxTempature + "min temp: "
                + mMinTempature + ", humidity: " + mHumidity + ", sunset: " + mSunSet
                + ", sunrise: " + mSunRise + ", cityId: " + mCityId + ", cityName: " + mCityName
                + ", nation: " + mNationName + ", cond code: " + mConditionCode + ", cond des: "
                + mConditionDescription + ", cond icon: " + mConditionIcon + ", wind: " + mWind;
    }
}
