package org.xdty.phone.number.net.caller;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;
import org.xdty.phone.number.util.Utils;

public class CallerNumber implements INumber {

    String number;
    String name;
    int type;
    int source;
    int count;
    long time;

    String province;
    String provider;
    String city;

    public CallerNumber(String number) {
        this.number = number;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public Type getType() {
        if (type == 3 || type == 4 || type == 64) {
            return Type.POI;
        } else {
            return Type.REPORT;
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCallerType() {
        return type;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean hasGeo() {
        return !Utils.get().isEmpty(getCity()) || !Utils.get().isEmpty(getProvince());
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CALLER;
    }

    @Override
    public void patch(INumber i) {
        city = i.getCity();
        provider = i.getProvider();
        province = i.getProvince();
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
