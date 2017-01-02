package org.xdty.phone.number.local.mvno;

import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;
import org.xdty.phone.number.util.Utils;

public class MvnoNumber implements INumber {
    private String mNumber = null;
    private String mProvince = null;
    private String mCity = null;
    private String mProvider = null;

    public MvnoNumber(String number, String province, String city, String provider) {
        mNumber = number;
        mProvince = province;
        mCity = city;
        mProvider = provider;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getProvince() {
        return mProvince;
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
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
        return !TextUtils.isEmpty(mNumber);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasGeo() {
        return !Utils.isEmpty(getProvince()) || !Utils.isEmpty(getCity()) || !Utils.isEmpty(
                getProvider());
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_MVNP;
    }

    @Override
    public void patch(INumber i) {

    }
}
