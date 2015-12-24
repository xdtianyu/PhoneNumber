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

    public String getName() {
        return name;
    }

    public Type getType() {
        return Type.fromString(type);
    }

    public int getCount() {
        return (int) count;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}