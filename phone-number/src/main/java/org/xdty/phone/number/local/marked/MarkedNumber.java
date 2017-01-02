package org.xdty.phone.number.local.marked;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;
import org.xdty.phone.number.util.Utils;

public class MarkedNumber implements INumber {

    private static String[] MARKED_NAMES =
            { "骚扰电话", "诈骗", "推销", "广告推销", "房产中介", "快递送餐", "诈骗电话", "外卖", "中介", "快递" };
    private String mProvince;
    private String mProvider;
    private String mCity;
    private String mNumber = null;
    private String mName = null;
    private int mMarkedType;

    public MarkedNumber(String number, int type) {
        mNumber = number;
        mName = MARKED_NAMES[type - 1];
        mMarkedType = type;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getProvince() {
        return mProvince;
    }

    @Override
    public Type getType() {
        if (mMarkedType == 6 || mMarkedType == 8 || mMarkedType == 10) {
            return Type.POI;
        } else {
            return Type.REPORT;
        }
    }

    @Override
    public String getCity() {
        return mCity;
    }

    @Override
    public String getNumber() {
        return mNumber;
    }

    @Override
    public String getProvider() {
        return mProvider;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return !Utils.isEmpty(mNumber) && !Utils.isEmpty(mName);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasGeo() {
        return !Utils.isEmpty(getCity()) || !Utils.isEmpty(getProvince());
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_MARKED;
    }

    @Override
    public void patch(INumber i) {
        mCity = i.getCity();
        mProvider = i.getProvider();
        mProvince = i.getProvince();
    }
}
