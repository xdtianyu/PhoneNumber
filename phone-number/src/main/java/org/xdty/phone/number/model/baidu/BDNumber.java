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
    public int getApiId() {
        return INumber.API_ID_BD;
    }

}
