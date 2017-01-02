package org.xdty.phone.number.net.cloud;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class CloudNumber implements INumber {
    private String uid;
    private String number;
    private String name;
    private String city;
    private String provider;
    private String province;
    private int type;
    private int from;
    private int count;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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
        return Type.REPORT;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }

    @Override
    public String getCity() {
        return city;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean isValid() {
        return name != null && type >= 0;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean hasGeo() {
        return false;
    }

    @Override
    public int getApiId() {
        return INumber.API_ID_CLOUD;
    }

    @Override
    public void patch(INumber i) {
        city = i.getCity();
        provider = i.getProvider();
        province = i.getProvince();
    }
}
