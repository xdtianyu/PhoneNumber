package org.xdty.phone.number.model.google;

import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class GooglePhoneNumber implements INumber {

    private String mNumber;
    private String mOperator;
    private String mProvince;

    public GooglePhoneNumber() {

    }

    protected GooglePhoneNumber(String number, String operator, String province) {
        mNumber = number;
        mOperator = operator;
        mProvince = province;
    }

    @Override
    public String getName() {
        return "";
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
        return "";
    }

    @Override
    public String getNumber() {
        return mNumber;
    }

    @Override
    public String getProvider() {
        return mOperator;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mNumber);
    }
}