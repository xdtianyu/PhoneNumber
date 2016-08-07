package org.xdty.phone.number.model.common;

import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class CommonNumber implements INumber {
    private String mNumber = null;
    private String mName = null;

    public CommonNumber(String number, String name) {
        mNumber = number;
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getProvince() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.POI;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getNumber() {
        return mNumber;
    }

    @Override
    public String getProvider() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mNumber) && !TextUtils.isEmpty(mName);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasGeo() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_COMMON;
    }

    @Override
    public void patch(INumber i) {

    }
}
