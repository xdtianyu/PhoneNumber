package org.xdty.phone.number.local.offline;

import android.text.TextUtils;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;
import org.xdty.phone.number.util.Utils;

public class OfflineNumber implements INumber {
    String province;
    String city;
    String zip;
    String area;
    String operators;
    String number;

    protected OfflineNumber(String s, String n, int type) {
        String[] a = s.split("\\|");
        province = a[0];
        city = a[1];
        zip = a[2];
        area = a[3];
        number = n;
        operators = humanReadableType(type);
    }

    public static String humanReadableType(int type) {
        switch (type) {
            case 1:
                return "移动";
            case 2:
                return "联通";
            case 3:
                return "电信";
            case 4:
                return "电信虚拟运营商";
            case 5:
                return "联通虚拟运营商";
            case 6:
                return "移动虚拟运营商";
            default:
                return "未知运营商";
        }
    }

    public String toString() {
        return province + ", " + city + ", " + zip + ", " + area;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public String getProvider() {
        return operators;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(getNumber());
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasGeo() {
        return !Utils.get().isEmpty(getProvince()) || !Utils.get().isEmpty(getCity())
                || !Utils.get().isEmpty(getProvider());
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_OFFLINE;
    }

    @Override
    public void patch(INumber i) {

    }
}
