package org.xdty.phone.number.model.baidu;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class BDNumber implements INumber {

    private BDResponse mBDResponse;
    private BDLocation mBDLocation;

    protected BDNumber(BDResponse bdResponse, String number) {
        mBDResponse = bdResponse;
        mBDLocation = mBDResponse.getLocation();
        mBDResponse.setNumber(number);

        if (mBDResponse != null && mBDResponse.getName() != null &&
                mBDResponse.getName().contains("百度糯米")) {
            mBDResponse.setName("");
            mBDResponse.setCount(0);
        }
    }

    @Override
    public String getName() {
        return mBDResponse.getName();
    }

    @Override
    public String getProvince() {
        return mBDLocation == null ? "" : mBDLocation.province;
    }

    @Override
    public Type getType() {
        return mBDResponse.getType();
    }

    @Override
    public String getCity() {
        return mBDLocation == null ? "" : mBDLocation.city;
    }

    @Override
    public String getNumber() {
        return mBDResponse.getNumber();
    }

    @Override
    public String getProvider() {
        return mBDLocation == null ? "" : mBDLocation.operators;
    }

    @Override
    public int getCount() {
        return mBDResponse.getCount();
    }

    @Override
    public boolean isValid() {
        return mBDResponse != null;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean hasGeo() {
        return getCity() != null || getProvince() != null;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_BD_DEAD;
    }

    @Override
    public void patch(INumber i) {

    }

}
