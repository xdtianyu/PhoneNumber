package org.xdty.phone.number.model.caller;

import org.xdty.phone.number.model.INumber;
import org.xdty.phone.number.model.Type;

public class CallerNumber implements INumber {

    String number;
    String name;
    int type;
    int source;
    int count;
    long time;

    public CallerNumber(String number) {
        this.number = number;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvince() {
        return null;
    }

    @Override
    public Type getType() {
        if (type == 3 || type == 4 || type == 64) {
            return Type.POI;
        } else {
            return Type.REPORT;
        }
    }

    public int getCallerType() {
        return type;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public String getProvider() {
        return null;
    }

    @Override
    public int getCount() {
        return count;
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
    public int getApiId() {
        return INumber.API_ID_CALLER;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
