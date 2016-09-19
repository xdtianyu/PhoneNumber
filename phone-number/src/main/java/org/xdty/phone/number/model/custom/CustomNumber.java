package org.xdty.phone.number.model.custom;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;
import org.xdty.phone.number.util.Utils;

public class CustomNumber implements INumber {
    String reason;
    int error_code;
    Result result;

    protected CustomNumber() {

    }

    @Override
    public String getName() {
        return result.company + " " + result.name;
    }

    @Override
    public String getProvince() {
        return result.province;
    }

    @Override
    public Type getType() {
        return Type.POI;
    }

    @Override
    public String getCity() {
        return result.city;
    }

    @Override
    public String getNumber() {
        return result.phone;
    }

    public void setNumber(String number) {
        if (result != null) {
            result.phone = number;
        }
    }

    @Override
    public String getProvider() {
        return result.provider;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return result != null && error_code == 0;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean hasGeo() {
        return !Utils.isEmpty(getCity()) || !Utils.isEmpty(getProvince());
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CUSTOM;
    }

    @Override
    public void patch(INumber i) {
        if (result != null && i != null) {
            result.city = i.getCity();
            result.provider = i.getProvider();
            result.province = i.getProvince();
        }
    }

    class Result {
        String province;
        String city;
        String provider;
        String phone;
        String name;
        String company;
        String info;
    }
}
