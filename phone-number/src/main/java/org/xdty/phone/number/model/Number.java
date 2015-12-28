package org.xdty.phone.number.model;

public class Number {
    Location location;
    String name;
    String type;
    double count;
    String number;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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