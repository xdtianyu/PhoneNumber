package org.xdty.phone.number.model.baidu;

import org.xdty.phone.number.model.Type;

public class BDResponse {
    BDLocation location;
    String name;
    String type;
    double count;
    String number;

    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return Type.fromString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type.toString();
    }

    public int getCount() {
        return (int) count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}